package com.mogproject.mogami.frontend.view

import org.scalajs.dom.{Element, Node}

/**
  *
  */
trait WebComponent {
  def element: Element
}

object WebComponent {
  def removeElement(elem: Node): Unit = elem.parentNode.removeChild(elem)

  def removeElements(elems: Iterable[Node]): Unit = elems.foreach(removeElement)

  def removeAllChildElements(elem: Node): Unit = while (elem.hasChildNodes()) elem.removeChild(elem.firstChild)
}