package net.starly.qm.data.stream

import org.bukkit.util.io.BukkitObjectOutputStream

interface Writeable {

    fun write(buf: BukkitObjectOutputStream)

}