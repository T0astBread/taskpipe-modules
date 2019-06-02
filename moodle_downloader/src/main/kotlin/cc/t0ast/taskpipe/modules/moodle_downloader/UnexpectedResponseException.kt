package cc.t0ast.taskpipe.modules.moodle_downloader

import com.github.kittinunf.fuel.core.Response

class UnexpectedResponseException(message: String?, val response: Response) : Exception(message) {
    override fun toString(): String {
        return "${super.toString()}\n$response"
//        return super.toString()
    }
}