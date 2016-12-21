package ch.fhnw.afpars.geometry.intersection

import ch.fhnw.afpars.geometry.Vector2


/**
 * Created by cansik on 28.11.16.
 */
class LineIntersection(var s: Vector2, var lambda: Double, var my: Double) {

    fun IsSpecialIntersection(): Boolean {
        if (my < 0) {
            my = 10.0
        }

        if (lambda < 0) {
            lambda = 10.0
        }

        return my < 1 || lambda < 1
    }
}