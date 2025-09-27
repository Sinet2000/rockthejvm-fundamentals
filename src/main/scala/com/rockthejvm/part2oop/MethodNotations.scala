package com.rockthejvm.part2oop

object MethodNotations {

  class Person(val name: String, val age: Int):
    infix def likes(movie: String): String =
      s"$name likes $movie"

    // Unary operator
    def unary_! : String = s"$name says: What the heck?!"

    // No-arg method for postfix
    def isAdult: Boolean = age >= 18

    // apply method
    def apply(times: Int): String =
      s"$name watched Inception $times times"


  def main(args: Array[String]): Unit =
    val bob = new Person("Bob", 20)

    // Infix
    println(bob likes "Inception") // same as bob.likes("Inception")

    // Unary
    println(!bob) // same as bob.unary_!

    // Postfix (need import)
    // postfix notation is discouraged (because it can make code ambiguous).
    import scala.language.postfixOps
    println(bob isAdult) // same as bob.isAdult

    // Apply
    println(bob(3)) // same as bob.apply(3)

    /*
    🔧 Rule of Thumb
      Use dot notation(bob.isAdult) in real code → clearer and doesn
      ’t need imports.
        Use postfix only in DSLs or when you want to write English -like code .
      Example: Akka actors use actorRef ! message (because ! is just a
      method
      ).
     */
}
