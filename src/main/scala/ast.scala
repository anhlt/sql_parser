package pipesql

// AST definitions
sealed trait PipeOp
case class From(table: String) extends PipeOp
case class Where(condition: Expr) extends PipeOp
case class Aggregate(aggs: Seq[AggExpr], groupBy: Seq[String]) extends PipeOp
case class OrderBy(items: Seq[(String, Boolean)]) extends PipeOp // Boolean: true -> ASC, false -> DESC

// Expressions
sealed trait Expr
case class Identifier(name: String) extends Expr
case class StringLit(value: String) extends Expr
case class NumberLit(value: Double) extends Expr
case class In(left: Expr, list: Seq[Expr]) extends Expr
case class NotEqual(left: Expr, right: Expr) extends Expr
case class And(left: Expr, right: Expr) extends Expr

// Aggregation expression like COUNT(*) AS cnt
case class AggExpr(func: String, arg: Option[Expr], alias: Option[String])