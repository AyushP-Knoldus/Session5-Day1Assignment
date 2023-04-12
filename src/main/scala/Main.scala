package com.knoldus
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global
object Main extends App{
    private val path = "/home/knoldus/Desktop/Ayush/Work/Session-5/Session5-Day1-Assignment/src/main/scala/marks.csv"
    val classAverage = GradeCalculator.calculateGrades(path)

    classAverage.onComplete {
      case Success(result) => println(s"Class average: $result")
      case Failure(ex) => println(s"Error: ${ex.getMessage}")
    }
    Thread.sleep(100)
  }