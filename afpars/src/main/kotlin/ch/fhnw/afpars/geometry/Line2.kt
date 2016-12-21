package ch.fhnw.afpars.geometry

import ch.fhnw.afpars.geometry.intersection.LineIntersection

/**
 * Created by cansik on 28.11.16.
 */
class Line2(internal var A: Vector2, B: Vector2) {
    internal var R: Vector2

    init {
        this.R = B.sub(A)
    }

    fun intersect(b: Line2): LineIntersection {
        val a = this

        //g
        val oa = a.A
        val ab = a.R

        //h
        val oc = b.A
        val cd = b.R

        //create linear system
        //lambda
        val l = Vector2(ab.x, ab.y)

        //my
        val m = Vector2(0 - cd.x, 0 - cd.y)

        //num
        val n = Vector2(oc.x - oa.x, oc.y - oa.y)

        //solve
        //if det = 0 => both vectors are over each other
        val det = l.x * m.y - m.x * l.y
        val x1 = (n.x * m.y - m.x * n.y) / det
        val x2 = (l.x * n.y - n.x * l.y) / det

        val s = oc.add(cd.scale(x2))

        return LineIntersection(s, x1, x2)
    }
}