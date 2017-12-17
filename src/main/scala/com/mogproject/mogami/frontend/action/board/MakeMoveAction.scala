package com.mogproject.mogami.frontend.action.board

import com.mogproject.mogami.Move
import com.mogproject.mogami.frontend.action.PlaygroundAction
import com.mogproject.mogami.frontend.model._

/**
  *
  */
case class MakeMoveAction(move: Move) extends PlaygroundAction {
  override def execute(model: BasePlaygroundModel): Option[BasePlaygroundModel] = {
    val m = model.mode
    m.getGameControl.map(gc => model.copy(m.setGameControl(gc.makeMove(move, m.isNewBranchMode, moveForward = true).get)))
  }
}
