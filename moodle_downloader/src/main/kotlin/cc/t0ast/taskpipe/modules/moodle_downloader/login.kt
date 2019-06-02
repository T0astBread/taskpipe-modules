package cc.t0ast.taskpipe.modules.moodle_downloader

import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.fuel.core.Response
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import java.net.URL
import java.nio.charset.Charset

const val LOGIN_PAGE = "/login/index.php"

fun logIn(username: String, password: String) {
    try {
        FuelManager.instance.baseHeaders = mapOf(
            "Cookie" to obtainLoginCookies(username, password)
        )
    } catch (exception: Exception) {
        throw Exception("Couldn't log in", exception)
    }
}

// THE MOODLE LOGIN FLOW:
// request 1 -> GET /login/index.php; obtain session cookies + login token
// request 2 -> POST /login/index.php with credentials + login token and obtained session cookies; obtain testsession redirect url, new session cookies
//              The testsession redirect is necessary but it contains the user ID and we can't know that unless we take it from the redirect
// request 3 -> GET /login/index.php?testsession=<user_id> with session cookies from request 2
// DONE; the session cookies from request 2 are the ones to use in later requests
private fun obtainLoginCookies(username: String, password: String): String {
    val (request1, response1, result1) = LOGIN_PAGE.httpGet().response()
    if (result1 is Result.Failure) throw Exception(
        "Couldn't get login token and session cookie: GET request to login page failed",
        result1.error
    )

    val loginToken =
        response1.loginToken ?: throw UnexpectedResponseException("No login token present in response", response1)
    var sessionCookies = response1.setCookiesOrThrow

    val (request2, response2, result2) = LOGIN_PAGE
        .httpPost()
        .allowRedirects(false)
        .body(
            mapOf(
                "username" to username,
                "password" to password,
                "logintoken" to loginToken
            ).urlEncoded
        )
        .header("Cookie" to sessionCookies)
        .response()
    if (result2 is Result.Failure && !result2.error.toString().contains("HTTP Exception 303")) {
        throw Exception("Couldn't get redirect link to testsession request", result2.error)
    }

    if (response2.statusCode != 303) throw UnexpectedResponseException(
        "POST request to login page did not redirect",
        response1
    )
    val testSessionURL = response2.redirectUrl!!
    sessionCookies = response2.setCookiesOrThrow

    val (request3, response3, result3) = testSessionURL
        .httpGet()
        .allowRedirects(false)
        .header("Cookie" to sessionCookies)
        .response()
    if (result3 is Result.Failure && !result3.error.toString().contains("HTTP Exception 303")) {
        throw Exception("testsession request failed", result3.error)
    }
    if (response3.statusCode != 303) throw UnexpectedResponseException(
        "testsession request did not redirect at all",
        response3
    )
    if (response3.redirectUrl?.replace(Regex("/$"), "") != FuelManager.instance.basePath) { // Check if testsession request redirects to landing page
        throw UnexpectedResponseException("testsession request did not redirect to dashboard", response3)
    }

    return sessionCookies
}

private val Response.redirectUrl: String?
    get() = this.headers["Location"]?.get(0)

private val Map<String, Any>.urlEncoded: String
    get() = this.entries
        .map { "${it.key}=${it.value}" }
        .joinToString("&")

private val Response.setCookiesOrThrow: String
    get() = this.setCookies ?: throw UnexpectedResponseException("No Set-Cookie header present in response", this)

/**
 * Extracts cookies from all Set-Cookie headers
 */
private val Response.setCookies: String?
    get() = this.headers["Set-Cookie"]
        ?.map { it.cookieString }
        ?.joinToString("; ")

private val Response.loginToken: String?
    get() {
        val loginTokenInputRegex = Regex("input\\s+type=\"hidden\"\\s+name=\"logintoken\"\\s+value=\"(\\w+)\"")
        val match = loginTokenInputRegex.find(text)
        return match?.groupValues?.get(1)
    }

private val Response.text: String
    get() = this.data.toString(Charset.forName("UTF-8"))

/**
 * Extracts a cookie string from a Set-Cookie header
 */
private val String.cookieString
    get() = this.split(";")[0]