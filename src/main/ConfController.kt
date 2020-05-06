package main

import main.utils.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.system.exitProcess

fun main() {
    ConfController().menu()
}

class ConfController {

    private lateinit var pathToHttpdConf: String
    private lateinit var _sourceToInactive: List<String>
    private lateinit var regex: Regex
    private lateinit var httpdFile: File

    init {
        initProperties()
    }

    private fun initProperties() {
        val propsDir = "files/conf.properties"
        Files.newInputStream(Paths.get(propsDir))
            .bufferedReader()
            .use { it ->
                val properties = Properties()
                properties.load(it)
                pathToHttpdConf = properties.getProperty("directory")
                _sourceToInactive = getSourceConfList(pathToHttpdConf)
                    .map { if (!it.startsWith("#")) "#$it" else it }
                regex = getIncludeEntryRegex(_sourceToInactive[0]).toRegex()
                httpdFile = File(pathToHttpdConf)
            }
    }

    private fun showAllConfs() {
        getSourceConfList(pathToHttpdConf).mapIndexed { index, s ->
            val match = regex.matchEntire(s)?.groupValues?.get(1)
            val conf = "[$index] $match"
            if (!s.startsWith("#")) "$conf [ACTIVE]" else conf
        }.forEach(::println)
        separation()
    }

    private fun separation() {
        println("##################### \n".repeat(3))
    }

    private fun markAllAsInactive() {
        httpdFile.writeCollectionContent(_sourceToInactive)
    }

    private fun rewriteConfsFileOnAdd(newEntry: String) {
        markAllAsInactive()
        val mutableConfList = _sourceToInactive.toMutableList()
        mutableConfList.add(0, newEntry)
        httpdFile.writeCollectionContent(mutableConfList)
    }

    private fun markConfAsActive() {
        showAllConfs()
        val indexToChange = readLine("Select conf to ACTIVATE (index) -> ").toInt()
        markAllAsInactive()
        httpdFile.writeCollectionContent(_sourceToInactive.mapIndexed { index, s ->
            if (index == indexToChange) {
                s.replace("#", "")
            } else s
        })
        println("Configuration file [${_sourceToInactive[indexToChange]}] marked as ACTIVE")
        separation()
    }

    private fun deleteConf() {
        showAllConfs()
        val indexToDelete = readLine("Select conf to DELETE (index) -> ").toInt()
        val confToRemove = _sourceToInactive[indexToDelete]
        httpdFile.writeCollectionContent(
            getSourceConfList(pathToHttpdConf)
                .filterIndexed { index, _ ->
                    index != indexToDelete
                }
        )
        println("Configuration file [${confToRemove}] deleted successfully")
        separation()
    }

    private fun addNewConf() {
        val vhostName = readLine("Introduce the name of the vhost file -> ")
        val newConf = getPathToAddNewConf(
            _sourceToInactive[0],
            vhostName
        )
        rewriteConfsFileOnAdd(newConf)
        println("Configuration file [${newConf}] added successfully")
        separation()
    }

    fun menu() {
        println("1 - Show all configurations")
        println("2 - Add new configuration")
        println("3 - Delete configuration")
        println("4 - Mark configuration as ACTIVE")
        println("0 - Type anything else to exit")
        when (readLine("Select option: ")) {
            "1" -> {
                showAllConfs()
                menu()
            }
            "2" -> {
                addNewConf()
                menu()
            }
            "3" -> {
                deleteConf()
                menu()
            }
            "4" -> {
                markConfAsActive()
                menu()
            }
            else -> {
                exitProcess(0)
            }
        }
    }

}