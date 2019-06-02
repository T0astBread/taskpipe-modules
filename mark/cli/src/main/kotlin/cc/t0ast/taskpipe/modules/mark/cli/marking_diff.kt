package cc.t0ast.taskpipe.modules.mark.cli

import cc.t0ast.taskpipe.modules.mark.Marking

typealias MarkingDiff = Map<String, Boolean>

fun String.toMarkingDiff(): MarkingDiff =
    this.split(Regex("\\s+"))
        .map { word -> word.substring(1) to (word[0] == '+') }
        .toMap()

fun MarkingDiff.applyTo(editedEntryNames: Iterable<String>, entries: MutableMap<String, Marking>) {
    editedEntryNames.forEach { entryName ->
        entries.putIfAbsent(entryName, Marking(entryName, false, mutableListOf()))
    }

    entries.values
        .filter { entry -> editedEntryNames.contains(entry.name) }
        .forEach { entry ->
            this.entries.forEach { markingDiffEntry ->
                if (markingDiffEntry.value)
                    entry.markings.add(markingDiffEntry.key)
                else
                    entry.markings.remove(markingDiffEntry.key)
            }
        }
}
