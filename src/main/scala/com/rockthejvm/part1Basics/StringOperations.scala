package com.rockthejvm.part1Basics

object StringOperations {

  def main(args: Array[String]): Unit = {
    // 1. Concat
    val name = "BigBoss"
    println(s"Hello, $name")

    // 2. Formatting
    val piVal = f"Pi is ${math.Pi}%.2f"
    println(piVal)

    // 3. Substring
    val s = "ScalaRocks"
    println(s.substring("Scala".length, s.length))
    println(s.substring(s.indexOf("Scala"), s.length))

    // 4. SPlitting
    val splStr = "Jamie, Arthur, John, Xavier, Dutch"
    println(splStr.split(", ").mkString("|"))

    // 5. Replace & REgex
    println("123394034433-2342344 Jamie Vanderline".replaceAll("[0-9]", "#"))

    // 6. Case Mainpulation
    println("scala rocks".toUpperCase())

    // 7. Checking contents
    println("person, ID:12333".contains("ID:12333"))
  }
}
