package cc.t0ast.taskpipe.modules.format

import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val baseDir = File(System.getProperty("user.dir"))
            val contentDir = File(baseDir, "content")

            formatEntry(baseDir)

            // Format entries
            contentDir.listFiles()
                .forEach { formatEntry(it) }
        }

        private fun formatEntry(entryDir: File) {
            val contentDir = entryDir.resolve("content")
            if(!contentDir.exists())
                contentDir.mkdir()

            // Move all files except for module_data dir and content dir itself into content dir
            entryDir.listFiles()
                .filterNot { it.isDirectory && (it.name == "module_data" || it.name == "content") }
                .forEach { Files.move(it.toPath(), contentDir.resolve(it.name).toPath(), StandardCopyOption.REPLACE_EXISTING) }

            val moduleDataDir = entryDir.resolve("module_data")
            if(!moduleDataDir.exists())
                moduleDataDir.mkdir()
        }
    }
}