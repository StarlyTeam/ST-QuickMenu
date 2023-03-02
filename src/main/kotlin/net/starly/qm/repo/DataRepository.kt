package net.starly.qm.repo

interface DataRepository<K, V> {

    fun get(key: K): V?
    fun register(key: K, value: V)
    fun unregister(key: K): V?
    fun iterator(filter: (Map.Entry<K, V>)->Boolean): Iterator<Map.Entry<K, V>>
    fun iterator(): Iterator<Map.Entry<K, V>>
    fun isEmpty(): Boolean
    fun contains(key: K): Boolean
    fun clear()

}