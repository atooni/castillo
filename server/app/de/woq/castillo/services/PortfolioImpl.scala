package de.woq.castillo.services

import java.util.concurrent.atomic.AtomicLong

import de.woq.castillo.model.{SeminarDetails, Seminar}

import scala.collection.concurrent.TrieMap

object PortfolioImpl extends Portfolio {

  private[this] val seqId    = new AtomicLong
  private[this] val seminars = TrieMap.empty[String, Seminar]

  override def list(): Seq[Seminar] = seminars.values.to[Seq]

  override def create(s : SeminarDetails) : Option[Seminar] = {
    val id = seqId.incrementAndGet().toString
    val seminar = Seminar(id, s)
    seminars.put(id, seminar)
    Some(seminar)
  }

  override def update(seminar : Seminar) = seminars.replace(seminar.id, seminar) match {
    case None => None
    case _ => Some(seminar)
  }

  override def delete(id: String): Option[Seminar] = seminars.remove(id)

  override def get(id: String): Option[Seminar] = seminars.get(id)
}
