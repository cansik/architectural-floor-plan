package ch.fhnw.afpars.ui.control

import javafx.beans.property.SimpleObjectProperty
import javafx.geometry.Insets
import javafx.geometry.Point2D
import javafx.geometry.Pos
import javafx.geometry.Rectangle2D
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.layout.HBox

/**
 * Created by cansik on 12.10.16.
 * Basic concept by James D https://gist.github.com/james-d/ce5ec1fd44ce6c64e81a
 */
class PreviewImageView : ImageView() {

    private val MIN_PIXELS = 10

    fun newImage(image: Image) {
        this.image = image
        reset(this, image.width, image.height)
    }

    init {
        isPreserveRatio = true

        val mouseDown = SimpleObjectProperty<Point2D>()

        setOnMousePressed({ e ->
            if (image == null) return@setOnMousePressed

            val mousePress = imageViewToImage(this, Point2D(e.x, e.y))
            mouseDown.set(mousePress)
        })

        setOnMouseDragged({ e ->
            if (image == null) return@setOnMouseDragged

            val dragPoint = imageViewToImage(this, Point2D(e.x, e.y))
            shift(this, dragPoint.subtract(mouseDown.get()))
            mouseDown.set(imageViewToImage(this, Point2D(e.x, e.y)))
        })

        setOnScroll({ e ->
            if (image == null) return@setOnScroll

            val delta = e.deltaY
            val viewport = viewport

            val scale = clamp(Math.pow(1.01, delta),

                    // don't scale so we're zoomed in to fewer than MIN_PIXELS in any direction:
                    Math.min(MIN_PIXELS / viewport.width, MIN_PIXELS / viewport.height),

                    // don't scale so that we're bigger than image dimensions:
                    Math.max(image.width / viewport.width, image.height / viewport.height))

            val mouse = imageViewToImage(this, Point2D(e.x, e.y))

            val newWidth = viewport.width * scale
            val newHeight = viewport.height * scale

            // To keep the visual point under the mouse from moving, we need
            // (x - newViewportMinX) / (x - currentViewportMinX) = scale
            // where x is the mouse X coordinate in the image

            // solving this for newViewportMinX gives

            // newViewportMinX = x - (x - currentViewportMinX) * scale

            // we then clamp this value so the image never scrolls out
            // of the imageview:

            val newMinX = clamp(mouse.x - (mouse.x - viewport.minX) * scale,
                    0.0, image.width - newWidth)
            val newMinY = clamp(mouse.y - (mouse.y - viewport.minY) * scale,
                    0.0, image.height - newHeight)

            setViewport(Rectangle2D(newMinX, newMinY, newWidth, newHeight))
        })

        setOnMouseClicked({ e ->
            if (image === null) return@setOnMouseClicked

            if (e.clickCount === 2) {
                reset(this, image.width, image.height)
            }
        })
    }

    private fun createButtons(width: Double, height: Double, imageView: ImageView): HBox {
        val reset = Button("Reset")
        reset.setOnAction({ e -> reset(imageView, width / 2, height / 2) })
        val full = Button("Full view")
        full.setOnAction({ e -> reset(imageView, width, height) })
        val buttons = HBox(10.0, reset, full)
        buttons.alignment = Pos.CENTER
        buttons.padding = Insets(10.0)
        return buttons
    }

    // reset to the top left:
    private fun reset(imageView: ImageView, width: Double, height: Double) {
        imageView.viewport = Rectangle2D(0.0, 0.0, width, height)
    }

    // shift the viewport of the imageView by the specified delta, clamping so
// the viewport does not move off the actual image:
    private fun shift(imageView: ImageView, delta: Point2D) {
        val viewport = imageView.viewport

        val width = imageView.image.width
        val height = imageView.image.height

        val maxX = width - viewport.width
        val maxY = height - viewport.height

        val minX = clamp(viewport.minX - delta.x, 0.0, maxX)
        val minY = clamp(viewport.minY - delta.y, 0.0, maxY)

        imageView.viewport = Rectangle2D(minX, minY, viewport.width, viewport.height)
    }

    private fun clamp(value: Double, min: Double, max: Double): Double {

        if (value < min)
            return min
        if (value > max)
            return max
        return value
    }

    // convert mouse coordinates in the imageView to coordinates in the actual image:
    private fun imageViewToImage(imageView: ImageView, imageViewCoordinates: Point2D): Point2D {
        val xProportion = imageViewCoordinates.x / imageView.boundsInLocal.width
        val yProportion = imageViewCoordinates.y / imageView.boundsInLocal.height

        val viewport = imageView.viewport
        return Point2D(
                viewport.minX + xProportion * viewport.width,
                viewport.minY + yProportion * viewport.height)
    }
}