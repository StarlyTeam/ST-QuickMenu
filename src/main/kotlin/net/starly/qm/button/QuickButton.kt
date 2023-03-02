package net.starly.qm.button

import net.starly.core.jb.version.nms.tank.NmsOtherUtil
import net.starly.core.jb.version.nms.wrapper.ArmorStandWrapper
import net.starly.qm.context.ON_TARGET_MOVE_DISTANCE
import net.starly.qm.context.TARGET_CORRECTION_VALUE
import net.starly.qm.data.ButtonData
import net.starly.qm.data.PlayerStateData
import net.starly.qm.loader.impl.ConfigLoader
import net.starly.qm.setting.impl.message.MessageSetting
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class QuickButton(
    val target: Player,
    val button: ButtonData,
    val data: PlayerStateData,
    startLocation: Location,
    private val genLocation: Location,
    plugin: JavaPlugin
) {

    private val armorStand = NmsOtherUtil.createArmorStandInstance(startLocation)
    var targeted = false
        private set
    private var defaultHeadPose: ArmorStandWrapper.HeadPoseWrapper? = null
    private val messageBox: MessageSetting = ConfigLoader.get(null, MessageSetting::class.java)

    init {
        armorStand.invisible = true
        armorStand.small = true
        armorStand.displayName = button.description
        armorStand.customNameVisible = button.description.isNotEmpty()
        genLocation.yaw = target.location.yaw + 180f
        armorStand.helmet = button.headItem
        armorStand.spawn(target)
        plugin.server.scheduler.runTaskLaterAsynchronously(plugin, {
            armorStand.teleport(target, genLocation, true)
            defaultHeadPose = armorStand.getHeadPose()
        }, 1L)

    }

    fun update(target: Player) {
        val eyeLocation = genLocation.getEye()
        if(target.isWatching(eyeLocation)) {
             if(!targeted) {
                 armorStand.displayName = messageBox.get("selected-button-prefix") + button.description + messageBox.get("selected-button-suffix")
                 button.onTarget(target)
                 armorStand.teleport(target, genLocation.getFrontLocationAt(target.location))
             } else {
                 val tempPose = armorStand.getHeadPose()
                 armorStand.setHeadPose(ArmorStandWrapper.HeadPoseWrapper(tempPose.x, tempPose.y + 15f, tempPose.z))
                 armorStand.applyMeta(target)
             }
            targeted = true
        } else {
            if(targeted) {
                armorStand.displayName = messageBox.get("default-button-prefix") + button.description + messageBox.get("default-button-suffix")
                armorStand.teleport(target, genLocation)
                armorStand.resetHeadPose()
                armorStand.applyMeta(target)
            }
            targeted = false
        }
    }

    fun remove() = armorStand.remove(target)

    private fun Player.isWatching(location: Location): Boolean {
        val toEntity = location.toVector().subtract(eyeLocation.toVector())
        val dot = toEntity.normalize().dot(eyeLocation.direction)
        return dot > TARGET_CORRECTION_VALUE
    }

    private fun Location.getFrontLocationAt(other: Location): Location {
        val otherVector = other.direction
        val vector = direction
        otherVector.subtract(vector)
        return clone().add(vector.normalize().multiply(-ON_TARGET_MOVE_DISTANCE))
    }

    private fun Location.getEye(): Location = clone().add(.0, .85, .0)

}