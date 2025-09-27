package com.rockthejvm.part2oop

import com.rockthejvm.part2oop.OopBasicsSolution.Driver

import java.time.{LocalDate, Year}

object CarRentalDSL {

  case class Driver(name: String, birthDate: LocalDate):
    def age(on: LocalDate = LocalDate.now()): Int =
      java.time.Period.between(birthDate, on).getYears

  case class Car(make: String, model: String, releaseYear: Year)

  // Rental contract
  case class Rental(driver: Driver, car: Car, days: Int):
    def summary: String =
      s"${driver.name} (age ${driver.age()}) rents a ${car.make} ${car.model} (${car.releaseYear}) for $days days."

  extension (driver: Driver)
    infix def rents(car: Car): PartialRental =
      PartialRental(driver, car)

  case class PartialRental(driver: Driver, car: Car):
    infix def forDays(days: Int): Rental =
      Rental(driver, car, days)

  def main(args: Array[String]): Unit = {
    val alice = Driver("Alice", LocalDate.of(1990, 5, 12))
    val bmw   = Car("BMW", "M3", Year.of(2015))

    val rental = alice rents bmw forDays 7

    println(rental.summary)
  }
}
