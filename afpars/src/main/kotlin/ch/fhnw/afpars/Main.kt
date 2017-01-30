package ch.fhnw.afpars

import ch.fhnw.afpars.ui.controller.MainView
import javafx.application.Application
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage


class Main : Application() {

    @Throws(Exception::class)
    override fun start(primaryStage: Stage) {
        val loader = FXMLLoader(javaClass.classLoader.getResource("view/MainView.fxml"))
        val root = loader.load<Any>() as Parent
        val controller = loader.getController<Any>() as MainView


        primaryStage.title = "Architectural Floor Plan Analysis"
        primaryStage.scene = Scene(root)
        primaryStage.setOnShown { controller.setupView() }
        primaryStage.show()
        System.out.println()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(Main::class.java)
        }
    }
}