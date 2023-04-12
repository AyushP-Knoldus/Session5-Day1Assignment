package com.knoldus

import scala.concurrent.Future
import scala.util.{Try, Success, Failure}
import scala.io.Source
import scala.concurrent.ExecutionContext.Implicits.global

object GradeCalculator {

  private def parseCsv(pathOfCsv: String): Future[List[Map[String, String]]] = {
    Future {
      Try {
        val lines = Source.fromFile(pathOfCsv).getLines.toList
        val header = lines.head.split(",").toList
        val data = lines.tail.map(_.split(",").toList)
        data.map(row => header.zip(row).toMap)
      } match {
        case Success(result) => result
        case Failure(ex) => throw new Exception(s"Error parsing CSV file: ${ex.getMessage}")
      }
    }
  }

  private def calculateStudentAverages(extractedClassInformation: Future[List[Map[String, String]]]): Future[List[(String, Double)]] = {
    extractedClassInformation.map(_.map { row =>
      val id = row("StudentID")
      val english = row("English").toDouble
      val physics = row("Physics").toDouble
      val chemistry = row("Chemistry").toDouble
      val maths = row("Maths").toDouble
      val avg = (english + physics + chemistry + maths) / 4
      (id, avg)
    })
  }

  private def calculateClassAverage(studentAverages: Future[List[(String, Double)]]): Future[Double] = {
    {
      studentAverages.map { averages =>
        val sum = averages.map(_._2).sum
        val count = averages.length
        sum / count
      }
    }
  }

  def calculateGrades(pathOfCsv: String): Future[Double] = {
    val extractedClassInformation = parseCsv(pathOfCsv)
    val studentAverages = calculateStudentAverages(extractedClassInformation)
    val classAverage = calculateClassAverage(studentAverages)
    classAverage.recover { case ex => throw new Exception(s"Error calculating class average: ${ex.getMessage}") }
  }
}
