package ch.fhnw.afpars.algorithm


/**
 * Created by cansik on 12.10.16.
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class AlgorithmParameter(val name: String, val helpText : String = "", val minValue: Double = 0.0, val maxValue: Double = 100.0, val majorTick: Double = 1.0)