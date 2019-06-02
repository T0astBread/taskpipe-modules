package cc.t0ast.taskpipe.modules.moodle_downloader

import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import java.io.FileOutputStream
import java.lang.Exception

const val ASSIGNMENT_DOWNLOAD_FILENAME = "moodle_download.zip"

fun downloadAssignmentSolutions(assignmentId: Int) {
    val request = "/mod/assign/view.php"
        .httpGet(listOf(
            "id" to assignmentId,
            "action" to "downloadall"
        ))
    val (_, response, result) = request.response()
    if(result is Result.Failure) throw Exception("Couldn't download solution: Request failed", result.error)

//    if(!response.headers.containsKey("Content-Description")) {
//        throw UnexpectedResponseException("Couldn't download solution: Response was not a download", response)
//    }
    response.downloadTo(ASSIGNMENT_DOWNLOAD_FILENAME)
}

private fun Response.downloadTo(filename: String) {
    FileOutputStream(filename).use {
        it.write(this.data)
    }
}