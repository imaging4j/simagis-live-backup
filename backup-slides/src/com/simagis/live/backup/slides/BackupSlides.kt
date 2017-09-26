package com.simagis.live.backup.slides

import java.nio.file.*

fun main(args: Array<String>) {
    val root = Paths.get(".")
    Files.walkFileTree(root, setOf<FileVisitOption>(), Int.MAX_VALUE, object: SimpleFileVisitor<Path>() {
    })
}