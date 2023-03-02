package net.starly.qm.container

import net.starly.core.jb.container.STContainer
import net.starly.core.jb.container.button.STButton.STButtonBuilder
import net.starly.core.jb.container.wrapper.InventoryClickEventWrapper
import net.starly.qm.QuickMenu
import net.starly.qm.data.PresetData
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory

class ButtonSettingContainer(
    private val plugin: QuickMenu,
    private val preset: PresetData
): STContainer(
    if(preset.position.count > 9) 18 else 9,
    "${preset.key} [설정]", true
) {

    private var check = false

    override fun guiClick(p0: InventoryClickEventWrapper?) {
    }

    override fun guiClose(p0: InventoryCloseEvent) {
        if(!check) MainContainer(plugin, preset).open(p0.player as Player)
    }

    override fun guiDrag(p0: InventoryDragEvent?) {
    }

    override fun initializingInventory(inventory: Inventory) {
        inventory.setItem(0, preset.buttons[0].headItem)
        preset.buttons.forEachIndexed { index, button ->
            STButtonBuilder(button.headItem)
                .setClickFunction { event, _ ->
                    check = true
                    event.player.closeInventory()
                    ButtonSettingInnerContainer(plugin, preset, button).open(event.player)
                }.setDisplayName("§6${index + 1} 번 버튼 설정")
                .setLore(listOf("",
                    "§e ▸ §f클릭시 상세설정을 할 수 있습니다.","§e ▸ §f버튼 이름 : §a" + if(button.description.isNotEmpty()) button.description else "§7없음",
                    "§e ▸ §f설정 명령어 : §a" + (button.command?:"§7없음"),
                    "§e ▸ §f마우스 오버 사운드 : §a" + button.selectSound,
                    "§e ▸ §f마우스 클릭 사운드 : §a" + button.targetSound,
                    ""))
                .build().setSlot(this, index)
        }
    }
}