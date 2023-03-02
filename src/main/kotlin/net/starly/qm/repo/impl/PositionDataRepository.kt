package net.starly.qm.repo.impl

import net.starly.qm.data.position.PositionData
import net.starly.qm.repo.DataRepository

class PositionDataRepository: DataRepository<String, PositionData> {

    private val positionDataMap = HashMap<String, PositionData>()

    override fun get(key: String): PositionData? = positionDataMap[key]
    override fun register(key: String, value: PositionData) { positionDataMap[key] = value }
    override fun unregister(key: String): PositionData? = positionDataMap.remove(key)
    override fun isEmpty(): Boolean = positionDataMap.isEmpty()
    override fun contains(key: String): Boolean = positionDataMap.containsKey(key)
    override fun clear() = positionDataMap.clear()

    override fun iterator(filter: (Map.Entry<String, PositionData>) -> Boolean): Iterator<Map.Entry<String, PositionData>> =
        positionDataMap.filter(filter).iterator()
    override fun iterator(): MutableIterator<Map.Entry<String, PositionData>> =
        positionDataMap.iterator()

    fun getKeys(): List<String> = positionDataMap.keys.toList()

}