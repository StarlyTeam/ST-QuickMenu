package net.starly.qm.runnable

import org.bukkit.scheduler.BukkitRunnable

abstract class CallbackRunnable<T>: BukkitRunnable() {
    protected abstract val callbackFunction: (T)->Unit

    private var cancelled = false

    final override fun run() {
        if(cancelled) return
        runBlock()
    }

    abstract fun runBlock()

    protected fun cancel(result: T) {
        callbackFunction(result)
        cancelled = true
        cancel()
    }

    @Deprecated("Cancel-with-result", replaceWith = ReplaceWith("cancel(T)"))
    final override fun cancel() { super.cancel() }

}