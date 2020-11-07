package com.vitekkor.view

import com.vitekkor.Styles
import com.vitekkor.controller.MyController
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.HBox
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
                    add(rulesHBox("Wall - not available for pass", "front_wall"))
                }
                row {
                    add(rulesHBox("Empty cell - available for pass", "empty_cell"))
                }
                row {
                    add(rulesHBox("Treasure", "treasure"))
                }
                row {
                    add(rulesHBox("Entrance", "entrance1"))
                }
                row {
                    add(rulesHBox("Exit", "exit1"))
                }
                row {
                    add(rulesHBox("Wormhole", "wormhole"))
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

    private fun rulesHBox(text: String, nameOfTile: String): HBox {
        return hbox {
            alignment = Pos.CENTER
            label(text) { addClass(Styles.rules); hboxConstraints { margin = Insets(20.0) } }
            val tile = controller.getTile(nameOfTile)
            tile.isVisible = true
            add(tile)
        }
    }
}
