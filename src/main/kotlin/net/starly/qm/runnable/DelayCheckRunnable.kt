package net.starly.qm.runnable

import net.starly.qm.data.PlayerStateData
import net.starly.qm.enum.PlayerState
import net.starly.qm.repo.DataRepository
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class DelayCheckRunnable(
    private val repo: DataRepository<UUID, PlayerStateData>
): BukkitRunnable() {

    override fun run() {
        val iter = repo.iterator { it.value.state == PlayerState.DELAY }
        while(iter.hasNext()) {
            val next = iter.next()
            if(!next.value.process(false)) {
                repo.unregister(next.key)
            }
        }
    }

}