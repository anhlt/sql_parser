import pipesql._
  import fastparse.Parsed

object Main extends App {
  val query = """FROM Produce
  |> WHERE
  item != 'bananas'
  AND category IN ('fruit', 'nut')
  |> AGGREGATE COUNT(*) AS num_items, SUM(sales) AS total_sales
  GROUP BY item
  |> ORDER BY item DESC;"""

  PipeSqlParser.parse(query) match {
  case Parsed.Success(value, _) =>
  println("Parsed AST:")
  println(value.mkString("\n"))
  case f: Parsed.Failure =>
  println("Parse failed: " + f)
  }
  }
