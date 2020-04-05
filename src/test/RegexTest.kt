package test

import main.utils.getIncludeEntryRegex
import main.utils.getPathToAddNewConf

fun main() {
    val entry = "#Include conf/optima/virtual/POGGOM.conf"
    println(getPathToAddNewConf(entry, "XD"))
}

