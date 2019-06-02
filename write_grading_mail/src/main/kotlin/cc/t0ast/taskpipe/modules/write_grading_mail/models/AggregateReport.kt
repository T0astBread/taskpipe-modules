package cc.t0ast.taskpipe.modules.write_grading_mail.models

class AggregateReport(
    val assignment: Assignment,
    val testRuns: List<TestRun>
)

data class Assignment(
    val id: Int,
    val name: String
)
