package com.vitekkor.view

import com.vitekkor.controller.MyController
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.image.Image
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.paint.Color
import javafx.stage.Screen
import tornadofx.*
import kotlin.concurrent.thread

class SplashScreen : View("") {
    private val image = imageview {
        opacity = 0.0
        image = Image(resources.stream("/splash.png"))
        fitHeight = Screen.getPrimary().bounds.height
        fitWidth = Screen.getPrimary().bounds.height
    }

    override val root = borderpane {
        center = hbox {
            background = Background(BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY))
            image.apply { hboxConstraints { alignment = Pos.CENTER } }
            add(image)
        }
    }

    override fun onDock() {
        thread {
            Thread.sleep(1)
            runLater {
                close()
                primaryStage.show()
                image.opacityProperty().animate(1.0, 1.5.seconds) {
                    setOnFinished {
                        runAsync {
                            find<MainMenuView>().root
                            find<GamePreView>().root
                            find<GameView>().root
                            find<MyController>().loadAssets()
                        }
                        thread {
                            Thread.sleep(1000)
                            runLater {
                                image.opacityProperty().animate(
                                    0.0,
                                    1.5.seconds
                                ) { setOnFinished { replaceWith<MainView>(ViewTransition.Fade(0.5.seconds)) } }
                            }
                        }
                    }
                }
            }
        }

    }
}
