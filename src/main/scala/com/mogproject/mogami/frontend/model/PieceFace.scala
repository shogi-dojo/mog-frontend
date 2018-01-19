package com.mogproject.mogami.frontend.model

import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.Ptype


/**
  *
  */
sealed abstract class PieceFace(val faceId: String, val displayName: String, val symmetric: Boolean) {
  val basePath: String = "assets/img/p/"

  def allImagePaths: Seq[String] = {
    for {
      pt <- Ptype.constructor
      fl <- Seq(false) ++ (!symmetric).option(true)
    } yield getImagePath(pt, fl)
  }

  def getImagePath(ptype: Ptype, flipped: Boolean): String = {
    val pl = symmetric.fold("", flipped.fold("W", "B"))
    s"${basePath}${faceId}/${pl}${ptype.toCsaString}.svg"
  }
}

case object JapaneseOneCharFace extends PieceFace("jp1", "Japanese 1", true)

case object JapaneseOneCharGraphicalFace extends PieceFace("jp2", "Japanese 2", true)

case object JapaneseTwoCharGraphicalFace extends PieceFace("jp3", "Japanese 3", true)

case object WesternOneCharFace extends PieceFace("en1", "Western 1", true)

case object HidetchiInternational extends PieceFace("hd1", "Hidetchi", false)

object PieceFace {
  def parseString(s: String): Option[PieceFace] = all.find(_.faceId == s)

  val all = Seq(JapaneseOneCharFace, JapaneseOneCharGraphicalFace, JapaneseTwoCharGraphicalFace, WesternOneCharFace, HidetchiInternational)
}