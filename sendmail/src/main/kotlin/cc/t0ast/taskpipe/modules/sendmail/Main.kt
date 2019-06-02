package cc.t0ast.taskpipe.modules.sendmail

import java.io.File

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val config = readConfig()

            val mailFolder = File(System.getProperty("user.dir"), "module_data/email")
            if(mailFolder.exists()) {
                findMails(mailFolder.toPath()).forEach { email ->
                    println("Sending mail to ${email.recipient}; Subject: ${email.subject}")
                    send(email, config)
                }
            }
        }
    }
}