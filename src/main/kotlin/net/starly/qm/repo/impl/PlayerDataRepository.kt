package net.starly.qm.repo.impl

import net.starly.qm.data.PlayerStateData
import net.starly.qm.repo.DataRepository
import java.util.UUID
import java.util.concurrent.locks.ReentrantLock

class PlayerDataRepository: DataRepository<UUID, PlayerStateData> {

    private val lock: ReentrantLock = ReentrantLock()
    private val dataMap = HashMap<UUID, PlayerStateData>()

    @Deprecated("NotSupported")
    override fun clear() {}

    override fun get(key: UUID): PlayerStateData? =
        dataMap[key]

    override fun register(key: UUID, value: PlayerStateData) {
        lock.lock()
        dataMap[key] = value
        lock.unlock()
    }

    override fun unregister(key: UUID): PlayerStateData? {
        lock.lock()
        val result = dataMap.remove(key)
        lock.unlock()
        return result
    }

    override fun iterator(filter: (Map.Entry<UUID,PlayerStateData>)->Boolean): Iterator<Map.Entry<UUID, PlayerStateData>> =
        dataMap.filter(filter).iterator()

    override fun iterator(): Iterator<Map.Entry<UUID, PlayerStateData>> =
        dataMap.iterator()

    override fun isEmpty(): Boolean =
        dataMap.isEmpty()

    override fun contains(key: UUID): Boolean =
        dataMap.containsKey(key)

}