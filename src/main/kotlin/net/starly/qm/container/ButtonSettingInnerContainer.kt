package net.starly.qm.container

import net.starly.core.jb.container.STContainer
import net.starly.core.jb.container.button.STButton.STButtonBuilder
import net.starly.core.jb.container.wrapper.InventoryClickEventWrapper
import net.starly.core.jb.version.VersionController
import net.starly.qm.QuickMenu
import net.starly.qm.data.ButtonData
import net.starly.qm.data.PresetData
import net.starly.qm.data.SoundData
import net.starly.qm.extension.sendMessageAfterPrefix
import net.starly.qm.extension.toFormattedString
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.inventory.Inventory
import org.bukkit.scheduler.BukkitTask
import java.util.UUID

class ButtonSettingInnerContainer(
    private val plugin: QuickMenu,
    private val preset: PresetData,
    private val button: ButtonData,
) : STContainer(
    9,
    "${preset.key} [설정]", false
) {

    private var close: Boolean = true
    private var copyMode: Boolean = false
    companion object {
        private val copyMap = HashMap<UUID, MutableList<SoundData?>>()
        private fun getCopy(player: Player, index: Int): SoundData? = copyMap[player.uniqueId]?.get(index)
        private fun registerCopy(player: Player, index: Int, sound: SoundData) {
            copyMap.computeIfAbsent(player.uniqueId) { MutableList(9) { null } }[index] = sound
        }
    }
    override fun guiDrag(p0: InventoryDragEvent?) {}
    override fun guiClick(p0: InventoryClickEventWrapper) {}
    override fun guiClose(p0: InventoryCloseEvent) {
        button.setHeadItem(inventory.getItem(0)?.run { if (type == Material.AIR) null else this })
        if (close) ButtonSettingContainer(plugin, preset).open(p0.player as Player)
    }

    override fun initializingInventory(inventory: Inventory) {
        close = true
        STButtonBuilder(Material.BARRIER).setDisplayName("§f").setCancelled(true).setCleanable(false).build()
            .setSlot(this, 3,4,5)
        val copyListLore = ArrayList<String>()
        copyListLore.addAll(listOf("", "§e ▸ §f현재 모드 : ${if(copyMode) "§a복사모드" else "§6붙여넣기모드"}",""))
        for(i in 0 .. 8) {
            copyListLore.add("§a[${i + 1}] " + (getCopy(viewer, i)?.run { "${this.sound?.name?:"§7타입없음"}, ${this.volume.toFormattedString()} ,${this.pitch.toFormattedString()}" }?:"§7비어있음"))
        }
        copyListLore.addAll(listOf("","§e ▸ §f클릭하여 모드를 변경할 수 있습니다.", ""))
        STButtonBuilder(Material.PAPER)
            .setGlow(copyMode)
            .setDisplayName("§6사운드 복사 목록 ${if(copyMode) "§a§l[복사]" else "§6§l[붙여넣기]"}")
            .setLore(copyListLore)
            .setClickFunction { _, _ ->
                copyMode = !copyMode
                refresh()
            }
            .setCleanable(false)
            .setCancelled(true)
            .build().setSlot(this, 6)
        inventory.setItem(0, button.headItem)
        STButtonBuilder("1c0646a28b451da74394e698b04fac935ba1975f42829067a0fbfed181a36594")
            .setDisplayName("§6버튼 아이콘 설정")
            .setCleanable(false)
            .setCancelled(true)
            .setLore(
                listOf(
                    "",
                    "§e ▸ §f왼쪽에 아이템을 등록하면 버튼의 아이콘으로 설정됩니다.",
                    "§e ▸ §f아이콘을 비워두면 자동으로 기본§7(베리어)§f 아이콘이 됩니다.",
                    "§e ▸ §f아이콘의 제목도 자동으로 해당 아이템의 이름으로 설정됩니다.", ""
                )
            ).build().setSlot(this, 1)
        STButtonBuilder("ee6ee34873d353eb0f58c309ce60789dd425700b28b6f7ce2186c9794c80513d")
            .setDisplayName("§6명령어 설정")
            .setCleanable(false).setCancelled(true)
            .setClickFunction { event, _ ->
                event.player.sendMessageAfterPrefix("§f설정 할 명령어를 입력하세요.")
                event.player.sendMessage(" §7 플레이어 닉네임이 명령어에 포함된다면 %player% 를 사용할 수 있습니다.")
                event.player.sendMessage(" §7 취소 : '취소' 입력")
                event.player.sendMessage(" §7 삭제 : '-' 입력")
                registerCheckListener(event.player) {
                    if(it == "-") {
                        button.command = null
                        event.player.sendMessageAfterPrefix("§a커맨드가 삭제 되었습니다.")
                        refresh()
                    } else if(it != "취소") {
                        button.command = it
                        event.player.sendMessageAfterPrefix("§a커맨드가 등록 되었습니다.")
                        refresh()
                    }
                    open(event.player)
                }
            }
            .setLore(
                listOf(
                    "",
                    "§e ▸ §f클릭시, 실행 될 명령어를 설정할 수 있습니다.",
                    "§e ▸ §f현재 명령어 : §e${button.command ?: "§7없음"}", ""
                )
            ).build().setSlot(this, 2)
        listOf(button.targetSound to "마우스 오버", button.selectSound to "마우스 클릭").forEachIndexed { index, pair ->
            val soundLore = ArrayList<String>()
            soundLore.addAll(listOf(
                "",
                "§e ▸ §f버튼 ${pair.second}시, 출력 될 사운드를 설정합니다.",
                "§e ▸ §f현재 사운드 : §e${pair.first.sound?.name ?: "§7없음"}",
                "§e ▸ §f볼륨 : §e${pair.first.volume.toFormattedString()}",
                "§e ▸ §f피치 : §e${pair.first.pitch.toFormattedString()}",
                "",
                "§e ▸ §f왼클릭/좌클릭 : §a볼륨 설정",
                "§e ▸ §f쉬프트+왼클릭/좌클릭 : §a피치 설정",
                "§e ▸ §f숫자키 : §a사운드 ${if(copyMode) "복사" else "붙여넣기"}",
                "§e ▸ §f휠클릭 : §a사운드 타입 설정",
                "§e ▸ §f버리기 : §c소리 삭제",
                ""
            ))
            STButtonBuilder("f22e40b4bfbcc0433044d86d67685f0567025904271d0a74996afbe3f9be2c0f")
                .setDisplayName("§6${pair.second} 사운드 설정")
                .setCancelled(true)
                .setCleanable(false)
                .setLore(soundLore)
                .setClickFunction { event, _ ->
                    val player = event.player
                    when {
                        event.hotbarButton >= 0 -> {
                            if(copyMode) {
                                registerCopy(player, event.hotbarButton, SoundData(pair.first.sound, pair.first.volume, pair.first.pitch))
                                player.sendMessageAfterPrefix("§a복사 되었습니다.")
                                refresh()
                            } else {
                                val data = getCopy(player, event.hotbarButton)
                                if(data == null) player.sendMessageAfterPrefix("§c복사 된 사운드가 없습니다.")
                                else {
                                    pair.first.apply {
                                        sound = data.sound
                                        volume = data.volume
                                        pitch = data.pitch
                                    }
                                    player.sendMessageAfterPrefix("§a붙여넣었습니다.")
                                    data.playSound(player)
                                    refresh()
                                }
                            }
                        }

                        event.isShiftLeft -> {
                            pair.first.pitch -= 0.1f
                            if (pair.first.pitch < .009999) pair.first.pitch = .0f
                            pair.first.playSound(player)
                            refresh()
                        }

                        event.isShiftRight -> {
                            pair.first.pitch += 0.1f
                            if (pair.first.pitch > 2.5) pair.first.pitch = 2.5f
                            pair.first.playSound(player)
                            refresh()
                        }

                        event.isLeft -> {
                            pair.first.volume -= 0.1f
                            if (pair.first.volume < .009999) pair.first.volume = .0f
                            pair.first.playSound(player)
                            refresh()
                        }

                        event.isRight -> {
                            pair.first.volume += 0.1f
                            if (pair.first.volume > 2.5) pair.first.volume = 2.5f
                            pair.first.playSound(player)
                            refresh()
                        }

                        event.isWheel -> {
                            player.sendMessageAfterPrefix("§f설정 할 사운드를 아래의 사이트에 있는 목록 중 선택하여 입력하세요.")
                            player.sendMessage(" §a§nhttps://helpch.at/docs/${VersionController.getInstance().version.v}/org/bukkit/Sound.html")
                            player.sendMessage(" §7 취소 : '취소' 입력")
                            player.sendMessage(" §7 삭제 : '-' 입력")
                            registerCheckListener(player) {
                                when (it) {
                                    "취소" -> {}
                                    "-" -> {
                                        pair.first.sound = null
                                        player.sendMessageAfterPrefix("§a사운드가 삭제 되었습니다.")
                                        refresh()
                                    }
                                    else -> try {
                                        pair.first.sound = Sound.valueOf(it)
                                        player.sendMessageAfterPrefix("§a사운드가 설정 되었습니다.")
                                        pair.first.playSound(player)
                                        refresh()
                                    } catch (_: Exception) {
                                        player.sendMessageAfterPrefix("§c찾을 수 없는 사운드입니다.")
                                    }
                                }
                                open(player)
                            }
                        }

                        event.clickType == ClickType.DROP -> {
                            pair.first.sound = null
                            refresh()
                        }
                    }
                }.build().setSlot(this, 7 + index)
        }

    }

    private fun registerCheckListener(player: Player, runBlock: (String)->Unit) {
        var task: BukkitTask? = null
        var unregisterFunc: (()->Unit)? = null
        close = false
        player.closeInventory()
        val listener: Listener = object : Listener {
            @EventHandler
            fun onChat(event: AsyncPlayerChatEvent) {
                if(event.player.name != player.name) return
                event.isCancelled = true
                task?.cancel()
                unregisterFunc?.invoke()
                runBlock(event.message)
            }

            @EventHandler
            fun onCommand(event: PlayerCommandPreprocessEvent) {
                if(event.player.name == player.name) {
                    event.isCancelled = true
                    event.player.sendMessageAfterPrefix("§c이 전에 하던 설정을 마치고 행동하는 것이 좋습니다.")
                }
            }
        }
        unregisterFunc = {
            try {
                AsyncPlayerChatEvent.getHandlerList().unregister(listener)
                PlayerCommandPreprocessEvent.getHandlerList().unregister(listener)
            } catch (ignored: Exception) {}
        }
        task = plugin.server.scheduler.runTaskLater(plugin, unregisterFunc, 3000L)
        plugin.server.pluginManager.registerEvents(listener, plugin)

    }

}