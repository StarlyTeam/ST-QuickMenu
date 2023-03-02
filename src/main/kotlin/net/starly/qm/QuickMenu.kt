package net.starly.qm

import net.starly.core.bstats.Metrics
import net.starly.qm.command.QuickMenuCommand
import net.starly.qm.data.PresetData
import net.starly.qm.data.position.PositionData
import net.starly.qm.data.position.impl.CirclePositionData
import net.starly.qm.data.position.impl.LinePositionData
import net.starly.qm.listener.QBIconHandleListener
import net.starly.qm.listener.QBLeftClickListener
import net.starly.qm.listener.QBPlayerMoveListener
import net.starly.qm.loader.impl.ConfigLoader
import net.starly.qm.repo.DataRepository
import net.starly.qm.repo.impl.PlayerDataRepository
import net.starly.qm.repo.impl.PositionDataRepository
import net.starly.qm.repo.impl.PresetDataRepository
import net.starly.qm.runnable.DelayCheckRunnable
import net.starly.qm.setting.impl.DefaultSetting
import org.bukkit.plugin.java.JavaPlugin

class QuickMenu : JavaPlugin() {

    companion object { internal lateinit var plugin: QuickMenu }
    internal val playerDataRepository by lazy { PlayerDataRepository() }
    internal val positionDataRepository by lazy { PositionDataRepository() }
    internal lateinit var presetDateRepository: PresetDataRepository
    internal lateinit var serverPreset: PresetData

    override fun onEnable() {
        // DEPENDENCY
        if(server.pluginManager.getPlugin("ST-Core") == null) {
            logger.apply {
                warning("[$name] ST-Core 플러그인이 적용되지 않았습니다! 플러그인을 비활성화합니다.")
                warning("[$name] 다운로드 링크 : §fhttp://starly.kr/discord")
            }
        }

        Metrics(this, 17837)

        plugin = this

        DelayCheckRunnable(playerDataRepository).runTaskTimerAsynchronously(this, 2L, 2L)
        generatePositions(positionDataRepository)
        presetDateRepository = PresetDataRepository(this)

        ConfigLoader.load(this)
        serverPreset = presetDateRepository.get(ConfigLoader.get(null, DefaultSetting::class.java).preset)

        server.pluginManager.apply {
            registerEvents(QBIconHandleListener(), this@QuickMenu)
            registerEvents(QBLeftClickListener(this@QuickMenu), this@QuickMenu)
            registerEvents(QBPlayerMoveListener(this@QuickMenu), this@QuickMenu)
        }

        QuickMenuSetter.initializingSetter(this)
        QuickMenuCommand(this)
    }

    private fun generatePositions(repo: DataRepository<String, PositionData>) {
        for(i in 2 .. 10) {
            repo.register("CIRCLE_$i", CirclePositionData("CIRCLE_$i", i))
            if(i < 8)
            repo.register("LINE_$i", LinePositionData("LINE_$i", i))
        }
    }

}