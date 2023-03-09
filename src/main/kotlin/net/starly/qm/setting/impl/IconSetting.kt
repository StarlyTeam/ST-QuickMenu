package net.starly.qm.setting.impl

import net.starly.core.jb.util.PlayerSkullManager
import net.starly.core.jb.version.nms.tank.NmsItemStackUtil
import net.starly.qm.setting.Setting
import net.starly.qm.extension.toColoredString
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class IconSetting: Setting {

    override val sectionKey: String = "icon"
    lateinit var headIcon: ItemStack
        private set
    private var invalid = true
    var slot: Int = 5
        private set

    fun setPlayer(player: Player) { player.inventory.setItem(slot, headIcon.clone()) }

    @Suppress("UsePropertyAccessSyntax")
    override fun load(config: ConfigurationSection) {
        val section = config.getConfigurationSection(sectionKey)?: return setInvalid()
        val url = section.getString("url")
        val display = section.getString("display").toColoredString()
        val lore = section.getStringList("lore").map(String::toColoredString)
        slot = section.getInt("slot")
        try {
            headIcon = PlayerSkullManager.getCustomSkull(url).apply {
                val util = NmsItemStackUtil.getInstance()!!
                itemMeta = util.asNMSCopy(this)!!.run {
                    val nbt = tag ?: util.nbtCompoundUtil.newInstance()
                    nbt.setString("ST-QuickButton", "Icon")
                    tag = nbt
                    util.asBukkitCopy(this).itemMeta.apply {
                        setDisplayName(display)
                        setLore(lore)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return setInvalid()
        }
        invalid = false
    }

    override fun isInvalid(): Boolean = invalid
    private fun setInvalid() { invalid = true }


}