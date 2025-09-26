package com.rockthejvm.part1Basics

object ValuesAndTypes {

  val meaningOfLife: Int = 42
  val anInteger: Int = 67 // 4 bytes
  val aShort: Short = 5234 // 2 bytes
  val aLong: Long = 13134324234L // 8 bytes
  val aFloat: Float = 123.4f // 4bytes
  val aDouble: Double = 3.13 // 8 bytes
  private var healthAmount: Double = 34.44d
  val aChar: Char = 'a'
  healthAmount += 4

  val aString: String = "Scala Rocks"

  def main(args: Array[String]): Unit = {

  }
}
