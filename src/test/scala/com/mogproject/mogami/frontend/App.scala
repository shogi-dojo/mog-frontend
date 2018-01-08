package com.mogproject.mogami.frontend

import com.mogproject.mogami._
import com.mogproject.mogami.util.Implicits._
import com.mogproject.mogami.core.state.StateCache.Implicits.DefaultStateCache
import com.mogproject.mogami.frontend.model._
import com.mogproject.mogami.frontend.state.TestState
import com.mogproject.mogami.frontend.view.{BrowserInfo, TestView}
import com.mogproject.mogami.frontend.view.board.{SVGCompactLayout, SVGStandardLayout}
import org.scalajs.dom
import org.scalajs.dom.html.Div

import scala.scalajs.js.JSApp
import scala.util.{Failure, Success, Try}

/**
  * Entry point for testing
  */
object App extends JSApp {
  override def main(): Unit = {
    TestSettings

    // get args
    val args = Arguments()
      .loadLocalStorage()
      .parseQueryString(dom.window.location.search)
    if (args.config.isDebug) {
      println("Debug Mode enabled.")
      println(s"Sound supported: ${BrowserInfo.isSoundSupported}")
    }
    if (args.config.isDev) println("Dev Mode enabled.")

    // load game
    val game = createGameFromArgs(args)

    // update mode
    val isSnapshot = game.trunk.moves.isEmpty && game.trunk.finalAction.isEmpty && game.branches.isEmpty

    val mode = isSnapshot.fold(
      PlayMode(GameControl(game, 0, 0)),
      ViewMode(GameControl(game, args.gamePosition.branch, math.max(0, args.gamePosition.position - game.trunk.offset)))
    )

    // update config
    val verifiedConfig = args.config.copy(
      soundEffectEnabled = args.config.soundEffectEnabled && BrowserInfo.isSoundSupported
    )

    // create model
    val model = TestModel(mode, verifiedConfig)

    // create view
    val rootElem = dom.document.getElementById("app").asInstanceOf[Div]
    val view = TestView(verifiedConfig.deviceType.isMobile, verifiedConfig.isDev, verifiedConfig.isDebug, rootElem)

    // handle special actions
    args.action match {
      case NotesAction =>
        view.drawNotes(game, verifiedConfig.recordLang)
      case ImageAction =>
        // todo: support compact layout
        val conf = if (verifiedConfig.layout == SVGCompactLayout) verifiedConfig.copy(layout = SVGStandardLayout) else verifiedConfig
        view.drawAsImage(conf, mode.getGameControl.get)
      case PlayAction =>
        // initialize state
        if (verifiedConfig.isDebug) println("Initializing...")
        PlaygroundSAM.initialize(TestModel.adapter)
        SAM.initialize(TestState(model, view))

        // hide loading message and show the main contents
        if (verifiedConfig.isDebug) println("Finished initialization.")
        rootElem.style.display = scalatags.JsDom.all.display.block.v
        dom.document.getElementById("messageWindow").textContent = ""
    }
  }

  private[this] def createGameFromArgs(args: Arguments): Game = {
    def loadGame(game: => Game): Game = Try(game) match {
      case Success(g) => g
      case Failure(e) =>
        println(s"Failed to create a game: ${e}")
        Game()
    }

    val gg: Game = ((args.usen, args.sfen) match {
      case (Some(u), _) => loadGame(Game.parseUsenString(u)) // parse USEN string
      case (_, Some(s)) => loadGame(Game.parseSfenString(s)) // parse SFEN string
      case _ => Game()
    }).copy(newGameInfo = args.gameInfo)

    // update comments
    val comments = for {
      (b, m) <- args.comments
      (pos, c) <- m
      h <- gg.getHistoryHash(GamePosition(b, pos))
    } yield h -> c
    gg.copy(newComments = comments)
  }

}

