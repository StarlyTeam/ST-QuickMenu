package net.starly.qm.extension

import java.text.DecimalFormat

private val doubleFormat = DecimalFormat("#,###.#")
fun Float.toFormattedString() = doubleFormat.format(this)
fun Double.toFormattedString() = doubleFormat.format(this)