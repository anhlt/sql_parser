package pipesql

import org.scalatest.funsuite.AnyFunSuite
import fastparse._

class PipeSqlParserSpec extends AnyFunSuite {

  val sampleQuery: String =
    """FROM Produce
      |> WHERE
      item != 'bananas'
      AND category IN ('fruit', 'nut')
      |> AGGREGATE COUNT(*) AS num_items, SUM(sales) AS total_sales
      GROUP BY item
      |> ORDER BY item DESC;"""

  test("parser should build expected AST for sample query") {
    val result = PipeSqlParser.parse(sampleQuery)
    assert(result.isInstanceOf[Parsed.Success[_]])
    val ops = result.asInstanceOf[Parsed.Success[Seq[PipeOp]]].value
    assert(ops.headOption.contains(From("Produce")))
    assert(ops.collect { case _: Where => 1 }.size == 1)
    assert(ops.collect { case _: Aggregate => 1 }.size == 1)
    assert(ops.collect { case _: OrderBy => 1 }.size == 1)
  }

  // Simple sanity-check: a bare FROM statement should parse successfully
  test("parser should accept simple FROM clause") {
  val q = "FROM Produce;"
  val res = PipeSqlParser.parse(q)
  assert(res.isInstanceOf[Parsed.Success[_]])
  val ops = res.asInstanceOf[Parsed.Success[Seq[PipeOp]]].value
  assert(ops == Seq(From("Produce")))
  }

  // ORDER BY variant: space between column and direction keyword
  test("parser should accept ORDER BY with space before direction keyword") {
  val q = "ORDER BY item DESC;"
  val res = PipeSqlParser.parse(q)
  assert(res.isInstanceOf[Parsed.Success[_]])
  val ops = res.asInstanceOf[Parsed.Success[Seq[PipeOp]]].value
  assert(ops == Seq(OrderBy(Seq("item" -> false)))) // false means DESC
  }

  // ORDER BY variant: newline before direction keyword
  test("parser should accept ORDER BY with newline before direction keyword") {
  val q = "ORDER BY item\nDESC;"
  val res = PipeSqlParser.parse(q)
  assert(res.isInstanceOf[Parsed.Success[_]])
  val ops = res.asInstanceOf[Parsed.Success[Seq[PipeOp]]].value
  assert(ops == Seq(OrderBy(Seq("item" -> false))))
  }
}