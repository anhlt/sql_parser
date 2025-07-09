/**
  * -----------------------------------------------------------------------------
  *  Pipe-SQL Grammar – Annotated Walk-Through (Generated Documentation)
  * -----------------------------------------------------------------------------
  *  This file is *not* part of the compiled logic.  It exists purely as rich
  *  inline documentation explaining the structure and intent of the actual
  *  implementation found in `parser.scala`.
  *
  *  The commentary is organised in the same order as the real code so you can
  *  scroll both files side-by-side and quickly understand what each line does.
  *
  *  ────────────────────────────────────────────────────────────────────────────
  *  Legend
  *  ──────
  *  ▸ Code tokens are shown using the same syntax as the original source.
  *  ▸ Wrapped text blocks explain *why* that line exists or *how* it works.
  *  ▸ No executable statements are declared here – the Scala compiler will treat
  *    this file as empty after stripping comments.
  * ---------------------------------------------------------------------------*/

/*
package pipesql

import fastparse._, NoWhitespace._

object PipeSqlParser {          // ➤ All grammar combinators live here

  //──────────────────────── Public entry point ───────────────────────────────
  def parse(sql: String): Parsed[Seq[PipeOp]] =
    parseInput(sql)              // Delegates to the private top-level parser

  //──────────────────────── Low-level lexical helpers ────────────────────────

  private def ws[_: P]: P[Unit] =
    P(CharsWhileIn(" \t\n\r").rep)  // Matches *any* amount of ASCII whitespace

  /** kw – generic *keyword* matcher
    *   • IgnoreCase(s): letters matched case-insensitively
    *   • !CharIn(...):  negative look-ahead ensuring word-boundary so that
    *                    FROMX does *not* match FROM
    *   • ws:            eagerly consumes trailing whitespace making downstream
    *                    rules easier to write (they can assume a clean slate)
    */
  def kw[_: P](s: String): P[Unit] =
    P(IgnoreCase(s) ~ !CharIn("a-zA-Z0-9_") ~ ws)

  // ident, stringLit, numberLit, parenExpr – standard SQL atoms
  // -----------------------------------------------------------
  // ident     → table or column names (letters/_, then alphanumerics/_)
  // stringLit → single-quoted strings mapped to StringLit case class
  // numberLit → digit sequences mapped to NumberLit(Double)
  // parenExpr → parenthesised sub-expressions used for precedence grouping

  //──────────────────────── Comparison / boolean expressions ─────────────────

  // exprTerm  → the *atom* level (identifier, literal, or parenthesis)
  // inExpr    → lhs IN (expr, expr, …)     → In(lhs, list)
  // notEq     → lhs != rhs                 → NotEqual(lhs, rhs)
  // simpleExpr→ ordered alternation; try the *more specific* patterns first
  // conjExpr  → simpleExpr (AND simpleExpr)* folded into left-nested And tree

  //──────────────────────── Aggregate clause helpers ─────────────────────────

  // aggFunc   → case-insensitive function names (COUNT | SUM | …)
  // star      → literal "*" (kept separate for clarity)
  // aggArg    → Option[Expr] where None encodes COUNT(*)
  // aggExpr   → FUNC(arg) [AS alias]       → AggExpr(fn, argOpt, aliasOpt)

  //──────────────────────── Pipe operations (clauses) ────────────────────────
  // fromOp      → FROM <ident>
  // whereOp     → WHERE <boolean expr>
  // aggregateOp → AGGREGATE <aggExpr(, …)> GROUP BY <ident(, …)>
  // orderByOp   → ORDER BY <ident> [ASC|DESC]
  //               – optional whitespace tolerated before/after direction
  //               – direction absent or ASC ⇒ ascending = true
  //               – DESC                      ⇒ ascending = false

  //──────────────────────── Statement assembly ───────────────────────────────
  // pipeSep   →  "|>" with surrounding optional whitespace
  // pipeOp    →  alternation of the clause parsers above
  // statement →  pipeOp (|> pipeOp)* <ws> ";" <ws> End
  //              – Input *must* end with a semicolon then EOF.

  //──────────────────────── parseInput helper ────────────────────────────────
  // Trims leading/trailing whitespace then calls FastParse with the statement
  // parser.  The result is bubbled up unchanged to the public `parse` method.

}
*/