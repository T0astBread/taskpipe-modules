package cc.t0ast.taskpipe.modules.mark.cli

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import java.io.File

class Args(parser: ArgParser) {
    private val TRAILING_WINDOWS_PATH_FUCKERY = Regex("[\\/\\\\\"]$")  // Windows appends weird stuff to its paths sometimes and it breaks everything

    val markingDiff by parser.storing(
        "--apply", "-a",
        help = """
            Applies a marking diff to the specified entries


            Syntax: --apply {(+|-)marking1 (+|-)marking2 ...}
                - (minus) removes a marking,
                + (plus) adds a marking


            Examples:
                --apply +plagiarism
                --apply "-plagiarism +build_failed"
        """.trimIndent()
    ) { toMarkingDiff() }
        .default(mapOf())

    val excluded: Boolean? by parser.storing(
        "--excluded", "-x",
        help = "Excludes or un-excludes the specified entries (specify a boolean)"
    ) { toBoolean() }
        .default<Boolean?>(null)

    val editedEntries by parser.positionalList(
        help = "The entries to edit, denoted by a valid path to their entry directory"
    ) { File(this).name.replace(TRAILING_WINDOWS_PATH_FUCKERY, "") }
}