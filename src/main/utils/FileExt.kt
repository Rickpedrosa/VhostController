package main.utils

import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.stream.Collectors

fun <T> File.writeCollectionContent(collection: List<T>) {
    this.bufferedWriter().use { out -> collection.forEach { out.write("$it\n") } }
}

fun readLine(info: String): String {
    print(info)
    return readLine() ?: ""
}

fun getSourceConfList(path: String): List<String> {
    return Files.newInputStream(Paths.get(path))
        .bufferedReader()
        .lines()
        .collect(Collectors.toList())
}