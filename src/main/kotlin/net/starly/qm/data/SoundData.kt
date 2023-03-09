package net.starly.qm.data

import net.starly.qm.data.stream.Readable
import net.starly.qm.data.stream.Writeable
import net.starly.qm.extension.toFormattedString
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream

class SoundData: Writeable, Readable {

    var sound: String? = null
    var volume: Float = 1f
    var pitch: Float = 1f

    constructor()
    constructor(sound: String?, volume: Float, pitch: Float) {
        this.sound = sound
        this.volume = volume
        this.pitch = pitch
    }

    override fun write(buf: BukkitObjectOutputStream) {
        buf.writeUTF(sound?:"")
        buf.writeFloat(volume)
        buf.writeFloat(pitch)
    }

    override fun read(buf: BukkitObjectInputStream) {
        sound = buf.readUTF().run { ifEmpty { null } }
        volume = buf.readFloat()
        pitch = buf.readFloat()
    }

    fun playSound(player: Player) {
        sound?.also {
            try {
                player.playSound(player.location, Sound.valueOf(it.uppercase()), volume, pitch)
            } catch (e: Exception) {
                player.playSound(player.location, it, volume, pitch)
            }
        }
    }

    override fun toString(): String = sound?.run { "$this, ${volume.toFormattedString()}, ${pitch.toFormattedString()}" }?: "§7없음"

}