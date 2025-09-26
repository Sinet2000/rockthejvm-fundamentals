package com.rockthejvm.part2oop

/**
 * Scala 3: given / using / apply / with / extension — quick cheatsheet
 *
 * - given: define an implicit “instance” the compiler can auto-supply (type class, config, etc.)
 * - using: mark a parameter as context/implicit and have the compiler fill it from scope
 * - apply: conventional factory/constructor method; lets you write Foo(...) instead of new Foo(...)
 * - with:
 *     (A) mixin at construction: new A with B
 *     (B) implement a given instance: given TC[T] with {...}
 * - extension: add methods to existing types without inheritance
 */
object ContextAndSyntaxCheatsheet:

  // ---------- 1) Type class + given / using ----------
  // A tiny type class: “Show” can pretty-print A as a String
  trait Show[-A]:
    extension (a: A) def show: String

  object Show:
    // given instance for Int
    given Show[Int] with
      extension (i: Int) def show: String = s"Int($i)"

    // given instance for String
    given Show[String] with
      extension (s: String) def show: String = s""""$s""""

    // helper to summon an instance explicitly (rarely needed; just for demo)
    def apply[A](using sh: Show[A]): Show[A] = sh

  // A function that REQUIRES a Show[A] in scope, but we don’t pass it explicitly:
  def printShown[A](a: A)(using Show[A]): Unit =
    // the compiler finds a matching `given Show[A]` in scope automatically
    println(a.show)

  // ---------- 2) extension methods ----------
  // Add methods to existing types (no inheritance needed)
  extension (i: Int)
    def squared: Int = i * i
    def clamp(min: Int, max: Int): Int = math.max(min, math.min(max, i))

  // ---------- 3) apply (factories / nicer construction) ----------
  final class Config private (val url: String, val poolSize: Int)
  object Config:
    // Conventional factory; lets you write Config(...) instead of new Config(...)
    def apply(url: String, poolSize: Int = 10): Config =
      new Config(url, poolSize)

  // Case classes already have an auto-generated apply:
  final case class User(name: String, age: Int) // you can do User("A", 42) without 'new'

  // ---------- 4) with (mixin + given implementation bodies) ----------
  trait Logger:
    def log(msg: String): Unit

  trait ConsoleLogger extends Logger:
    override def log(msg: String): Unit = println(s"[LOG] $msg")

  trait Timestamped extends Logger:
    abstract override def log(msg: String): Unit =
      super.log(s"${java.time.Instant.now()} | $msg")

  // A service that needs a Logger; we’ll provide one at construction using mixins.
  final class PaymentService(logger: Logger):
    def pay(amount: BigDecimal): Unit =
      logger.log(s"Charging $$amount")

  def main(args: Array[String]): Unit =
    println("== given / using demo ==")
    import Show.given // bring given instances into scope

    printShown(42)          // uses given Show[Int]
    printShown("BigBoss")   // uses given Show[String]

    println("== extension methods demo ==")
    println(5.squared)            // 25
    println(27.clamp(0, 10))      // 10

    println("== apply demo ==")
    val cfg = Config("jdbc:postgresql://db.prod:5432/app", poolSize = 20) // calls Config.apply
    val u   = User("Alice", 30)                                           // case class apply
    println(s"Config url=${cfg.url}, pool=${cfg.poolSize}")
    println(s"User: $u")

    println("== with (mixin) demo ==")
    val logger: Logger = new ConsoleLogger with Timestamped {}
    val svc = new PaymentService(logger)
    svc.pay(BigDecimal("19.99")) // logs with timestamp decorator

    println("== given ... with body demo ==")
    // You can also define a 'given' with a body using 'with'
    trait Rand[A]:
      def next(): A

    given Rand[Int] with
      private val r = new scala.util.Random()
      def next(): Int = r.nextInt()

    // Consume the given Rand[Int] via 'using'
    def drawInt()(using rnd: Rand[Int]): Int = rnd.next()
    println(s"Random draw: ${drawInt()}")

    println("== explicitly summoning a given (rare) ==")
    val stringShow = Show[String] // equivalent to summon[Show[String]]
    println(stringShow.asInstanceOf[Show[String]]) // just to show it's there

    println("== Done ==")