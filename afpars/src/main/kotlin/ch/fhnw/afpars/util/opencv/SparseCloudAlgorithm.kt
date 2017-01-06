package ch.fhnw.afpars.util.opencv

import org.opencv.core.Point

/**
 * Created by cansik on 06.01.17.
 */
class SparseCloudAlgorithm {

    fun sparsePoints(points: MutableList<Point>, maxDistance: Double): MutableList<MutableList<Point>> {
        val sparseCloud = mutableListOf<MutableList<Point>>()

        while (!points.isEmpty()) {
            val firstPoint = points.removeAt(0)
            val group = mutableListOf<Point>()
            val nearPoints = mutableListOf(firstPoint)

            while (!nearPoints.isEmpty()) {
                val point = nearPoints.removeAt(0)
                group.add(point)

                for (p in points.toList()) {
                    // calculate distance
                    val dist = distance(point, p)

                    if (dist < maxDistance) {
                        points.remove(p)
                        nearPoints.add(p)
                    }
                }
            }

            sparseCloud.add(group)
        }

        return sparseCloud
    }

    fun combinePoints(sparseCloud: MutableList<MutableList<Point>>): MutableList<Point> {
        val sparsePoints = mutableListOf<Point>()
        sparseCloud.mapTo(sparsePoints) { pointList -> Point(pointList.map { it.x }.average(), pointList.map { it.y }.average()) }
        return sparsePoints
    }

    private fun distance(p1: Point, p2: Point): Double {
        return Math.sqrt(Math.pow(p2.x - p1.x, 2.0) + Math.pow(p2.y - p1.y, 2.0))
    }
}