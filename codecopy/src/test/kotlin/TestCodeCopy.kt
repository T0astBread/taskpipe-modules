import cc.t0ast.taskpipe.modules.codecopy.Main
import org.junit.Test
import java.io.File

class TestCodeCopy {
    @Test
    fun testCopy() {
        // Check manually if everything is where it should be
        // the copied project can be found at <project_dir>/build/resources/test/copied/testproject

        val projectTemplatePath = javaClass.getResource("testproject").path
        val sourcesPath = javaClass.getResource("sources").path
        val sourcesDir = File(sourcesPath)
        val targetDir = File(sourcesDir.parent, "copied")

        targetDir.deleteRecursively()

        val args = arrayOf(projectTemplatePath, sourcesPath, targetDir.path)
        println(args.joinToString("\n"))
        Main.main(args)
    }
}