package net.starly.qm.command

import net.starly.core.jb.annotation.Subcommand
import net.starly.core.jb.command.STCommand
import net.starly.core.jb.command.wrapper.CommandSenderWrapper
import net.starly.qm.QuickMenu
import net.starly.qm.QuickMenuSetter
import net.starly.qm.container.MainContainer
import net.starly.qm.data.PresetData
import net.starly.qm.data.position.PositionData
import net.starly.qm.loader.impl.ConfigLoader
import net.starly.qm.setting.impl.DefaultSetting
import net.starly.qm.setting.impl.message.MessageSetting
import java.io.File


class QuickMenuCommand(
    private val plugin: QuickMenu,
): STCommand(
    "quick-menu",
    "퀵 메뉴 명령어 입니다.", plugin
) {

    override fun executeDefaultCommand(sender: CommandSenderWrapper) {
        if(ConfigLoader.get(null, DefaultSetting::class.java).isOpenType(DefaultSetting.OpenType.COMMAND)) {
            QuickMenuSetter.start(sender.player, plugin.playerDataRepository, plugin)
        }
        super.executeDefaultCommand(sender)
    }

    @Subcommand(subCommand = "리로드", description = "구성 파일을 다시 읽습니다.", permission = "starly.qm.reload")
    fun reload(sender: CommandSenderWrapper) {
        ConfigLoader.load(plugin)
        sender.sendMessageAfterPrefix("§a구성 파일을 새로 읽어왔습니다.")
    }

    @Subcommand(subCommand = "적용", description = "프리셋을 서버에 적용합니다.", permission = "starly.qm.apply")
    fun applyPreset(sender: CommandSenderWrapper, preset: QuickMenuSetter.PresetKeyWrapper) {
        if(preset.preset == null) sender.sendMessageAfterPrefix("§c찾을 수 없는 프리셋입니다.")
        else {
            if(plugin.serverPreset.key == preset.preset.key) {
                sender.sendMessageAfterPrefix("§c이미 적용된 프리셋입니다.")
                return
            }
            plugin.serverPreset = preset.preset
            plugin.config.apply {
                set("default.preset", preset.preset.key)
                save(File(plugin.dataFolder, "config.yml"))
            }
            sender.sendMessageAfterPrefix("§a서버의 퀵메뉴로 적용되었습니다.")
        }
    }

    @Subcommand(subCommand = "수정", description = "생성 된 프리셋을 수정합니다.", permission = "starly.qm.modify")
    fun editPreset(sender: CommandSenderWrapper, preset: QuickMenuSetter.PresetKeyWrapper) {
        preset.preset?.apply {
            MainContainer(plugin, this).open(sender.player)
        }?: sender.sendMessageAfterPrefix("찾을 수 없는 프리셋입니다.")
    }

    @Subcommand(subCommand = "삭제", description = "생성 된 프리셋을 삭제합니다.", permission = "starly.qm.remove")
    fun removePreset(sender: CommandSenderWrapper, preset: QuickMenuSetter.PresetKeyWrapper) {
        if(preset.preset == null) sender.sendMessageAfterPrefix("§c존재하지 않는 프리셋입니다.")
        else {
            if(plugin.presetDateRepository.unregister(preset.str) != null) sender.sendMessageAfterPrefix("§a해당 프리셋을 삭제하였습니다.")
            else sender.sendMessageAfterPrefix("§c찾을 수 없는 프리셋이거나 기본 프리셋은 삭제할 수 없습니다.")
        }
    }

    @Subcommand(subCommand = "생성", description = "새로운 프리셋을 생성합니다.", permission = "starly.qm.create")
    fun createPreset(sender: CommandSenderWrapper, preset: QuickMenuSetter.PresetKeyWrapper, position: PositionData) {
        if(preset.preset == null) {
            plugin.presetDateRepository.register(preset.str, PresetData(preset.str.replace("_", " "), position.key, plugin))
            sender.sendMessageAfterPrefix("§a새로운 프리셋을 생성했습니다. §7(수정 명령어로 수정할 수 있습니다.)")
        } else sender.sendMessageAfterPrefix("§c이미 존재하는 프리셋입니다.")
    }

    private fun CommandSenderWrapper.sendMessageAfterPrefix(msg: String) =
        sendMessage(ConfigLoader.get(null, MessageSetting::class.java).getPrefix() + msg)

}