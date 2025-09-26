package com.rockthejvm.part2oop

import java.time.Instant
import scala.util.Try

/**
 * OOP + FP Concepts in Scala 3 (Enterprise-flavored)
 *
 * What you'll see below:
 * - Classes, case classes, companion objects
 * - Traits (why they're richer than interfaces), multiple inheritance via mixins, stackable behavior
 * - Sealed ADTs (algebraic data types) for domain modeling
 * - Scala 3 enums vs sealed traits
 * - Pattern matching everywhere
 * - Type classes (a common FP technique) using 'given'/'using' + extension methods
 * - Smart constructors idea (validate at the boundary)
 * - Error modeling with Either instead of exceptions
 * - A small "service layer" example with dependency inversion (Logger & PaymentGateway)
 * - A 'main' that runs a tiny scenario end-to-end
 */

object OOPFpConcepts {
  // ----------------------------
  // 1) DOMAIN MODELS (immutable by default for safer concurrency)
  // ----------------------------

  // Case classes are Scala's "record/data classes":
  // - value-based equality (equals/hashCode) + nice toString
  // - pattern-matchable (auto-unapply)
  // - with copy(...) to derive modified instances immutably
  final case class User(id: String, name: String, email: String)

  final case class OrderItem(sku: String, qty: Int, unitPrice: BigDecimal):
    def lineTotal: BigDecimal = unitPrice * qty

  // Scala 3 enums (closed set of values) — great for simple states.
  enum OrderStatus:
    case New, Paid, Shipped, Cancelled

  final case class Order(id: String, user: User, items: List[OrderItem], status: OrderStatus):
    def total: BigDecimal = items.map(_.lineTotal).sum

  // ----------------------------
  // 2) TRAITS (richer than interfaces)
  // ----------------------------
  // Traits are like interfaces *with* concrete methods and even state if you want.
  // You can mix multiple traits into the same class (multiple inheritance of behavior),
  // and use "stackable modifications" with 'abstract override' + linearization.
  trait Logger:
    def log(level: String, msg: String): Unit

    // Default methods (provide behavior without forcing each implementor to re-implement)
    def info(msg: String): Unit = log("INFO", msg)

    def warn(msg: String): Unit = log("WARN", msg)

    def error(msg: String): Unit = log("ERROR", msg)

    // A concrete Logger that prints to console
  trait ConsoleLogger extends Logger:
    override def log(level: String, msg: String): Unit =
      println(s"[$level] $msg")

    // A stackable behavior that timestamps messages.
    // 'abstract override' means: must be mixed into something that provides a concrete 'log' in the chain.
  trait Timestamped extends Logger:
    private def now(): Instant = Instant.now()

    abstract override def log(level: String, msg: String): Unit =
      super.log(level, s"${now()} - $msg")

    // Another stackable behavior: redacts secrets in the message
  trait Redacting extends Logger:
    private val secretPatterns = List("\\b\\d{16}\\b".r, "\\bCVV=\\d{3}\\b".r) // silly demo

    private def redact(s: String): String =
      secretPatterns.foldLeft(s)((acc, re) => re.replaceAllIn(acc, "***REDACTED***"))

    abstract override def log(level: String, msg: String): Unit =
      super.log(level, redact(msg))

  // ----------------------------
  // 3) SEALED ADTs for explicit domain modeling (sum types)
  // ----------------------------
  // Sealed traits + case classes give you closed hierarchies:
  // the compiler can warn you on non-exhaustive pattern matches (big enterprise win!).

  sealed trait PaymentMethod

  object PaymentMethod:
    final case class Card(number: String, cvv: String, holder: String) extends PaymentMethod
    final case class PayPal(email: String) extends PaymentMethod
    case object CashOnDelivery extends PaymentMethod

  sealed trait PaymentError

  object PaymentError:
    final case class Validation(msg: String) extends PaymentError
    final case class Network(msg: String) extends PaymentError
    final case class Rejected(code: String) extends PaymentError

  final case class Receipt(orderId: String, amount: BigDecimal, reference: String)

  // ----------------------------
  // 4) COMPANION OBJECTS (no 'static' in Scala)
  // ----------------------------
  // Put factory methods, smart constructors, and helpers here.
  object Order:
    def empty(user: User): Order =
      Order(id = java.util.UUID.randomUUID().toString, user, items = Nil, status = OrderStatus.New)

  // "Smart constructor" style for validated emails (example boundary validation).
  // In real apps you'd use proper libs; this just shows the pattern.
  object Email:
    def validate(raw: String): Either[String, String] =
      if raw.contains("@") then Right(raw.trim)
      else Left("Invalid email")

  // ----------------------------
  // 5) SERVICE ABSTRACTION (Dependency Inversion via traits)
  // ----------------------------
  trait PaymentGateway:
    def charge(order: Order, method: PaymentMethod): Either[PaymentError, Receipt]

  // A fake gateway for demo/testing. We "depend" on a Logger using Scala 3 context params.
  final class FakePaymentGateway(using logger: Logger) extends PaymentGateway:
    private def validate(method: PaymentMethod): Either[PaymentError, Unit] =
      method match
        case PaymentMethod.Card(number, cvv, holder) =>
          val num = number.replaceAll("\\s", "")
          if num.length < 12 then Left(PaymentError.Validation("Card number too short"))
          else if cvv.length != 3 then Left(PaymentError.Validation("CVV must be 3 digits"))
          else if holder.trim.isEmpty then Left(PaymentError.Validation("Empty holder"))
          else Right(())
        case PaymentMethod.PayPal(email) =>
          Email.validate(email).left.map(PaymentError.Validation.apply).map(_ => ())
        case PaymentMethod.CashOnDelivery =>
          Right(())

    override def charge(order: Order, method: PaymentMethod): Either[PaymentError, Receipt] =
      val amount = order.total
      logger.info(s"Charging order ${order.id} amount=$amount via $method")
      if amount <= 0 then
        val e = PaymentError.Validation("Amount must be > 0")
        logger.warn(e.toString)
        Left(e)
      else
        validate(method) match
          case Left(err) =>
            logger.error(s"Validation failed: $err")
            Left(err)
          case Right(_) =>
            // Fake some non-determinism:
            val approved = (order.id.hashCode + amount.hashCode()) % 5 != 0
            if approved then
              val ref = java.util.UUID.randomUUID().toString
              val receipt = Receipt(order.id, amount, ref)
              logger.info(s"Approved: $receipt")
              Right(receipt)
            else
              val err = PaymentError.Rejected("DO_NOT_HONOR")
              logger.error(s"Rejected: $err")
              Left(err)

    // ----------------------------
    // 6) TYPE CLASSES (FP pattern) — pretty printing without changing the types
    // ----------------------------
    // Type class = behavior defined outside the data type, resolved by the compiler.
    // Great for cross-cutting concerns (JSON, Show, Orderings, etc.) without inheritance.

  trait Show[-A]:
    extension (a: A) def show: String

  object Show:
    // Derive tiny "pretty" printers for our domain
    given Show[OrderItem] with
      extension (i: OrderItem) def show: String =
        s"Item(${i.sku}, qty=${i.qty}, unit=${i.unitPrice}, total=${i.lineTotal})"

    given Show[Order] with
      extension (o: Order) def show: String =
        val items = if o.items.isEmpty then "[]"
        else o.items.map(_.show).mkString("[", ", ", "]")
        s"Order(id=${o.id}, user=${o.user.name}, status=${o.status}, total=${o.total}, items=$items)"

    given Show[Receipt] with
      extension (r: Receipt) def show: String =
        s"Receipt(order=${r.orderId}, amount=${r.amount}, ref=${r.reference})"

    // Helper syntax to summon a given Show
    def apply[A](using sh: Show[A]): Show[A] = sh

  // ----------------------------
  // 7) PATTERN MATCHING EVERYWHERE (exhaustive on sealed hierarchies)
  // ----------------------------
  // Example: map a PaymentError to an HTTP-ish status/message (think controller layer).
  def toHttp(err: PaymentError): (Int, String) =
    err match
      case PaymentError.Validation(msg) => 400 -> s"Bad Request: $msg"
      case PaymentError.Network(msg) => 502 -> s"Bad Gateway: $msg"
      case PaymentError.Rejected(code) => 402 -> s"Payment Required: $code"

  def main(args: Array[String]): Unit = {
    // Compose a Logger with stackable behaviors:
    // ConsoleLogger -> Timestamped -> Redacting
    given Logger = new ConsoleLogger with Timestamped with Redacting {}

    // Build some domain data (immutable)
    val user = User(
      id = "u-001",
      name = "BigBoss",
      email = Email.validate("boss@corp.io").getOrElse("boss@invalid") // unsafe unwrap for demo
    )

    val order = Order(
      id = "o-1001",
      user = user,
      items = List(
        OrderItem("SKU-CHAIR", 2, BigDecimal("49.99")),
        OrderItem("SKU-DESK", 1, BigDecimal("199.00"))
      ),
      status = OrderStatus.New
    )

    // Show case-class goodies
    println("== Case class goodies ==")
    val order2 = order.copy(status = OrderStatus.Paid) // immutably derive a modified version
    println(s"order == order2? ${order == order2}") // false (status differs)
    println(s"order total: ${order.total}")
    println(s"order2 total: ${order2.total}")

    // Type class pretty-print
    import Show.given
    println("== Pretty printing via type class ==")
    println(order.show)

    // Pattern matching on enums (exhaustive)
    def nextStatus(s: OrderStatus): OrderStatus = s match
      case OrderStatus.New => OrderStatus.Paid
      case OrderStatus.Paid => OrderStatus.Shipped
      case OrderStatus.Shipped => OrderStatus.Shipped
      case OrderStatus.Cancelled => OrderStatus.Cancelled

    println("== Enum pattern match ==")
    println(s"Next status after ${order.status} is ${nextStatus(order.status)}")

    // Service abstraction + DI via 'using'
    val gateway = new FakePaymentGateway() // uses the given Logger above

    // Try 1: charge with a valid-ish card (16 digits)
    val method1 = PaymentMethod.Card(number = "4111 1111 1111 1111", cvv = "123", holder = "Big Boss")
    println("== Charge attempt #1 (Card) ==")
    gateway.charge(order2, method1) match
      case Right(receipt) =>
        println(s"SUCCESS -> ${receipt.show}")
      case Left(err) =>
        val (code, msg) = toHttp(err)
        println(s"FAIL ($code) -> $msg")

    // Try 2: charge with an invalid card (redaction demo: number will be masked in logs)
    val method2 = PaymentMethod.Card(number = "1234 5678 9", cvv = "99", holder = "")
    println("== Charge attempt #2 (Bad Card) ==")
    gateway.charge(order, method2) match
      case Right(receipt) =>
        println(s"UNEXPECTED SUCCESS -> ${receipt.show}")
      case Left(err) =>
        val (code, msg) = toHttp(err)
        println(s"EXPECTED FAIL ($code) -> $msg")

    // Try 3: PayPal path (smart constructor demo)
    val method3 = PaymentMethod.PayPal(email = "not-an-email") // invalid
    println("== Charge attempt #3 (Bad PayPal) ==")
    gateway.charge(order2, method3) match
      case Right(receipt) =>
        println(s"UNEXPECTED SUCCESS -> ${receipt.show}")
      case Left(err) =>
        val (code, msg) = toHttp(err)
        println(s"EXPECTED FAIL ($code) -> $msg")

    // Pattern-match on ADTs for reporting
    println("== ADT pattern matching demo ==")

    def methodName(pm: PaymentMethod): String = pm match
      case PaymentMethod.Card(_, _, holder) => s"Card(holder=$holder)"
      case PaymentMethod.PayPal(email) => s"PayPal($email)"
      case PaymentMethod.CashOnDelivery => "CashOnDelivery"

    List(method1, method2, method3).foreach(m => println(methodName(m)))

    // Bonus: tiny Try example (interop with exceptions without blowing up your app)
    println("== Try demo ==")
    val parsed: Try[Int] = Try("123x".toInt)
    println(parsed) // Failure(java.lang.NumberFormatException: For input string: "123x")

    println("== Done ==")
  }
}

/*
KEY TAKEAWAYS (enterprise lens):

- Prefer immutable case classes for your domain; use .copy(...) for changes.
- Model states & variants explicitly with enums/sealed traits; pattern match exhaustively.
- Use traits for behavior: default methods, multiple inheritance via mixins, stackable behavior with 'abstract override'.
- There’s no 'static': use companion objects for factories, smart constructors, constants.
- Push errors to the type level with Either/ADT instead of throwing everywhere.
- Type classes (given/using + extension) let you add behavior without modifying types or inheritance chains.
- Keep side-effects at the edges (e.g., logging/IO), keep core pure when possible.

This style scales better for testing, reasoning, and maintenance in large codebases.
*/