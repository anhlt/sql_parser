package pipesql

import fastparse._, NoWhitespace._

object PipeSqlParser {
// public API
def parse(sql: String): Parsed[Seq[PipeOp]] = parseInput(sql)
  // generic whitespace parser (spaces, tabs, newlines)
  private def ws[_: P]: P[Unit] = P( CharsWhileIn(" \t\n\r").rep )

  // match keyword followed by word-boundary, then consume trailing whitespace
  def kw[_: P](s: String): P[Unit] = P(IgnoreCase(s) ~ !CharIn("a-zA-Z0-9_") ~ ws)
  def ident[_: P]: P[String] = P( CharIn("a-zA-Z_") ~ CharsWhileIn("a-zA-Z0-9_", 0) ).!
  def stringLit[_: P]: P[StringLit] = P("'" ~/ CharsWhile(_ != '\'', 0).! ~ "'").map(StringLit)
  def numberLit[_: P]: P[NumberLit] = P( CharIn("0-9").rep(1).! ).map(s => NumberLit(s.toDouble))
  // allow parentheses around expressions to handle precedence in future
  def parenExpr[_: P]: P[Expr] = P("(" ~/ conjExpr ~ ")")

  def exprTerm[_: P]: P[Expr] = P( stringLit | numberLit | parenExpr | ident.map(Identifier) )
  // IN list comparison with optional whitespace after commas
    def inExpr[_: P]: P[Expr] = P(
  exprTerm ~ ws ~ IgnoreCase("IN") ~/ ws ~ "(" ~/ exprTerm.rep(1, sep = "," ~ ws) ~ ")"
  ).map {
  case (lhs, list) => In(lhs, list)
  }
  // not-equal comparison, tolerate spaces around the operator
  def notEq[_: P]: P[Expr] = P(exprTerm ~ ws ~ "!=" ~ ws ~ exprTerm).map {
  case (l, r) => NotEqual(l, r)
  }
  // Order matters: try the most specific patterns first
  def simpleExpr[_: P]: P[Expr] = P( inExpr | notEq | exprTerm )

  // Conjunction of expressions separated by AND keyword
  def conjExpr[_: P]: P[Expr] = P(
    simpleExpr ~ (ws ~ IgnoreCase("AND") ~ ws ~/ simpleExpr).rep
  ).map {
  case (first, rest) => rest.foldLeft(first) { case (acc, e) => And(acc, e) }
  }

  // Aggregate pieces
  private def aggFunc[_: P]: P[String] = P( StringInIgnoreCase("COUNT", "SUM", "AVG", "MIN", "MAX") ).!
  def star[_: P]: P[Unit] = P("*")
  def aggArg[_: P]: P[Option[Expr]] = P( star.?.map(_ => None) | exprTerm.map(Some(_)) )
  // Aggregate expression: allow optional whitespace before the optional "AS alias"
    def aggExpr[_: P]: P[AggExpr] = P(
  aggFunc ~ "(" ~/ (star.map(_ => None) | exprTerm.map(Some(_))) ~ ")" ~
  ws ~ (IgnoreCase("AS") ~/ ws ~ ident).?
  ).map {
  case (fn, arg, alias) => AggExpr(fn.toUpperCase, arg, alias)
  }

  def fromOp[_: P]: P[From] = P( kw("FROM") ~/ ident ).map(From)
  def whereOp[_: P]: P[Where] = P( kw("WHERE") ~/ conjExpr ).map(Where)
  // AGGREGATE clause: allow optional whitespace after commas between aggregate expressions
    def aggregateOp[_: P]: P[Aggregate] = P(
  kw("AGGREGATE") ~/ aggExpr.rep(1, sep = "," ~ ws) ~
  ws ~ kw("GROUP") ~ kw("BY") ~/ ident.rep(1, sep = "," ~ ws)
  ).map {
  case (aggs, group) => Aggregate(aggs, group)
  }
  // ORDER BY clause: tolerate whitespace before the direction keyword and optionally after it
    def orderByOp[_: P]: P[OrderBy] = P(
  kw("ORDER") ~ kw("BY") ~/ ident ~ ws.? ~ (IgnoreCase("ASC") | IgnoreCase("DESC")).!.? ~ ws.?
  ).map { case (col, dirOpt) =>
  // default direction = ASC (true) unless an explicit DESC appears
  OrderBy(Seq(col -> dirOpt.forall(_.toUpperCase != "DESC")))
  }


  def pipeSep[_: P]: P[Unit] = P( ws ~ "|>" ~ ws )
  def pipeOp[_: P]: P[PipeOp] = P( fromOp | whereOp | aggregateOp | orderByOp ).map(identity)
  def statement[_: P]: P[Seq[PipeOp]] = P( pipeOp.rep(1, sep = pipeSep) ~ ws ~ ";" ~ ws ~ End )

  // entry point: ignore leading/trailing whitespace so queries may start/end with newlines
  def parseInput(input: String): Parsed[Seq[PipeOp]] =
  fastparse.parse(input.trim, statement(_))
}