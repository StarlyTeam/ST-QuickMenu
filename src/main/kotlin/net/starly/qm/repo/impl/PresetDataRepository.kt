package net.starly.qm.repo.impl

import net.starly.core.jb.util.AsyncExecutor
import net.starly.qm.QuickMenu
import net.starly.qm.context.DEFAULT_POSITION_KEY
import net.starly.qm.data.PresetData
import net.starly.qm.repo.DataRepository
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class PresetDataRepository(
    private val plugin: QuickMenu
): DataRepository<String, PresetData> {

    private val presetMap = HashMap<String, PresetData>()
    private val folder = File(plugin.dataFolder, "preset")
    private val defaultPreset: PresetData by lazy { PresetData("기본 프리셋", DEFAULT_POSITION_KEY, plugin) }

    init {
        if(!folder.exists()) folder.mkdirs()
        folder.listFiles()?.forEach {
            val name = it.name.run { substring(0, length - 4).replace("_"," ") }
            FileInputStream(it).use { stream ->
                val data = ByteArray(it.length().toInt())
                stream.read(data)
                presetMap[name] = PresetData(plugin).apply {
                    ByteArrayInputStream(data).use { bis ->
                        BukkitObjectInputStream(bis).use { bois ->
                            read(bois)
                        }
                    }
                }
            }
        }
        if(isEmpty()) presetMap["기본 프리셋"] = defaultPreset
    }

    override fun get(key: String): PresetData {
        val origin = key.replace("_", " ")
        return presetMap[origin]?: defaultPreset
    }

    override fun register(key: String, value: PresetData) {
        val origin = key.replace("_", " ")
        if(origin == "기본 프리셋") return
        presetMap[origin] = value
    }

    fun save(key: String): Boolean {
        val target = presetMap[key]?: return false
        AsyncExecutor.run {
            if(!folder.exists()) folder.mkdirs()
            val file = File(folder, "$key.bin")
            FileOutputStream(file).use { fo ->
                ByteArrayOutputStream().use { bos ->
                    BukkitObjectOutputStream(bos).use { boos ->
                        target.write(boos)
                    }
                    fo.write(bos.toByteArray())
                }
            }
        }
        return true
    }

    override fun unregister(key: String): PresetData? {
        val origin = key.replace("_", " ")
        if(origin == "기본 프리셋") return null
        return presetMap.remove(origin)?.apply {
            val file = File(folder, origin)
            try { if (file.exists()) file.delete() } catch (_: Exception) {}
        }
    }

    override fun iterator(filter: (Map.Entry<String, PresetData>) -> Boolean): Iterator<Map.Entry<String, PresetData>> =
        presetMap.filter(filter).iterator()

    override fun iterator(): MutableIterator<Map.Entry<String, PresetData>> =
        presetMap.iterator()

    override fun isEmpty(): Boolean =
        presetMap.isEmpty()

    override fun contains(key: String): Boolean {
        val origin = key.replace("_", " ")
        return if(origin == "기본 프리셋") true
        else presetMap.containsKey(origin)
    }

    override fun clear() = presetMap.clear()
    fun getKeys() = presetMap.keys.map { it.replace(" ", "_") }

}