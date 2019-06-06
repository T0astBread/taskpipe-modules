package cc.t0ast.taskpipe.modules.write_grading_mail

import cc.t0ast.taskpipe.modules.mark.Marking
import cc.t0ast.taskpipe.modules.mark.readMarkings
import cc.t0ast.taskpipe.modules.write_grading_mail.models.*
import com.google.gson.Gson
import org.simpleframework.xml.core.Persister
import java.io.File
import java.io.FileReader
import java.io.PrintStream


private val GSON = Gson()
private val XML_PERSISTER = Persister()


fun writeReports() {
    println("Writing reports...")

    val moodleDataFile = File(System.getProperty("user.dir"), "module_data/moodle_data.json")
    val moodleData = GSON.fromJson<MoodleData>(FileReader(moodleDataFile), MoodleData::class.java)

    val markings = readMarkings()

    val aggregateReportDir = File(System.getProperty("user.dir"), "module_data/reports")
    aggregateReportDir.mkdirs()

    val testRuns = mutableListOf<TestRun>()

    val aggregateReportFile = File(aggregateReportDir, "grading_report.txt")
    PrintStream(aggregateReportFile, "utf-8").use { aggregateReportFileStream ->
        moodleData.submissions.forEach { submission ->
            val submissionDir = findSubmissionEntryDir(submission.name) ?: return@forEach

            val marking = markings[submissionDir.name]

            val testRunData = readTestRun(submissionDir)
            testRunData.name = submission.name
            testRunData.entryDir = submissionDir.name
            testRuns.add(testRunData)

            val emailDataDir = File(submissionDir, "module_data/email")
            emailDataDir.mkdirs()
            val mailFile = File(emailDataDir, "report_mail.mailfile")
            PrintStream(mailFile, "utf-8").use { mailFileStream ->
                writeEmail(
                    moodleData.assignmentName,
                    submission,
                    marking,
                    testRunData,
                    mailFileStream
                )
            }

            val structuredTestReportFile = File(submissionDir, "module_data/unit_tests/test_results.json")
            structuredTestReportFile.writeText(GSON.toJson(testRunData))

            writeAggregateReportEntry(submission, marking, testRunData, aggregateReportFileStream)

            println("Wrote report for ${submission.name}")
        }
    }

    val aggregateReport = AggregateReport(
        Assignment(moodleData.assignmentID, moodleData.assignmentName),
        testRuns
    )
    val aggregateStructuredTestReportFile = File(aggregateReportDir, "test_results.json")
    aggregateStructuredTestReportFile.writeText(GSON.toJson(aggregateReport))
}

private fun findSubmissionEntryDir(studentName: String): File? {
    val entryDirs = File(System.getProperty("user.dir"), "content").listFiles()
    val entryDirNameRegex = Regex("^${studentName}_.*")
    return entryDirs.find { entryDir -> entryDir.name.matches(entryDirNameRegex) }
}

private fun writeEmail(
    assignmentName: String,
    submission: Submission,
    marking: Marking?,
    testRunData: TestRun,
    outputStream: PrintStream = System.out
) {
    outputStream.println("To: ${submission.email}")
    outputStream.println("Subject: Bewertung - $assignmentName")
    outputStream.println()

    outputStream.println("Übung: $assignmentName")

    if (marking.isPlag()) {
        outputStream.println()
        outputStream.println("Deine Abgabe wurde als Plagiat markiert")
    }

    outputStream.println()
    outputStream.println("Testergebnisse deiner Abgabe:")
    writeTestResults(testRunData, outputStream = outputStream)
    outputStream.println()
}

private fun readTestRun(submissionDir: File): TestRun {
    val testSuiteXMLFiles = File(submissionDir, "module_data/unit_tests")
        .listFiles()
        .filter { file -> file.name.startsWith("TEST-") && file.name.endsWith(".xml") }
    val testSuiteResults = testSuiteXMLFiles.map { file -> XML_PERSISTER.read(TestSuite::class.java, file) }
    return TestRun(null, null, testSuiteResults)
}

private fun writeAggregateReportEntry(
    submission: Submission,
    marking: Marking?,
    testRunData: TestRun,
    outputStream: PrintStream = System.out
) {
    outputStream.println(submission.name)

    if(marking.isPlag()) {
        outputStream.println()
        outputStream.println("Plagiat")
    }

    outputStream.println()
    writeTestResults(testRunData, writeStackTrace = false, outputStream = outputStream)
    outputStream.print("\n\n\n")
}

private fun writeTestResults(
    testRunData: TestRun,
    writeStackTrace: Boolean = false,
    outputStream: PrintStream = System.out
) {
    testRunData.testSuites.forEach { suite ->
        outputStream.println("${suite.name}:")
        suite.cases.forEach { case ->
            outputStream.println("  ${case.name}() ${if (case.hasFailed) "failed" else "succeeded"} in ${case.time}s")
            if (case.hasFailed) {
                outputStream.println("    Exception: ${case.failure!!.type}")
                outputStream.println("    Message: ${case.failure!!.message}")
                if (writeStackTrace) {
                    outputStream.println()
                    outputStream.println("    Stack trace:")
                    outputStream.println(case.failure!!.stackTrace.prependIndent("        "))
                }
            }
        }
    }
}
