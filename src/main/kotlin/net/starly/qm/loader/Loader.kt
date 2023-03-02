package net.starly.qm.loader

import net.starly.qm.QuickMenu

interface Loader<S> {

    fun load(plugin: QuickMenu)
    fun <T: S> get(obj: T?, clazz: Class<T>): T

}