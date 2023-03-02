package net.starly.qm.data.position

import org.bukkit.Location
import org.bukkit.entity.Player

interface PositionData {

    val key: String
    val count: Int
    fun getLocations(player: Player): List<Location>
    fun getBaseLocation(player: Player): Location

}