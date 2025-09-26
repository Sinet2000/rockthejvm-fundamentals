package com.rockthejvm.part1Basics

object Instructions {

  // instructions vs expressions
  // imperative, means what to do step-by-step: do this, do that, repeat(10) { ... }
  // 2 + 3, IO effects, map, flatMap, filter - functional programming
  // In scala, an instruction is a structure (expression) returning Unit
  val printing: Unit = println("This is an instruction")

  var aVariable = 10
  val reassignment: Unit = aVariable += 1

  def main(args: Array[String]): Unit = {
    var theNum = 1
    while (theNum <= 10) {
      println(theNum)
      theNum += 1
    }
  }
}
