package net.starly.qm.data.position.impl

import net.starly.qm.context.BUTTON_DISTANCE_AT_PLAYER
import net.starly.qm.context.LINE_OTHER_BUTTON_DISTANCE
import net.starly.qm.data.position.PositionData
import net.starly.qm.util.VectorUtil
import net.starly.qm.util.VectorUtil.normalized
import org.bukkit.Location


class GridPositionData(
    override val key: String,
    override val count: Int,
) : PositionData {

    companion object {
        private val metricsMap = mapOf(
            4 to listOf<(Location) -> Location>({ loc ->
                val metrics = VectorUtil.getRotationMatrix(loc.direction)
                val result = loc.clone()
                result.add(metrics.component1().normalized.multiply(LINE_OTHER_BUTTON_DISTANCE / 2.0))
                    .add(metrics.component2().normalized.multiply(LINE_OTHER_BUTTON_DISTANCE / 2.0))
                result
            },
                { loc ->
                    val metrics = VectorUtil.getRotationMatrix(loc.direction)
                    val result = loc.clone()
                    result.add(metrics.component1().normalized.multiply(-LINE_OTHER_BUTTON_DISTANCE / 2.0))
                        .add(metrics.component2().normalized.multiply(LINE_OTHER_BUTTON_DISTANCE / 2.0))
                    result
                },
                { loc ->
                    val metrics = VectorUtil.getRotationMatrix(loc.direction)
                    val result = loc.clone()
                    result.add(metrics.component1().normalized.multiply(LINE_OTHER_BUTTON_DISTANCE / 2.0))
                        .add(metrics.component2().normalized.multiply(-LINE_OTHER_BUTTON_DISTANCE / 2.0))
                    result
                },
                { loc ->
                    val metrics = VectorUtil.getRotationMatrix(loc.direction)
                    val result = loc.clone()
                    result.add(metrics.component1().normalized.multiply(-LINE_OTHER_BUTTON_DISTANCE / 2.0))
                        .add(metrics.component2().normalized.multiply(-LINE_OTHER_BUTTON_DISTANCE / 2.0))
                    result
                }
            ),
            9 to listOf(
                { loc ->
                    val metrics = VectorUtil.getRotationMatrix(loc.direction)
                    val result = loc.clone()
                    result.add(metrics.component1().normalized.multiply(LINE_OTHER_BUTTON_DISTANCE))
                        .add(metrics.component2().normalized.multiply(LINE_OTHER_BUTTON_DISTANCE))
                    result
                },
                { loc ->
                    val metrics = VectorUtil.getRotationMatrix(loc.direction)
                    val result = loc.clone()
                    result.add(metrics.component2().normalized.multiply(LINE_OTHER_BUTTON_DISTANCE))
                    result
                },
                { loc ->
                    val metrics = VectorUtil.getRotationMatrix(loc.direction)
                    val result = loc.clone()
                    result.add(metrics.component1().normalized.multiply(-LINE_OTHER_BUTTON_DISTANCE))
                        .add(metrics.component2().normalized.multiply(LINE_OTHER_BUTTON_DISTANCE))
                    result
                },
                { loc ->
                    val metrics = VectorUtil.getRotationMatrix(loc.direction)
                    val result = loc.clone()
                    result.add(metrics.component1().normalized.multiply(LINE_OTHER_BUTTON_DISTANCE))
                    result
                },
                { loc -> loc.clone() },
                { loc ->
                    val metrics = VectorUtil.getRotationMatrix(loc.direction)
                    val result = loc.clone()
                    result.add(metrics.component1().normalized.multiply(-LINE_OTHER_BUTTON_DISTANCE))
                    result
                },
                { loc ->
                    val metrics = VectorUtil.getRotationMatrix(loc.direction)
                    val result = loc.clone()
                    result.add(metrics.component1().normalized.multiply(LINE_OTHER_BUTTON_DISTANCE))
                        .add(metrics.component2().normalized.multiply(-LINE_OTHER_BUTTON_DISTANCE))
                    result
                },
                { loc ->
                    val metrics = VectorUtil.getRotationMatrix(loc.direction)
                    val result = loc.clone()
                    result.add(metrics.component2().normalized.multiply(-LINE_OTHER_BUTTON_DISTANCE))
                    result
                },
                { loc ->
                    val metrics = VectorUtil.getRotationMatrix(loc.direction)
                    val result = loc.clone()
                    result.add(metrics.component1().normalized.multiply(-LINE_OTHER_BUTTON_DISTANCE))
                        .add(metrics.component2().normalized.multiply(-LINE_OTHER_BUTTON_DISTANCE))
                    result
                },
            )
        )
    }

    override fun getLocations(center: Location): List<Location> =
        createLine(center)

    override fun getBaseLocation(center: Location): Location {
        val dist = center.direction.multiply(BUTTON_DISTANCE_AT_PLAYER)
        return center.clone().add(dist).apply { y -= 0.8 }
    }

    private fun createLine(center: Location): List<Location> {
        if (count == 0) return emptyList()

        val result = ArrayList<Location>()
        val mid= getBaseLocation(center)
        metricsMap[count]?.forEach { result.add(it(mid)) }
        return result
    }

}