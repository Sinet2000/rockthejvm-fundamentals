package com.rockthejvm.part1Basics

object Functions {

  private def aFunction(a: String, b: Int): String =
    a + " " + b

  // invocation
  val anInvocation = aFunction("Scala", 990)

  // void Function
  def aVoidFunction(aString: String): Unit = {
    println("Void")
  }

  def main(args: Array[String]): Unit = {
    println(anInvocation)
  }
}
