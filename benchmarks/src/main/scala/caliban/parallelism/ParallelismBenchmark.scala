package caliban.parallelism

import java.util.concurrent.TimeUnit

import caliban.GraphQL.graphQL
import caliban.{CalibanError, GraphQLInterpreter, RootResolver}
import org.openjdk.jmh.annotations._
import zio.{BootstrapRuntime, Runtime, ZEnv}
import zio.internal.Platform

@State(Scope.Thread)
@BenchmarkMode(Array(Mode.Throughput))
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS)
@Fork(1)
class ParallelismBenchmark {
  val query: String = """{
    entities {
      id
      counter
    }
  }""".stripMargin

  val runtime: Runtime[ZEnv] = new BootstrapRuntime {
    override val platform: Platform = Platform.benchmark
  }

  import Schema._

  val interpreter100Par: GraphQLInterpreter[Any, CalibanError] = runtime.unsafeRun(graphQL(RootResolver(Schema.root100)).interpreter)
  val interpreter1000Par: GraphQLInterpreter[Any, CalibanError] = runtime.unsafeRun(graphQL(RootResolver(Schema.root1000)).interpreter)
  val interpreter10000Par: GraphQLInterpreter[Any, CalibanError] = runtime.unsafeRun(graphQL(RootResolver(Schema.root10000)).interpreter)
  val interpreter100Seq: GraphQLInterpreter[Any, CalibanError] = runtime.unsafeRun(graphQL(RootResolver(Schema.root100), parallelism = false).interpreter)
  val interpreter1000Seq: GraphQLInterpreter[Any, CalibanError] = runtime.unsafeRun(graphQL(RootResolver(Schema.root1000), parallelism = false).interpreter)
  val interpreter10000Seq: GraphQLInterpreter[Any, CalibanError] = runtime.unsafeRun(graphQL(RootResolver(Schema.root10000), parallelism = false).interpreter)

  @Benchmark
  def parallelQuery100(): Any = {
    val io = interpreter100Par.execute(query)
    runtime.unsafeRun(io)
  }

  @Benchmark
  def parallelQuery1000(): Any = {
    val io = interpreter1000Par.execute(query)
    runtime.unsafeRun(io)
  }

  @Benchmark
  def parallelQuery10000(): Any = {
    val io = interpreter10000Par.execute(query)
    runtime.unsafeRun(io)
  }

  @Benchmark
  def sequentialQuery100(): Any = {
    val io = interpreter100Seq.execute(query)
    runtime.unsafeRun(io)
  }

  @Benchmark
  def sequentialQuery1000(): Any = {
    val io = interpreter1000Seq.execute(query)
    runtime.unsafeRun(io)
  }

  @Benchmark
  def sequentialQuery10000(): Any = {
    val io = interpreter10000Seq.execute(query)
    runtime.unsafeRun(io)
  }
}
