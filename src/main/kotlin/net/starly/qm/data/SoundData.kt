package net.starly.qm.data

import net.starly.qm.data.stream.Readable
import net.starly.qm.data.stream.Writeable
import net.starly.qm.extension.toFormattedString
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream

class SoundData: Writeable, Readable {

    var sound: Sound? = null
    var volume: Float = 1f
    var pitch: Float = 1f

    constructor()
    constructor(sound: Sound?, volume: Float, pitch: Float) {
        this.sound = sound
        this.volume = volume
        this.pitch = pitch
    }

    override fun write(buf: BukkitObjectOutputStream) {
        buf.writeUTF(sound?.name?:"")
        buf.writeFloat(volume)
        buf.writeFloat(pitch)
    }

    override fun read(buf: BukkitObjectInputStream) {
        sound = try {
            val strSound = buf.readUTF()
            if(strSound.isNotEmpty())
                Sound.valueOf(strSound)
            else null
        } catch (_: Exception) { null }
        volume = buf.readFloat()
        pitch = buf.readFloat()
    }

    fun playSound(player: Player) {
        sound?.apply { player.playSound(player.location, this, volume, pitch) }
    }

    override fun toString(): String = sound?.run { "$this, ${volume.toFormattedString()}, ${pitch.toFormattedString()}" }?: "§7없음"

}