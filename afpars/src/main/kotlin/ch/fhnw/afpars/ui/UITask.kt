package ch.fhnw.afpars.ui

import ch.fhnw.afpars.ui.controller.MainView
import javafx.application.Platform
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import kotlin.concurrent.thread

/**
 * Created by cansik on 18.02.17.
 */
class UITask(val block : ((task : UITask) -> Unit), val callBack : ((task : UITask) -> Unit) = {}, val taskName : String = "Task") {

    companion object
    {
        val status = SimpleStringProperty()

        val running = SimpleBooleanProperty()

        fun run(block : ((task : UITask) -> Unit), callBack : ((task : UITask) -> Unit) = {}, taskName : String = "Task")
        {
            UITask(block, callBack, taskName).run()
        }
    }

    fun run()
    {
        Platform.runLater({
            running.set(true)
            status.set("running task $taskName...")
        })

        // run block
        block(this)

        Platform.runLater({
            callBack(this)

            status.set("task $taskName finished!")
            running.set(false)
        })
    }
}