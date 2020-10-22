package com.vitekkor.view

import com.vitekkor.Styles
import com.vitekkor.controller.MyController
import javafx.geometry.Insets
import javafx.geometry.Pos
import tornadofx.*

class RulesView : Fragment() {
    private val controller: MyController by inject()
    private val text = resources.stream("/rules.txt").bufferedReader().readText().split("^")
    override val root = vbox {
        shortcut("Esc") { replaceWith<MainMenuView>() }
        alignment = Pos.TOP_CENTER
        label("Game Rules") { vboxConstraints { margin = Insets(20.0); marginTop = 50.0 } }
        scrollpane {
            vboxConstraints { alignment = Pos.CENTER }
            gridpane {
                row {
                    vbox {
                        alignment = Pos.CENTER
                        //"# стена (недоступна для прохода), T сокровище, S вход, E выход, 0-9 ямы с заданным номером (wormholes). "
                        label(text[0]) {
                            //wrappingWidth = app.workspace.currentStage?.width ?: 500.0
                            addClass(Styles.rules)
                            vboxConstraints { alignment = Pos.CENTER;margin = Insets(20.0) }
                        }
                    }
                }
                row {
                    vbox {
                        alignment = Pos.CENTER
                        label("Wall - not available for pass") { addClass(Styles.rules);vboxConstraints { margin = Insets(20.0) } }
                        val tile = controller.getTile("front_wall")
                        tile.isVisible = true
                        add(tile)
                    }
                }
                row {
                    vbox {
                        alignment = Pos.CENTER
                        label("Treasure") { addClass(Styles.rules);vboxConstraints { margin = Insets(20.0) } }
                        val tile = controller.getTile("treasure")
                        tile.isVisible = true
                        add(tile)
                    }
                }
                row {
                    vbox {
                        alignment = Pos.CENTER
                        label("Entrance") { addClass(Styles.rules);vboxConstraints { margin = Insets(20.0) } }
                        val tile = controller.getTile("entrance1")
                        tile.isVisible = true
                        add(tile)
                    }
                }
                row {
                    vbox {
                        alignment = Pos.CENTER
                        label("Exit") { addClass(Styles.rules);vboxConstraints { margin = Insets(20.0) } }
                        val tile = controller.getTile("exit1")
                        tile.isVisible = true
                        add(tile)
                    }
                }
                row {
                    vbox {
                        alignment = Pos.CENTER
                        label("Wormhole") { addClass(Styles.rules); vboxConstraints { margin = Insets(20.0) } }
                        val tile = controller.getTile("wormhole")
                        tile.isVisible = true
                        add(tile)
                    }
                }
                row {
                    vbox {
                        alignment = Pos.CENTER
                        //"# стена (недоступна для прохода), T сокровище, S вход, E выход, 0-9 ямы с заданным номером (wormholes). "
                        label(text[1]) {
                            //wrappingWidth = app.workspace.currentStage?.width ?: 500.0
                            addClass(Styles.rules)
                            vboxConstraints { margin = Insets(20.0) }
                            gridpaneConstraints { alignment = Pos.CENTER }
                        }
                    }
                }
            }
        }

    }
}
