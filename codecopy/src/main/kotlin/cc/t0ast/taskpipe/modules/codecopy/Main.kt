package cc.t0ast.taskpipe.modules.codecopy

import java.io.File
import java.nio.file.Files
import java.util.stream.Collectors

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            try {
                validateArgs(args)
            }
            catch(exception: IllegalArgumentException) {
                printUsage()
                System.exit(1)
            }

            val projectTemplateFile = File(args[0])
            val sourcesFile = File(args[1])
            val targetParent = File(args[2])
            val targetFile = File(targetParent, projectTemplateFile.name)

            projectTemplateFile.copyRecursively(targetFile, overwrite = false)
            deleteSources(targetFile)
            copySources(sourcesFile, targetFile)
        }

        private fun validateArgs(args: Array<String>) {
            if(args.size != 3)
                throw IllegalArgumentException("Two args required")
        }

        private fun printUsage() {
            println("Usage:")
            println("\tcodecopy <project_template_path> <source_path> <target_path>")
            println()
            println("""
                project_template_path is the path to the project template (you
                don't need to remove the sources in advance)

                source_path is the path to the sources you want to copy

                target_path is the path where the copied project should be
            """.trimIndent())
        }

        private fun deleteSources(projectDir: File) {
            val sourcesDir = File(projectDir, "src/main")
            sourcesDir.deleteRecursively()
        }

        private fun copySources(sourcesDir: File, targetProjectDir: File) {
            val sourceFiles = Files.walk(sourcesDir.toPath())
                    .map { it.toFile() }
                    .filter { it.isFile }
                    .collect(Collectors.toSet())
                    .toMutableSet()
            val targetSourceDir = File(targetProjectDir, "src/main")

            copySourcesOfType(targetSourceDir, sourceFiles, sourcesDir, "java", ".*\\.java")
            copySourcesOfType(targetSourceDir, sourceFiles, sourcesDir, "kotlin", ".*\\.kt")
            copySourcesOfType(targetSourceDir, sourceFiles, sourcesDir, "resources", ".*")
        }

        private fun copySourcesOfType(targetSourceDir: File, sourceFiles: MutableSet<File>, sourcesDir: File, typeSourceDir: String, typeFilePattern: String) {
            val targetJavaSourceDir = File(targetSourceDir, typeSourceDir)
            val typeFileRegex = Regex(typeFilePattern)
            sourceFiles.filter { file -> file.name.matches(typeFileRegex) }
                    .forEach { typeFile ->
                        val relativeSourceFilePath = typeFile.toRelativeString(sourcesDir)
                        val targetFile = File(targetJavaSourceDir, relativeSourceFilePath)
                        typeFile.copyTo(targetFile, overwrite = false)

                        sourceFiles.remove(typeFile)
                    }
        }
    }
}