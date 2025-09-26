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


  def main(args: Array[String]): Unit = {
    println(s"ISPrime= ${isPrime(1709)}")
  }
}
