package net.starly.qm.container

import net.starly.core.jb.container.STContainer
import net.starly.core.jb.container.button.STButton.STButtonBuilder
import net.starly.core.jb.container.wrapper.InventoryClickEventWrapper
import net.starly.qm.QuickMenu
import net.starly.qm.data.PresetData
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory

class MainContainer(
    private val plugin: QuickMenu,
    private val preset: PresetData
): STContainer(
    9, "${preset.key} [설정]", true
) {

    private var save: Boolean = true
    override fun guiClick(p0: InventoryClickEventWrapper) {

    }

    override fun guiClose(p0: InventoryCloseEvent) {
        if(save) preset.save()
    }

    override fun guiDrag(p0: InventoryDragEvent) {

    }

    override fun initializingInventory(inventory: Inventory) {
        STButtonBuilder("1789b3e2868d716a921dec5932d530a892f600235f187766bc02d145ed16865b")
            .setDisplayName("§6버튼 설정")
            .setLore(listOf(
                "",
                "§e ▸ §f각 버튼에 대한 설정을 할 수 있습니다.",""
            )).setClickFunction { event, _ ->
                save = false
                event.player.closeInventory()
                ButtonSettingContainer(plugin, preset).open(event.player)
            }.build().setSlot(this, 0)

        STButtonBuilder("f22e40b4bfbcc0433044d86d67685f0567025904271d0a74996afbe3f9be2c0f")
            .setDisplayName("§6§m메뉴 오픈 사운드")
            .setLore(listOf(
                "",
                "§e ▸ §7준비중인 기능입니다.",
                ""))
            .build().setSlot(this, 1)
    }
}