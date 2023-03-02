package net.starly.qm.data.position.impl

import net.starly.qm.context.BUTTON_DISTANCE_AT_PLAYER
import net.starly.qm.context.LINE_OTHER_BUTTON_DISTANCE
import net.starly.qm.data.position.PositionData
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

class LinePositionData(
    override val key: String,
    override val count: Int,
) : PositionData {

    override fun getLocations(player: Player): List<Location> =
        createLine(player)

    override fun getBaseLocation(player: Player): Location {
        val dist = player.eyeLocation.direction.multiply(BUTTON_DISTANCE_AT_PLAYER)
        return player.eyeLocation.add(dist).apply { y -= 0.8 }
    }

    private fun createLine(player: Player): List<Location> {
        if (count == 0) return emptyList()

        val result = ArrayList<Location>()
        val mid: Location = getBaseLocation(player)
        for (i in 0 until count) {
            if(count % 2 == 1) {
                val center = count / 2
                val distance = (i - center) * LINE_OTHER_BUTTON_DISTANCE
                if (center == i) result.add(mid.clone())
                else if (distance < .0) result.add(getLeftSide(mid, -distance).apply { yaw = mid.yaw })
                else result.add(getRightSide(mid, distance).apply { yaw = mid.yaw })
            } else {
                val center = count / 2.0 - 0.5
                val distance = (i - center) * LINE_OTHER_BUTTON_DISTANCE
                if (distance < .0) result.add(getLeftSide(mid, -distance).apply { yaw = mid.yaw })
                else result.add(getRightSide(mid, distance).apply { yaw = mid.yaw })
            }
        }
        return result
    }

    private fun getRightSide(location: Location, distance: Double): Location {
        val angle = location.yaw / 60
        return location.clone().subtract(
            Vector(cos(angle.toDouble()), 0.0, sin(angle.toDouble())).normalize().multiply(distance)
        )
    }

    private fun getLeftSide(location: Location, distance: Double): Location {
        val angle = location.yaw / 60
        return location.clone()
            .add(Vector(cos(angle.toDouble()), 0.0, sin(angle.toDouble())).normalize().multiply(distance))
    }


}