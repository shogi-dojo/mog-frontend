package com.mogproject.mogami.frontend.view.menu

import com.mogproject.mogami.frontend._
import com.mogproject.mogami.frontend.view.Observable
import org.scalajs.dom.html.Div
import com.mogproject.mogami.util.Implicits._

import scalatags.JsDom.TypedTag
import scalatags.JsDom.all._
import org.scalajs.jquery.jQuery


/**
  *
  */
trait AccordionMenu extends WebComponent with Observable[AccordionMenu] with SAMObserver[BasePlaygroundModel] {

  def ident: String

  def getTitle(messages: Messages): String

  def icon: String

  def content: TypedTag[Div]

  def visibleMode: Set[ModeType]

  private[this] val panelCls = Map(false -> "panel-default", true -> "panel-info")

  private[this] lazy val glyph = span(cls := s"glyphicon glyphicon-${icon}").render

  private[this] lazy val mainElem: Div = div(
    id := s"collapse${ident}",
    cls := "panel-collapse collapse",
    role := "tabpanel",
    aria.labelledby := s"heading${ident}",
    div(
      cls := "panel-body",
      content
    )
  ).render

  private[this] lazy val labelArea = WebComponent(span(paddingLeft := 20.px)).withDynamicTextContent(getTitle)

  private[this] val titleElemHeading = h4(cls := "panel-title",
    span(
      cls := "accordion-toggle",
      glyph,
      labelArea.element
    )
  ).render

  override lazy val element: Div = {
    val elem = div(
      cls := "panel",
      data("toggle") := "tooltip",
      data("placement") := "left",
      marginBottom := 5.px,
      div(
        cls := "panel-heading",
        id := s"heading${ident}",
        role := "button",
        data("toggle") := "collapse",
        data("target") := s"#collapse${ident}",
        data("parent") := "#accordion",
        titleElemHeading
      ),
      mainElem
    ).render

    Tooltip.enableHoverToolTip(elem)
    elem
  }

  def initialize(): Unit = {
    def f(b: Boolean): Unit = {
      element.classList.remove(panelCls(!b))
      element.classList.add(panelCls(b))
    }

    // set events
    jQuery(mainElem)
      .on("show.bs.collapse", { () => f(true); notifyObservers(this) })
      .on("hide.bs.collapse", () => f(false))

    f(false)
    expandTitle()
  }

  def collapseTitle(): Unit = {
    labelArea.hide()
    element.setAttribute("data-original-title", ident)
  }

  def expandTitle(): Unit = {
    labelArea.show(display.inline)
    element.removeAttribute("data-original-title")
  }

  initialize()

  //
  // Observer
  //
  /** @note This must be a function in order to be used in inherited classes. */
  override def samObserveMask: Int = ObserveFlag.MODE_TYPE

  override def refresh(model: BasePlaygroundModel, flag: Int): Unit = {
    if (visibleMode.contains(model.mode.modeType)) show() else hide()
  }
}

