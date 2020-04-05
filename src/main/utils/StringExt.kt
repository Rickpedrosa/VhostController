package main.utils

fun String.symbolPosition(
    symbolNumber: Int = 0,
    symbol: Char = '/',
    included: Boolean = false
): Int {
    var offset = 0
    var counter = 0
    for ((index, char) in this.withIndex()) {
        if (char == symbol) counter++
        if (counter == symbolNumber) {
            offset = index
            break
        }
    }
    return if (included) offset + 1 else offset
}

fun getIncludeEntryRegex(entry: String): String {
    val includeToRegex = "#?Include\\s"
    var backSlashCounter = 0
    for (i in entry) {
        if (i == '/') backSlashCounter++
    }
    val pathToRegex = entry.substring(
        entry.indexOf(" "),
        entry.symbolPosition(backSlashCounter, included = true)
    ).trim()

    val vhostName = entry.substring(entry.symbolPosition(backSlashCounter, included = true))
    val vhostNameToRegex = vhostName
        .replace(".", "\\.")
        .replace(
            vhostName.substring(0, vhostName.indexOf(".")),
            "(.*)"
        )
    println((includeToRegex + pathToRegex + vhostNameToRegex))
    return (includeToRegex + pathToRegex + vhostNameToRegex)
}

fun getPathToAddNewConf(entry: String, vhost: String): String {
    var backSlashCounter = 0
    for (i in entry) {
        if (i == '/') backSlashCounter++
    }
    return "${entry.substring(
        0, entry.symbolPosition(backSlashCounter, included = true)
    ).replace("#", "")}$vhost.conf"
}