package net.starly.qm

import net.starly.core.StarlyCore
import net.starly.core.jb.command.STArgument
import net.starly.qm.button.QuickButton
import net.starly.qm.command.QuickMenuCommand
import net.starly.qm.data.PlayerStateData
import net.starly.qm.data.position.PositionData
import net.starly.qm.enum.CallBackReason
import net.starly.qm.loader.impl.ConfigLoader
import net.starly.qm.repo.impl.PlayerDataRepository
import net.starly.qm.repo.impl.PositionDataRepository
import net.starly.qm.repo.impl.PresetDataRepository
import net.starly.qm.runnable.RightClickCallBackRunnable
import net.starly.qm.setting.impl.message.MessageSetting
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

object QuickMenuSetter {

    class PresetKeyWrapper(val str: String, presetRepo: PresetDataRepository) { val preset = if(presetRepo.contains(str)) presetRepo.get(str) else null }
    class PositionKeyWrapper(str: String, positionRepo: PositionDataRepository) { val position = positionRepo.get(str) }

    fun initializingSetter(plugin: QuickMenu) {
        val presetRepo = plugin.presetDateRepository
        val positionRepo = plugin.positionDataRepository

        StarlyCore.getArgumentRepository().registerArguments(
            STArgument(PresetKeyWrapper::class.java, "프리셋", { p0 -> PresetKeyWrapper(p0, presetRepo) }) { presetRepo.getKeys().toList() },
            STArgument(PositionData::class.java, "포지션타입", { p0 -> PositionKeyWrapper(p0, positionRepo).position }) { positionRepo.getKeys() }
        )
    }

    fun start(player: Player, stateRepo: PlayerDataRepository, plugin: QuickMenu) {
        val preset = plugin.serverPreset
        preset.position.let { position ->
            val data = PlayerStateData(player.location.clone())
            stateRepo.register(player.uniqueId, data)
            player.addPotionEffect(PotionEffect(PotionEffectType.SLOW_DIGGING, Int.MAX_VALUE, 999, false, false))
            val list: MutableList<QuickButton> = ArrayList()
            val base = position.getBaseLocation(player)

            position.getLocations(player).forEachIndexed { index, loc ->
                list.add(QuickButton(player, preset.buttons[index], data, base, loc, plugin)) }

            runCallBackRunnable(player, data, list, stateRepo, plugin)
        }
    }

    private fun runCallBackRunnable(player: Player, data: PlayerStateData, buttons: List<QuickButton>, stateRepo: PlayerDataRepository, plugin: JavaPlugin) {
        val task = plugin.server.scheduler.runTaskTimerAsynchronously(
            plugin,
            Runnable { buttons.forEach { it.update(player) } },
            0L,
            2L
        )
        RightClickCallBackRunnable(stateRepo, player, player.inventory.heldItemSlot) {
            task.cancel()
            plugin.server.scheduler.runTask(plugin) { player.removePotionEffect(PotionEffectType.SLOW_DIGGING) }
            buttons.forEach(QuickButton::remove)
            when (it) {
                CallBackReason.CLICK_OFF, CallBackReason.CHANGE_SLOT -> {
                    data.delay()
                    buttons.firstOrNull { button -> button.targeted }?.apply {
                        plugin.server.scheduler.runTask(plugin) { button.onSelect(target) }
                    }
                }
                CallBackReason.TIME_OUT -> {
                    data.delay()
                    ConfigLoader.get(null, MessageSetting::class.java).sendMessage(player, "time-out")
                }
                CallBackReason.RUN_AWAY -> {
                    data.delay()
                    ConfigLoader.get(null, MessageSetting::class.java).sendMessage(player, "away-from-menu")
                }

                CallBackReason.OFFLINE -> stateRepo.unregister(player.uniqueId)
                CallBackReason.NULL_DATA -> {}
            }
        }.runTaskTimerAsynchronously(plugin, 1L, 1L)
    }

}