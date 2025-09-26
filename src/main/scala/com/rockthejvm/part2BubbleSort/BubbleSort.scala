package com.rockthejvm.part2BubbleSort

import scala.util.Random

object BubbleSort {
  /*
  * * Bubble sort algorithm.
   *
   * Idea:
   * - Repeatedly step through the list
   * - Compare each pair of adjacent elements
   * - Swap them if they are in the wrong order
   * - Each pass "bubbles up" the largest element to the end
   * - Repeat until no swaps are needed (list is sorted)
   *
   * Time complexity:
   * - Worst case: O(n^2)
   * - Best case (already sorted, optimized version): O(n)
   * - Space complexity: O(1) (in-place sort)*/
  // val nums: List[Int] = List(); // Immutable
  // val mutableList: Array[Int] = Array(); // mutable, genearte as (2 to 20 by 2).toArray

  def sort(arr: Array[Int]): Array[Int] = {
    val count = arr.length
    var swapped = true

    while (swapped) {
      swapped = false
      for (i <- 0 until count - 1) { // equivalent to int i = 0; i < count; i++
        // foreach: nums.foreach(n => println(n)
        // comprehensions for, : for (n <- nums if n% 2 == 0) yield n * 2; val nums = 1 to 5
        if (arr(i) > arr(i + 1)) {
          // Swap adjacent elements // print : println(s"i = $i")
          val temp = arr(i)
          arr(i) = arr(i + 1)
          arr(i + 1) = temp
          swapped = true
        }
      }
    }
    arr
  }

  def main(args: Array[String]): Unit = {
    val nums: Array[Int] = Array.fill(10)(Random.nextInt(100) + 1)

    println("Before sorting: " + nums.mkString(", "))
    println("After sorting:  " + sort(nums.clone()).mkString(", ")) // clone to make the original unsorted
  }
}
