package cc.t0ast.taskpipe.modules.sendmail

import org.apache.commons.mail.SimpleEmail

fun send(email: Email, config: Config) {
    val commonsEmail = SimpleEmail()

    commonsEmail.hostName = config.smtp.host
    commonsEmail.setSmtpPort(config.smtp.port)
    commonsEmail.isSSLOnConnect = config.smtp.useTLS

    val credentials = config.smtp.credentials
    if(credentials != null) {
        commonsEmail.setAuthentication(credentials.userName, credentials.password)
    }

    commonsEmail.setFrom(config.sender.address, config.sender.displayName)
    commonsEmail.addTo(email.recipient)
    commonsEmail.subject = email.subject
    commonsEmail.setMsg(email.body)

    commonsEmail.send()
}
