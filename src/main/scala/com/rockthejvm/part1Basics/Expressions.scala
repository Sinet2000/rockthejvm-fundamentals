package com.rockthejvm.part1Basics

object Expressions {

  // structures that evaluate to a value
  val justAnExpr: Float = (40 + 3.0f) / 38.0f

  // boolean expressions
  val eqTest = 1 == 2

  // if expr
  val age = 19
  val canDrink = if (age > 18) true else false
  val canSMoke = if age > 16 then true else false

  // code blocks
  val aCodeBlock = {
    val localValue = 47
    // a bunch of expression

    localValue + 99
  }

  // Scala 3 way for expressions
  private val aCodeBlock_2 =
    val localValue = 45
    localValue + 34

  // pattern matching
  private val someValue: Int = 42
  val description: String = someValue match {
    case 1 => "the first"
    case 2 => "The second"
    case _ => "out of range"
  }

  def main(args: Array[String]): Unit = {
    println(s"Value: $justAnExpr")
    println(s"Can Drink: $canDrink")
    println(s"Can Smoke: $canSMoke")
    println(s"aCodeBlock: $aCodeBlock")
    println(s"aCodeBlock_2: $aCodeBlock_2")
    println(s"description: $description")
  }
}
