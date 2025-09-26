package com.rockthejvm.part3InsertionSort

object InsertionSort {

  def insertionSort(arr: Array[Int]): Array[Int] = {
    // 5
    // 3, 6, 4, 7 , 2

    // key = 4
    //  3 6 6 7 2
    // 3 4 6 7 2

    val n = arr.length
    for (i <- 1 until n) {
      val key = arr(i)
      var j = i -1
      // Shift elements greater than key to the right
      while (j >= 0 && arr(j) > key) {
        arr(j + 1) = arr(j)
        j -= 1
      }
      arr(j + 1) = key
    }

    arr
  }

  def main(args: Array[String]): Unit = {

  }
}
