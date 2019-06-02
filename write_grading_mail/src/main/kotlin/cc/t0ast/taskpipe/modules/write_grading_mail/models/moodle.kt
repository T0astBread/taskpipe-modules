package cc.t0ast.taskpipe.modules.write_grading_mail.models

import java.time.LocalDateTime

data class MoodleData(
//    val timeOfRetrieval: LocalDateTime,
    val assignmentName: String,
    val assignmentID: Int,
    val submissions: Array<Submission>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is MoodleData) return false

        if (!submissions.contentEquals(other.submissions)) return false

        return true
    }

    override fun hashCode(): Int {
        return submissions.contentHashCode()
    }
}

data class Submission(
    val name: String,
    val email: String,
    val submissionURL: String,
    val fileName: String
)
