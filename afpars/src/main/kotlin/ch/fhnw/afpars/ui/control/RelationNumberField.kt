package ch.fhnw.afpars.ui.control

/**
 * Created by cansik on 02.02.17.
 */
import javafx.animation.Interpolator
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.Timeline
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.control.TextFormatter
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Rectangle
import javafx.util.Duration
import javafx.util.StringConverter
import javafx.util.converter.DoubleStringConverter
import javafx.util.converter.NumberStringConverter
import java.text.NumberFormat
import java.util.*

/**
 * Created by cansik on 06.01.17.
 */
class RelationNumberField @JvmOverloads constructor(value: Double = 0.0, minimum: Double = 0.0, maximum: Double = 100.0) : TextField() {


    // value specific
    private val formatter: TextFormatter<Double>
    private val converter: StringConverter<Number>

    private val value: DoubleProperty

    private val minimum: DoubleProperty
    private val maximum: DoubleProperty

    // controls
    private val pane: AnchorPane
    private val valueRect: Rectangle

    private val minimumLabel: Label
    private val maximumLabel: Label

    // animation
    private val inLabelAnimation = Timeline()
    private val outLabelAnimation = Timeline()

    // design specific
    private val minimumBarHeight = SimpleDoubleProperty(4.0)

    private val showRange = SimpleBooleanProperty(true)

    private val barFill = SimpleObjectProperty<Paint>(Color(0.203, 0.596, 0.858, 1.0))

    init {
        this.value = SimpleDoubleProperty(value)
        this.minimum = SimpleDoubleProperty(minimum)
        this.maximum = SimpleDoubleProperty(maximum)

        // init controls
        pane = AnchorPane()
        valueRect = Rectangle(0.0, 0.0, 0.0, 0.0)
        maximumLabel = Label()
        minimumLabel = Label()

        formatter = TextFormatter(DoubleStringConverter())
        converter = NumberStringConverter(NumberFormat.getInstance(Locale.ENGLISH))

        initializeNumberField()
        initializeControls()

        // setup animations
        setupLabelAnimation(inLabelAnimation, 0.0, 1.0)
        setupLabelAnimation(outLabelAnimation, -20.0, 0.0)

        this.value.addListener { o, oldVal, newVal -> resizeAnimation() }
        this.widthProperty().addListener { o -> resize() }

        this.value.value = this.value.get()
        this.minimum.value = this.minimum.get()
        this.maximum.value = this.maximum.get()
    }

    private fun initializeControls() {
        // init ui
        setMinSize(MINIMUM_WIDTH, MINIMUM_HEIGHT)
        setPrefSize(PREFERRED_WIDTH, PREFERRED_HEIGHT)
        setMaxSize(MAXIMUM_WIDTH, MAXIMUM_HEIGHT)

        alignment = Pos.TOP_RIGHT

        applyLabelStyle(minimumLabel)
        applyLabelStyle(maximumLabel)

        AnchorPane.setTopAnchor(maximumLabel, 7.0)

        valueRect.fillProperty().bindBidirectional(barFill)
        valueRect.heightProperty().bindBidirectional(minimumBarHeight)
        valueRect.arcHeight = 2.0
        valueRect.arcWidth = 2.0

        // maximum label show
        setOnMouseEntered { me -> fadeLabelsIn() }
        setOnMouseExited { me -> fadeLabelsOut() }

        AnchorPane.setBottomAnchor(valueRect, 0.0)
        AnchorPane.setLeftAnchor(valueRect, 0.0)
        AnchorPane.setRightAnchor(valueRect, 0.0)

        pane.children.addAll(valueRect, maximumLabel, minimumLabel)
        children.addAll(pane)
    }

    private fun setupLabelAnimation(timeline: Timeline, xPosition: Double, opacity: Double) {
        val kvMin = KeyValue(minimumLabel.layoutXProperty(), xPosition, ANIMATION_INTERPOLATOR)
        val kvMax = KeyValue(maximumLabel.layoutXProperty(), xPosition, ANIMATION_INTERPOLATOR)

        val opMin = KeyValue(minimumLabel.opacityProperty(), opacity, ANIMATION_INTERPOLATOR)
        val opMax = KeyValue(maximumLabel.opacityProperty(), opacity, ANIMATION_INTERPOLATOR)

        timeline.keyFrames.add(KeyFrame(Duration.millis(50.0), kvMin))
        timeline.keyFrames.add(KeyFrame(Duration.millis(50.0), kvMax))

        timeline.keyFrames.add(KeyFrame(Duration.millis(50.0), opMin))
        timeline.keyFrames.add(KeyFrame(Duration.millis(50.0), opMax))
    }

    private fun fadeLabelsIn() {
        if (showRange.value!!) {
            outLabelAnimation.stop()
            inLabelAnimation.play()
        }
    }

    private fun fadeLabelsOut() {
        inLabelAnimation.stop()
        outLabelAnimation.play()
    }

    private fun applyLabelStyle(label: Label) {
        label.textFill = Color.web("#2c3e50")
        label.style = "-fx-font-size: 8;"
        label.clip = this.clip
    }

    private fun initializeNumberField() {
        // create binding between value and text
        Bindings.bindBidirectional(textProperty(), value, converter)

        minimum.addListener { obs, o, n -> minimumLabel.text = "Min: " + n }
        maximum.addListener { obs, o, n -> maximumLabel.text = "Max: " + n }

        // set number formatter
        textFormatter = formatter
    }

    private val paneWidth: Double
        get() = limit(map(value.value!!, minimum.value!!, maximum.value!!, 0.0, pane.width), 0.0, pane.width)

    public fun resize() {
        valueRect.width = paneWidth
    }

    public fun resizeAnimation() {
        val position = paneWidth

        val timeline = Timeline()
        val kv = KeyValue(valueRect.widthProperty(), position, ANIMATION_INTERPOLATOR)
        val kf = KeyFrame(Duration.millis(ANIMATION_DURATION), kv)
        timeline.keyFrames.add(kf)
        timeline.play()
    }


    fun getValue(): Double {
        return value.get()
    }

    fun valueProperty(): DoubleProperty {
        return value
    }

    fun setValue(value: Double) {
        this.value.set(value)
    }

    fun getMinimum(): Double {
        return minimum.get()
    }

    fun minimumProperty(): DoubleProperty {
        return minimum
    }

    fun setMinimum(minimum: Double) {
        this.minimum.set(minimum)
    }

    fun getMaximum(): Double {
        return maximum.get()
    }

    fun maximumProperty(): DoubleProperty {
        return maximum
    }

    fun setMaximum(maximum: Double) {
        this.maximum.set(maximum)
    }

    fun getMinimumBarHeight(): Double {
        return minimumBarHeight.get()
    }

    fun minimumBarHeightProperty(): DoubleProperty {
        return minimumBarHeight
    }

    fun setMinimumBarHeight(minimumBarHeight: Double) {
        this.minimumBarHeight.set(minimumBarHeight)
    }

    fun getBarFill(): Paint {
        return barFill.get()
    }

    fun barFillProperty(): ObjectProperty<Paint> {
        return barFill
    }

    fun setBarFill(barFill: Paint) {
        this.barFill.set(barFill)
    }

    var isShowRange: Boolean
        get() = showRange.get()
        set(showRange) = this.showRange.set(showRange)

    fun showRangeProperty(): BooleanProperty {
        return showRange
    }

    companion object {
        private val PREFERRED_WIDTH = 200.0
        private val MINIMUM_WIDTH = 30.0
        private val MAXIMUM_WIDTH = 800.0

        private val PREFERRED_HEIGHT = 32.0
        private val MINIMUM_HEIGHT = 32.0
        private val MAXIMUM_HEIGHT = 32.0

        private val ANIMATION_DURATION = 500.0
        private val ANIMATION_INTERPOLATOR = Interpolator.EASE_OUT

        private fun map(value: Double, start1: Double, stop1: Double, start2: Double, stop2: Double): Double {
            return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1))
        }

        private fun limit(value: Double, min: Double, max: Double): Double {
            return Math.min(max, Math.max(min, value))
        }
    }
}