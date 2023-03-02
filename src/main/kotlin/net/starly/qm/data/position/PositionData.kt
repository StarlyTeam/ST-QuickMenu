package net.starly.qm.data.position

import org.bukkit.Location
import org.bukkit.entity.Player

interface PositionData {

    val key: String
    val count: Int
    fun getLocations(center: Location): List<Location>
    fun getBaseLocation(center: Location): Location

}