package com.simagis.live.backup.uploads

import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.sql.Timestamp
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val src = Paths.get(".").toRealPath()
    val dst = src.resolve(".backup-" + Timestamp(System.currentTimeMillis()).toString().replace(':', '-'))
    Files.walkFileTree(src, setOf<FileVisitOption>(), Int.MAX_VALUE, object : SimpleFileVisitor<Path>() {
        override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
            val path = src.relativize(dir)
            val names = path.map { it.toString() }
            if (names.isEmpty()) return FileVisitResult.CONTINUE
            if (names.size == 1) return when (names[0]) {
                "" -> FileVisitResult.CONTINUE
                "Users" -> FileVisitResult.CONTINUE
                else -> FileVisitResult.SKIP_SUBTREE
            }
            if (names[0] != "Users") return FileVisitResult.SKIP_SUBTREE
            val workspace = names.getOrNull(1) ?: return FileVisitResult.CONTINUE
            if (workspace.startsWith(".")) return FileVisitResult.SKIP_SUBTREE
            val syncSWS = names.getOrNull(2) ?: return FileVisitResult.CONTINUE
            if (syncSWS != ".syncSWS") return FileVisitResult.SKIP_SUBTREE
            val input = names.getOrNull(3) ?: return FileVisitResult.CONTINUE
            if (input != "input") return FileVisitResult.SKIP_SUBTREE
            val slide = names.getOrNull(4) ?: return FileVisitResult.CONTINUE

            print("backup: $path")
            val timeMillis = measureTimeMillis {
                dir.toFile()
                        .copyRecursively(dst
                                .resolve(workspace)
                                .resolve("uploads")
                                .resolve(slide)
                                .toFile())
            }
            println(" $timeMillis ms")

            return FileVisitResult.SKIP_SUBTREE
        }

    })
}