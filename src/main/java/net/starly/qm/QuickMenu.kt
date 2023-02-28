package net.starly.qm

import net.starly.core.bstats.Metrics
import org.bukkit.plugin.java.JavaPlugin

class QuickMenu : JavaPlugin() {

    companion object { internal lateinit var plugin: QuickMenu }
    override fun onEnable() {
        // DEPENDENCY
        if(server.pluginManager.getPlugin("ST-Core") == null) {
            logger.apply {
                warning("[$name] ST-Core 플러그인이 적용되지 않았습니다! 플러그인을 비활성화합니다.")
                warning("[$name] 다운로드 링크 : §fhttp://starly.kr/discord")
            }
        }
        plugin = this
        Metrics(this, 12345) // TODO: 수정

        // CONFIG
        // TODO: 작성

        // COMMAND
        // TODO: 작성

        // EVENT
        // TODO: 작성
    }

}