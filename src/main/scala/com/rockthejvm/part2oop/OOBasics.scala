package com.rockthejvm.part2oop

object Utils:
  def sayHello(name: String): Unit = println(s"Hello, $name")

object OOBasics {

  class Person(var name: String, var age: Int):
    def greet(): Unit =
      println(s"Hello $name, your age is $age")

  class OfficeWorker(name: String, age: Int, var department: String) extends Person(name, age):
    override def greet(): Unit =
      println(s"Hello $name from $department, your age is $age")


  abstract class Shape:
    def area: Double

  class Circle(var radius: Double) extends Shape:
    override def area: Double = math.Pi * math.pow(radius, 2)


  class BankAccount(var startBalance: Double = 0.0):
    private var balance: Double = startBalance
    def deposit(amount: Double): Unit = balance += amount

  // static + companion object
  object BankAccount:
    def createDefault(): BankAccount = new BankAccount(100.0)

  // case class. generates equals, hashCode, toString and copy, like records in c#
  // Immutable by default
  case class Point(x: Int, y: Int)

  def main(args: Array[String]): Unit = {
    val person = new Person("John", 35)
    person.greet()
    Utils.sayHello(person.name)

    var officeWorker = new OfficeWorker("Andrw", 23, "OPS")
    officeWorker.greet()

    var circle = new Circle(35.6)
    println(f"Circle area: ${circle.area}%.2f")

    var bac = BankAccount.createDefault()
  }

}
