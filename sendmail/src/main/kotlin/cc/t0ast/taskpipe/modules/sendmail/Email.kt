package cc.t0ast.taskpipe.modules.sendmail

data class Email(
        val recipient: String,
        val subject: String,
        val body: String
)
