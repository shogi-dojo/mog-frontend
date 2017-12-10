package com.mogproject.mogami.frontend.view.board

import com.mogproject.mogami.frontend.Coord
import com.mogproject.mogami.frontend.view.board.board.SVGBoardLayout
import com.mogproject.mogami.frontend.view.board.hand.SVGHandLayout
import com.mogproject.mogami.frontend.view.board.player.SVGPlayerLayout
import com.mogproject.mogami.frontend.view.coordinate.Rect

/**
  *
  */
sealed trait SVGAreaLayout {
  def board: SVGBoardLayout

  def hand: SVGHandLayout

  def player: SVGPlayerLayout

  def viewBoxBottomRight: Coord
}

// fixme: offsets
case object SVGStandardLayout extends SVGAreaLayout {
  private[this] val boardPieceWidth = 210
  private[this] val boardPieceHeight = 230
  private[this] val boardMargin = 79
  private[this] val handPieceWidth = boardPieceWidth * 6 / 7
  private[this] val handPieceHeight = handPieceWidth * boardPieceHeight / boardPieceWidth
  private[this] val topMargin = 30
  private[this] val boardWidth = 9 * boardPieceWidth
  private[this] val handWidth = 7 * handPieceWidth
  private[this] val playerWidth = boardWidth - handWidth
  private[this] val symbolOffset = 30
  private[this] val symbolSize = 150
  private[this] val indicatorHeight = 92
  private[this] val playerNameHeight = handPieceHeight - indicatorHeight
  private[this] val playerBorderStroke = 5
  private[this] val indicatorBackgroundStroke = 30

  override val board: SVGBoardLayout = SVGBoardLayout(Coord(0, topMargin + handPieceHeight), boardPieceWidth, boardPieceHeight)

  private[this] val pivot: Coord = board.center

  private[this] val whiteHandTopLeft = Coord(boardMargin, topMargin)
  private[this] val blackHandTopLeft = Coord(boardMargin + playerWidth, board.offset.y + board.VIEW_BOX_HEIGHT)

  override val hand: SVGHandLayout = SVGHandLayout(whiteHandTopLeft, blackHandTopLeft, handPieceWidth, handPieceHeight, 1, 7)

  private[this] val blackPlayerArea = Rect(Coord(boardMargin, blackHandTopLeft.y), playerWidth, handPieceHeight)
  private[this] val blackSymbolArea = Rect(blackPlayerArea.leftTop - Coord(0, symbolOffset), symbolSize, symbolSize)
  private[this] val blackPlayerNameArea = Rect(blackPlayerArea.leftTop + Coord(symbolSize, 0), playerWidth - symbolSize, playerNameHeight)
  private[this] val blackIndicatorArea = Rect(blackPlayerArea.leftBottom - Coord(0, indicatorHeight), playerWidth, indicatorHeight)
  private[this] val blackIndicatorBackground = Seq(
    Rect(
      Coord(boardMargin - playerBorderStroke, blackHandTopLeft.y - playerBorderStroke - indicatorBackgroundStroke),
      boardWidth + indicatorBackgroundStroke + playerBorderStroke * 2, indicatorBackgroundStroke
    ),
    Rect(
      Coord(boardMargin + boardWidth + playerBorderStroke, blackHandTopLeft.y - playerBorderStroke - 1),
      indicatorBackgroundStroke, handPieceHeight + playerBorderStroke * 2 + 1
    )
  )

  override def player: SVGPlayerLayout = SVGPlayerLayout(
    blackPlayerArea.rotate(pivot),
    blackSymbolArea.rotate(pivot),
    blackPlayerNameArea.rotate(pivot),
    blackIndicatorArea.rotate(pivot),
    blackIndicatorBackground.map(_.rotate(pivot)),
    blackPlayerArea,
    blackSymbolArea,
    blackPlayerNameArea,
    blackIndicatorArea,
    blackIndicatorBackground
  )

  override def viewBoxBottomRight: Coord = board.offset + Coord(board.VIEW_BOX_WIDTH, board.VIEW_BOX_HEIGHT + (handPieceHeight + topMargin) * 2)
}

case object SVGCompactLayout extends SVGAreaLayout {
  private[this] val boardPieceWidth = 210
  private[this] val boardPieceHeight = 230
  private[this] val boardMargin = 79
  private[this] val handPieceWidth = boardPieceWidth * 6 / 7
  private[this] val handPieceHeight = handPieceWidth * boardPieceHeight / boardPieceWidth
  private[this] val topMargin = 30

  override val board: SVGBoardLayout = SVGBoardLayout(Coord(boardMargin + handPieceWidth + topMargin, 0), boardPieceWidth, boardPieceHeight)

  override val hand: SVGHandLayout = SVGHandLayout(
    Coord(boardMargin, boardMargin),
    board.offset + Coord(board.VIEW_BOX_WIDTH + topMargin, boardMargin + 3 * boardPieceHeight),
    handPieceWidth, handPieceHeight, 7, 1
  )

  override def player: SVGPlayerLayout = ???

  override def viewBoxBottomRight: Coord = board.offset + Coord(board.VIEW_BOX_WIDTH + handPieceWidth + boardMargin + topMargin, board.VIEW_BOX_HEIGHT)
}

case object SVGWideLayout extends SVGAreaLayout {
  private[this] val boardPieceWidth = 210
  private[this] val boardPieceHeight = 230
  private[this] val boardMargin = 79
  private[this] val handPieceWidth = boardPieceWidth
  private[this] val handPieceHeight = boardPieceHeight
  private[this] val topMargin = 30

  override val board: SVGBoardLayout = SVGBoardLayout(Coord(boardMargin + 2 * handPieceWidth + topMargin, 0), boardPieceWidth, boardPieceHeight)

  override val hand: SVGHandLayout = SVGHandLayout(
    Coord(topMargin, boardMargin),
    board.offset + Coord(board.VIEW_BOX_WIDTH + topMargin, boardMargin + 5 * boardPieceHeight),
    handPieceWidth, handPieceHeight, 4, 2
  )

  override def player: SVGPlayerLayout = ???

  override def viewBoxBottomRight: Coord = board.offset + Coord(board.VIEW_BOX_WIDTH + 2 * handPieceWidth + boardMargin + topMargin, board.VIEW_BOX_HEIGHT)
}