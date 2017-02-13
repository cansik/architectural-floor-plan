package ch.fhnw.afpars.util

/**
 * Created by cansik on 13.02.17.
 */
class Stopwatch {
    private var startTime = 0L
    private var lastSince = 0L

    fun start() {
        startTime = System.currentTimeMillis()
    }

    fun elapsed(): Long {
        val since = System.currentTimeMillis() - (startTime + lastSince)
        lastSince += since
        return since
    }

    fun stop(): Long {
        return System.currentTimeMillis() - startTime
    }
}