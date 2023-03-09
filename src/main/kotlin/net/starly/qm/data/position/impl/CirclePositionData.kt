package net.starly.qm.data.position.impl

import net.starly.qm.context.BUTTON_DISTANCE_AT_PLAYER
import net.starly.qm.context.CIRCLE_RADIUS
import net.starly.qm.data.position.PositionData
import org.bukkit.Location
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

class CirclePositionData(
    override val key: String,
    override val count: Int,
) : PositionData {

    override fun getLocations(center: Location): List<Location> =
        createCircle(center)

    override fun getBaseLocation(center: Location): Location {
        val dist = center.direction.multiply(BUTTON_DISTANCE_AT_PLAYER)
        return center.clone().add(dist).apply { y -= 0.8 }
    }

    private fun createCircle(center: Location): List<Location> {
        if (count == 0) return emptyList()

        val result = ArrayList<Location>()
        val mid: Location = getBaseLocation(center)
        for (i in 0 until count) {
            val angle: Double = 2 * Math.PI * i / count
            val x: Double = cos(angle) * CIRCLE_RADIUS
            val y: Double = sin(angle) * CIRCLE_RADIUS
            var v: Vector = rotateAroundAxisX(Vector(x, y, 0.0), center.pitch.toDouble())
            v = rotateAroundAxisY(v, center.yaw.toDouble())
            result.add(mid.clone().add(v))
        }

        return result
    }

    private fun rotateAroundAxisX(vector: Vector, pitch: Double): Vector {
        val angle = Math.toRadians(pitch)
        val cos = cos(angle)
        val sin = sin(angle)
        val y = vector.y * cos - vector.z * sin
        val z = vector.y * sin + vector.z * cos
        return vector.setY(y).setZ(z)
    }

    private fun rotateAroundAxisY(v: Vector, yaw: Double): Vector {
        val angle = Math.toRadians(-yaw)
        val cos: Double = cos(angle)
        val sin: Double = sin(angle)
        val x = v.x * cos + v.z * sin
        val z = v.x * -sin + v.z * cos
        return v.setX(x).setZ(z)
    }

}