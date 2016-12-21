package ch.fhnw.afpars.geometry

/**
 * Created by cansik on 28.11.16.
 */

import java.io.Serializable
import java.lang.Math.*

class Vector2(val x: Double, val y: Double) : Serializable {

    fun add(a: Vector2): Vector2 {
        return Vector2(x + a.x, y + a.y)
    }

    fun sub(a: Vector2): Vector2 {
        return Vector2(x - a.x, y - a.y)
    }

    fun neg(): Vector2 {
        return Vector2(-x, -y)
    }

    fun scale(a: Double): Vector2 {
        return Vector2(a * x, a * y)
    }

    fun dot(a: Vector2): Double {
        return x * a.x + y * a.y
    }

    fun modSquared(): Double {
        return dot(this)
    }

    fun mod(): Double {
        return sqrt(modSquared())
    }

    fun normalize(): Vector2 {
        return scale(1 / mod())
    }

    fun rotPlus90(): Vector2 {
        return Vector2(-y, x)
    }

    fun rotMinus90(): Vector2 {
        return Vector2(y, -x)
    }

    fun angle(): Double {
        return atan2(y, x)
    }

    fun distance(v: Vector2): Double {
        val o = sub(v)
        return Math.abs(Math.sqrt(Math.pow(o.x, 2.0) + Math.pow(o.y, 2.0)))
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        var temp: Long
        temp = java.lang.Double.doubleToLongBits(x)
        result = prime * result + (temp xor temp.ushr(32)).toInt()
        temp = java.lang.Double.doubleToLongBits(y)
        result = prime * result + (temp xor temp.ushr(32)).toInt()
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj)
            return true
        if (obj == null)
            return false
        if (javaClass != obj.javaClass)
            return false
        val other = obj as Vector2?
        if (java.lang.Double.doubleToLongBits(x) != java.lang.Double.doubleToLongBits(other!!.x))
            return false
        if (java.lang.Double.doubleToLongBits(y) != java.lang.Double.doubleToLongBits(other.y))
            return false
        return true
    }

    override fun toString(): String {
        return javaClass.simpleName + "(" + x + ", " + y + ")"
    }

    companion object {

        private const val serialVersionUID = 1L

        val NULL = Vector2(0.0, 0.0)
        val X = Vector2(1.0, 0.0)
        val Y = Vector2(0.0, 1.0)

        fun fromAngle(ang: Double): Vector2 {
            return Vector2(cos(ang), sin(ang))
        }

        fun fromPolar(ang: Double, mod: Double): Vector2 {
            return Vector2(mod * cos(ang), mod * sin(ang))
        }
    }

}