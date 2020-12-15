package caliban.parallelism

import caliban.schema.GenericSchema
import zio.query.ZQuery

object Schema extends GenericSchema[Any] {
  type Query[A] = ZQuery[Any, Throwable, A]

  case class Root(entities: Query[List[Entity]])
  case class Entity(id: Int, counter: Query[Int])

  val root100: Root = root(100)
  val root1000: Root = root(1000)
  val root10000: Root = root(10000)

  private def root(n: Int) = {
    val entities = (1 to n).map(i => Entity(i, ZQuery.succeed(i))).toList
    Root(ZQuery.succeed(entities))
  }
}
