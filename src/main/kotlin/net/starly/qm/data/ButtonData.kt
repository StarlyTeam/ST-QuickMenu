package net.starly.qm.data

import net.starly.core.jb.util.ItemStackNameUtil
import net.starly.core.jb.util.PlayerSkullManager
import net.starly.qm.context.NONE_BUTTON_URL
import net.starly.qm.data.stream.Readable
import net.starly.qm.data.stream.Writeable
import net.starly.qm.loader.impl.ConfigLoader
import net.starly.qm.setting.impl.message.MessageSetting
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream

@Suppress("UsePropertyAccessSyntax")
class ButtonData: Writeable, Readable {

    companion object {
        private val defaultHead by lazy { PlayerSkullManager.getCustomSkull(NONE_BUTTON_URL) }
    }

    lateinit var headItem: ItemStack
        private set
    var command: String? = null
    lateinit var targetSound: SoundData
        private set
    lateinit var selectSound: SoundData
        private set

    lateinit var description: String
        private set

    constructor()
    constructor(
        headItem: ItemStack?,
        command: String? = null,
        targetSound: SoundData? = null,
        selectSound: SoundData? = null
    ) {
        this.headItem = headItem?: defaultHead
        description = this.headItem.run { if(hasItemMeta() && itemMeta.hasDisplayName()) itemMeta.getDisplayName() else "" }
        this.command = command
        this.targetSound = targetSound?: SoundData(null, 1f, 1f)
        this.selectSound = selectSound?: SoundData(null, 1f, 1f)
    }

    fun setHeadItem(item: ItemStack?) {
        headItem = item?: defaultHead
        description = this.headItem.run { if(hasItemMeta() && itemMeta.hasDisplayName()) itemMeta.getDisplayName() else "" }
    }

    fun onTarget(player: Player) { targetSound.playSound(player) }
    fun onSelect(player: Player) {
        selectSound.playSound(player)
        command?.apply { runOpCommand(player) }?: ConfigLoader.get(null, MessageSetting::class.java).sendMessage(player, "none-command")
    }

    private fun runOpCommand(player: Player) {
        command?.apply {
            val isOp = player.isOp
            player.isOp = true
            try { player.performCommand(replace("%player%", player.name)) }
            finally { if(!isOp) player.isOp = false }
        }
    }

    override fun read(buf: BukkitObjectInputStream) {
        headItem = buf.readObject() as ItemStack
        description = headItem.run { if(hasItemMeta() && itemMeta.hasDisplayName()) itemMeta.getDisplayName() else "" }
        command = buf.readUTF().run { ifEmpty { null } }
        targetSound = SoundData().apply { read(buf) }
        selectSound = SoundData().apply { read(buf) }
    }

    override fun write(buf: BukkitObjectOutputStream) {
        buf.writeObject(headItem)
        buf.writeUTF(command?: "")
        targetSound.write(buf)
        selectSound.write(buf)
    }

}