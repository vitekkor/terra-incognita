package com.vitekkor.view

import com.vitekkor.Styles
import com.vitekkor.controller.MyController
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import tornadofx.*

class RulesView : Fragment() {
    private val controller: MyController by inject()
    private val text = resources.stream("/rules.txt").bufferedReader().readText().split("^")
    override val root = vbox {
        addClass("setBackgroundFill")
        alignment = Pos.TOP_CENTER
        label("Game Rules") { vboxConstraints { margin = Insets(20.0); marginTop = 50.0 } }
        scrollpane {
            isFitToWidth = true
            vboxConstraints { alignment = Pos.CENTER }
            gridpane {
                row {
                    vbox {
                        alignment = Pos.CENTER
                        label(text[0]) {
                            isWrapText = true
                            addClass(Styles.rules)
                            vboxConstraints { alignment = Pos.CENTER;margin = Insets(20.0) }
                        }
                    }
                }
                row {
                    hbox {
                        alignment = Pos.CENTER
                        label("Wall - not available for pass") { addClass(Styles.rules);hboxConstraints { margin = Insets(20.0) } }
                        val tile = controller.getTile("front_wall")
                        tile.isVisible = true
                        add(tile)
                    }
                }
                row {
                    hbox {
                        alignment = Pos.CENTER
                        label("Treasure") { addClass(Styles.rules);hboxConstraints { margin = Insets(20.0) } }
                        val tile = controller.getTile("treasure")
                        tile.isVisible = true
                        add(tile)
                    }
                }
                row {
                    hbox {
                        alignment = Pos.CENTER
                        label("Entrance") { addClass(Styles.rules);hboxConstraints { margin = Insets(20.0) } }
                        val tile = controller.getTile("entrance1")
                        tile.isVisible = true
                        add(tile)
                    }
                }
                row {
                    hbox {
                        alignment = Pos.CENTER
                        label("Exit") { addClass(Styles.rules);hboxConstraints { margin = Insets(20.0) } }
                        val tile = controller.getTile("exit1")
                        tile.isVisible = true
                        add(tile)
                    }
                }
                row {
                    hbox {
                        alignment = Pos.CENTER
                        label("Wormhole") { addClass(Styles.rules); hboxConstraints { margin = Insets(20.0) } }
                        val tile = controller.getTile("wormhole")
                        tile.isVisible = true
                        add(tile)
                    }
                }
                row {
                    hbox {
                        alignment = Pos.CENTER
                        label(text[1]) {
                            isWrapText = true
                            addClass(Styles.rules)
                            hboxConstraints { margin = Insets(20.0); alignment = Pos.CENTER }
                        }
                    }
                }
            }
            background = Background(BackgroundFill(Color.BLACK, CornerRadii(20.0), Insets.EMPTY))
        }

    }
}
