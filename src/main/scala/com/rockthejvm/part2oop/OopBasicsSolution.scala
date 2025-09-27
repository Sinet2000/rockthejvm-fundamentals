package com.rockthejvm.part2oop

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, Period, Year}

object OopBasicsSolution {
  trait HasFullName:
    def firstName: String
    def lastName: String
    def fullName: String = s"$firstName $lastName"

  sealed trait Person extends HasFullName:
    def birthDate: LocalDate

    def age(dateNow: LocalDate = LocalDate.now()): Int =
      Period.between(birthDate, dateNow).getYears

    infix def likes(movie: String) = s"$fullName says: I adore the movie $movie!!"

  final case class Driver(
     firstName: String,
     lastName: String,
     birthDate: LocalDate
   ) extends Person

  final case class Car(
      make: String,
      model: String,
      releaseYear: Year,
      owner: Driver
    ):
    /** Age of the current owner today (or on a given date). */
    def ownerAge(dateNow: LocalDate = LocalDate.now()): Int = owner.age(dateNow)

    /** Value-based check: same person data ⇒ true. */
    def isOwnedBy(driver: Driver): Boolean = owner == driver

    /** Convenience: user-facing model string. */
    def displayName: String = s"$make $model ${releaseYear.getValue}"

    /** Shallow copy (like case class `copy`): keeps same `owner` reference. */
    private def copyShallow(
       make: String = this.make,
       model: String = this.model,
       releaseYear: Year = this.releaseYear,
       owner: Driver = this.owner
     ): Car = Car(make, model, releaseYear, owner)

    /** Deep-ish copy: duplicates nested Driver too (C#-style “clone”). */
    def copyDeep(
        make: String = this.make,
        model: String = this.model,
        releaseYear: Year = this.releaseYear,
        owner: Driver = this.owner.copy()
      ): Car = Car(make, model, releaseYear, owner)

    /** Return a new Car with a different owner (immutably). */
    def withOwner(newOwner: Driver): Car = copyShallow(owner = newOwner)

  object Driver:
    /** Helper for legacy data with only year-of-birth. */
    def fromYearOfBirth(first: String, last: String, yearOfBirth: Int): Driver =
      Driver(first, last, LocalDate.of(yearOfBirth, 1, 1))

  object Car:
    def createNew(make: String, model: String, releaseYear: Int, owner: Driver): Car =
      Car(make, model, Year.of(releaseYear), owner)

  def main(args: Array[String]): Unit = {
    val driver = Driver.fromYearOfBirth("John", "DOe", 1988)
    val driverImposter = Driver.fromYearOfBirth("John", "DOe", 1988)
    val car = Car("Volvo", "XC90", Year.from(LocalDate.parse("2015-04-23")), driver)
    println(s"Car: ${car.displayName}")
    println(s"Car: ${car.copy(releaseYear = Year.from(LocalDate.parse("2013-04-23"))).ownerAge()}")
    println(s"testing eq: ${driver == driverImposter}")
    println(driver likes "Forest gUMP")
    println(driver.likes("Forest gUMP"))
  }
}
