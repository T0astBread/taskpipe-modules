package cc.t0ast.taskpipe.modules.moodle_downloader

import java.lang.Exception

class InvalidCredentialsException(cause: Throwable?) : Exception("The credentials provided were invalid", cause)