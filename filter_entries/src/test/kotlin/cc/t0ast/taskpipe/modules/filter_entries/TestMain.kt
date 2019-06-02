package cc.t0ast.taskpipe.modules.filter_entries

import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class TestMain {
    @Test
    fun testFindFailingEntries() {
        baseFile = File(System.getProperty("user.dir")).resolve("src/test/resources/test_pipeline_exec/content")
        val failingEntries = findFailing(Regex("src"))
        assertEquals(1, failingEntries.size)
        assertEquals("failing_entry", failingEntries[0].name)
    }
}