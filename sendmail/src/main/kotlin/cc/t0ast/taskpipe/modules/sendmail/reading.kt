package cc.t0ast.taskpipe.modules.sendmail

import java.nio.file.Files
import java.nio.file.Path

fun findMails(basePath: Path) =
        findMailfiles(basePath)
                .map { file -> file.readLines() }
                .map { lines -> parseMailfile(lines.asSequence()) }

private fun findMailfiles(basePath: Path) =
        Files.walk(basePath)
                .map { it.toFile() }
                .filter { file -> file.isFile && file.name.endsWith(".mailfile") }

private fun parseMailfile(lines: Sequence<String>): Email {
    var recipient: String? = null
    var subject: String? = null
    var body: String?

    var i = 0
    for(line in lines) {
        if(line.isBlank()) break

        match(line, cases = *arrayOf(
            "^To:\\s+(.*)" to { r -> recipient = r.groupValues[1] },
            "^Subject:\\s+(.*)" to { r -> subject = r.groupValues[1] }
        ))

        i++
    }

    body = lines.drop(i).joinToString("\n")

    return Email(recipient!!, subject!!, body)
}
