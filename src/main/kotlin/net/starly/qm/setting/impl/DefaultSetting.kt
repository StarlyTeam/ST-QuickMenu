package net.starly.qm.setting.impl

import net.starly.qm.setting.Setting
import org.bukkit.configuration.ConfigurationSection

class DefaultSetting : Setting {

    enum class OpenType(
        private val cfgLabel: String,
    ) {
        SHIFT_F("SHIFT-F"),
        ICON("ICON"),
        COMMAND("COMMAND");

        fun equalsLabel(label: String) = cfgLabel.equals(label, true)
    }

    override val sectionKey: String = "default"
    private var invalid: Boolean = true

    lateinit var openTypeList: List<OpenType>
    fun isOpenType(openType: OpenType): Boolean = openTypeList.contains(openType)

    lateinit var icon: IconSetting
        private set
    var preset: String = "기본 프리셋"
        private set

    override fun load(config: ConfigurationSection) {
        val section = config.getConfigurationSection(sectionKey) ?: return setInvalid()
        preset = section.getString("preset")
        try {
            val list = ArrayList<OpenType>()
            section.getStringList("open-type").forEach {
                list.add(OpenType.values().firstOrNull {
                        type -> type.equalsLabel(it)
                }?: return@forEach)
            }
            openTypeList = list
        } catch (_: Exception) { return setInvalid() }

        icon = IconSetting().apply { load(section) }
        invalid = icon.isInvalid()
    }

    override fun isInvalid(): Boolean = invalid
    private fun setInvalid() { invalid = true }

}