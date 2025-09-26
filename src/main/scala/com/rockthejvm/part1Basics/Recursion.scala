package com.rockthejvm.part1Basics

object Recursion {

  def sum(n: Int): Int =
    if (n <= 0) 0
    else n + sum(n - 1)

  // Prime: TEST all numbers from 2 to n /2, test n & d == 0
  def isPrime(n: Int): Boolean = {
    if (n <= 1) false
    else if (n == 2) true
    else if (n % 2 == 0) false
    else {
      val limit = math.sqrt(n).toInt
      !(2 to limit).exists(i => n % i == 0)
    }
  }

  /**
   * Exercises
   * 1. concatenae a string a set number of times
   *    concatenateN("Scala", 3)= "ScalaScalaScala"
   * 2. Fibonacci numbers
   *    1, 2, 3, 5, 8, 13, 21
   */
  def concatenateNSb(str: String, n: Int): String = {
    val sb = new StringBuilder
    for (_ <- 1 to n)
      sb.append(str)

    sb.toString()
  }

  def concateNList(str: String, n: Int): String =
    List.fill(n)(str).mkString("")

  /*
  fib(5)
    - fib(4) + fib(3)
    - fib(3) = fib(2) + fib (1) = 2 + 1
   */
  def fibonacci(n: Int): Int = {
    if (n <= 1) 1
    else if (n == 2) 2
    else fibonacci(n - 1) + fibonacci(n - 2)
  }

  def fibTailRec(n: Int): Int = {
    @annotation.tailrec
    def loop(i: Int, prev: Int, curr: Int): Int = {
      if (i >= n) curr
      else loop(i + 1, curr, prev + curr)
    }

    if (n <= 0) 0 else loop(1, 0, 1)
  }

  def fibLoop(n: Int): Int = {
    if (n <= 0) return 0
    var a = 0
    var b = 1
    for (_ <- 2 to n) {
      val temp = a + b
      a = b
      b = temp
    }

    b
  }

  def main(args: Array[String]): Unit = {
    println(s"ISPrime= ${isPrime(1709)}")
    println(s"Concat: ${concatenateNSb("Scala", 3)}")
    println(s"Concat: ${concateNList("Scala", 3)}")
    println(s"Fib: ${fibonacci(6)}")
  }
}
