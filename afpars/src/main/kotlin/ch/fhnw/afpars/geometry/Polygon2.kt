package ch.fhnw.afpars.geometry

import java.io.Serializable

/**
 * Created by cansik on 28.11.16.
 */
class Polygon2() : Serializable {

    val points = mutableListOf<Vector2>()

    constructor(vararg points: Vector2) : this() {
        this.points.addAll(points)
    }

    /**
     * PNPoly Method:
     * http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html#The%20C%20Code
     */
    fun contains(p: Vector2): Boolean {
        var inside = false
        var i = 0
        var j = points.size - 1
        while (i < points.size) {
            if (points[i].y > p.y != points[j].y > p.y
                    && p.x < (points[j].x - points[i].x)
                    * (p.y - points[i].y)
                    / (points[j].y - points[i].y) + points[i].x)
                inside = !inside
            j = i++
        }
        return inside
    }

    /**
     * Finds the best position to insert the point.
     */
    fun addPoint(p: Vector2) {

    }

    companion object {

        private const val serialVersionUID = 1L

    }

}