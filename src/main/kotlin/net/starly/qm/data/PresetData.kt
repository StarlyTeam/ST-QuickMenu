package net.starly.qm.data

import net.starly.core.jb.util.AsyncExecutor
import net.starly.qm.QuickMenu
import net.starly.qm.data.position.PositionData
import net.starly.qm.data.stream.Readable
import net.starly.qm.data.stream.Writeable
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class PresetData: Writeable, Readable {

    var key: String = "기본 프리셋"
        private set
    var positionKey: String = "LINE_2"
        private set
    private val plugin: QuickMenu
    lateinit var buttons: List<ButtonData>
        private set
    constructor(plugin: QuickMenu) { this.plugin = plugin }
    constructor(
        key: String,
        positionKey: String,
        plugin: QuickMenu
    ) {
        this.key = key
        this.positionKey = positionKey
        this.plugin = plugin
        initializing()
    }

    lateinit var position: PositionData
        private set

    private fun initializing() {
        position = plugin.positionDataRepository.get(positionKey)!!
        val buttonList = ArrayList<ButtonData>()
        for(i in 0 until position.count)
            buttonList.add(ButtonData(null))
        buttons = buttonList
    }
    private fun getBaseFolder() = File(plugin.dataFolder, "preset")

    fun save() {
        AsyncExecutor.run {
            val folder = getBaseFolder()
            if(!folder.exists()) folder.mkdirs()
            val file = File(folder, "$key.bin")
            FileOutputStream(file).use { fo ->
                ByteArrayOutputStream().use { bos ->
                    BukkitObjectOutputStream(bos).use { boos ->
                        write(boos)
                    }
                    fo.write(bos.toByteArray())
                }
            }
        }
    }

    override fun read(buf: BukkitObjectInputStream) {
        key = buf.readUTF()
        positionKey = buf.readUTF()
        position = plugin.positionDataRepository.get(positionKey)!!
        val buttonList = ArrayList<ButtonData>()
        for(i in 0 until position.count)
            buttonList.add(ButtonData().apply { read(buf) })
        buttons = buttonList
    }

    override fun write(buf: BukkitObjectOutputStream) {
        buf.writeUTF(key)
        buf.writeUTF(positionKey)
        for(button in buttons)
            button.write(buf)
        buf.writeUTF("==EOF")
    }

}