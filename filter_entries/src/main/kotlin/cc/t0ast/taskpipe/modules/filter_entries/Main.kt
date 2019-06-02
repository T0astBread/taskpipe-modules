package cc.t0ast.taskpipe.modules.filter_entries

import cc.t0ast.taskpipe.modules.mark.Marking
import cc.t0ast.taskpipe.modules.mark.readMarkings
import cc.t0ast.taskpipe.modules.mark.saveMarkings
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

var baseFile: File = File(System.getProperty("user.dir"), "content")

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val pathRegex = Regex(args[0])
            val markNotMatching = args[1].toBoolean()

            val failingEntries = findFailing(pathRegex)

            println("Failing entries: ")
            failingEntries.forEach { println(it.relativeTo(baseFile).path) }

            if (markNotMatching) {
                val markings = readMarkings()
                val markingReason = args[2]

                val excludedDir = File(System.getProperty("user.dir"), "module_data/excluded_entries")
                excludedDir.mkdirs()

                failingEntries.forEach { file ->
                    val targetFile = File(excludedDir, file.name)
                    Files.move(file.toPath(), targetFile.toPath())

                    val entryName = file.name
                    val marking = markings.getOrPut(entryName) {
                        Marking(
                            name = file.name,
                            isInExcludedDir = true
                        )
                    }

                    marking.markings.add(markingReason)
                }

                saveMarkings(markings)
            } else {
                failingEntries.forEach { it.deleteRecursively() }
            }
        }
    }
}

fun findFailing(pathRegex: Regex) =
    baseFile.listFiles()
        .filterNot { it.matches(pathRegex) }

private fun File.matches(pathRegex: Regex): Boolean =
    if (this.isDirectory)
        toPath().matches(pathRegex)
    else
        relativeTo(baseFile).path.matches(pathRegex)

private fun Path.matches(pathRegex: Regex): Boolean =
    Files.walk(this)
        .map { it.toFile() }
        .map { it.relativeTo(this.toFile()).path }
        .anyMatch { it.matches(pathRegex) }
