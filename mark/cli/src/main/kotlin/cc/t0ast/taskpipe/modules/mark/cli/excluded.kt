package cc.t0ast.taskpipe.modules.mark.cli

import cc.t0ast.taskpipe.modules.mark.Marking
import java.io.File

fun applyExclusion(
    exclude: Boolean,
    editedEntryNames: Iterable<String>,
    markings: Map<String, Marking>
) {
    val rootDir = File(System.getProperty("user.dir"))
    val contentDir = File(rootDir, "content")
    val excludedDir = File(rootDir, "module_data/excluded_entries")

    markings
        .values
        .filter { entry -> editedEntryNames.contains(entry.name) }
        .forEach { entry ->
            entry.isInExcludedDir = exclude

            val entryInContentDir = File(contentDir, entry.name)
            val entryInExcludedDir = File(excludedDir, entry.name)

            if (exclude) {
                if (entryInContentDir.exists()) {
                    entryInContentDir.copyRecursively(entryInExcludedDir, true)
                    entryInContentDir.deleteRecursively()
                }
            } else {
                if (entryInExcludedDir.exists()) {
                    entryInExcludedDir.copyRecursively(entryInContentDir, true)
                    entryInExcludedDir.deleteRecursively()
                }
            }
        }
}
