package de.woq.castillo.model

case class Seminar (
  id: Long,              // A unique identifier
  title: String,         // A seminar title
  description: String,   // The course content
  trainer: String,       // Who is the trainer 
  duration: Int          // The course duration in days
)
