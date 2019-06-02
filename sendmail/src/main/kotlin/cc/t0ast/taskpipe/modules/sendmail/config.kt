package cc.t0ast.taskpipe.modules.sendmail

data class Config(
        val sender: SenderConfig,
        val smtp: SMTPConfig
)

data class SenderConfig(
        val address: String,
        val displayName: String
)

data class SMTPConfig(
        val host: String,
        val port: Int,
        val credentials: Credentials?,
        val useTLS: Boolean
)

data class Credentials(
        val userName: String,
        val password: String
)
