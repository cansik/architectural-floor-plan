package ch.fhnw.afpars.workflow

import ch.fhnw.afpars.algorithm.IAlgorithm
import ch.fhnw.afpars.event.Event
import ch.fhnw.afpars.model.AFImage
import ch.fhnw.afpars.ui.controller.ParameterEditView
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.stage.Stage
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import kotlin.properties.Delegates

/**
 * Created by cansik on 07.10.16.
 */
class WorkflowEngine {
    val finished = Event<AFImage>()
    val stepDone = Event<Pair<IAlgorithm, AFImage>>()

    var currentImage: AFImage by Delegates.notNull()

    private var stepLatch = CountDownLatch(1)

    var cancelRequested = false

    fun nextStep() {
        stepLatch.countDown()
    }

    fun run(workflow: Workflow, afImage: AFImage, editParameters: Boolean = false, waitAfterStep: Boolean = false) {
        thread {
            currentImage = afImage
            loop@ for (alg in workflow.algorithms) {

                // run algorithm
                if (editParameters) {
                    var isOk = false

                    while(!isOk) {
                        val res = showEditView(alg, currentImage)
                        isOk = res.second

                        // step latch
                        if(!isOk) {
                            stepLatch = CountDownLatch(1)
                            stepLatch.await()

                            if (cancelRequested) {
                                cancelRequested = false
                                break@loop
                            }
                        }
                        else
                        {
                            currentImage = res.first
                        }
                    }
                }
                else
                    currentImage = alg.run(currentImage)

                if (waitAfterStep && workflow.algorithms.last() != alg) {
                    stepLatch = CountDownLatch(1)
                    stepDone(Pair(alg, currentImage))
                    stepLatch.await()
                }

                if (cancelRequested) {
                    cancelRequested = false
                    break@loop
                }
            }
            finished(currentImage)
        }
    }

    fun showEditView(algorithm: IAlgorithm, afImage: AFImage): Pair<AFImage, Boolean> {
        val latch = CountDownLatch(1)

        val fxmlLoader = FXMLLoader(javaClass.classLoader.getResource("view/ParameterEditView.fxml"))
        val root: Parent = fxmlLoader.load()
        val controller = fxmlLoader.getController<ParameterEditView>()
        controller.initView(algorithm, afImage)

        Platform.runLater {
            val stage = Stage()
            stage.setOnShown { controller.setupView() }
            stage.title = "Algorithm: ${algorithm.name}"
            stage.scene = Scene(root)
            stage.showAndWait()
            latch.countDown()
        }

        // wait for ui
        latch.await()
        return Pair(controller.result, controller.isOK)
    }
}