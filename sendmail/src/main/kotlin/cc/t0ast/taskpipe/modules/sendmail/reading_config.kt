package cc.t0ast.taskpipe.modules.sendmail

import com.google.gson.Gson
import java.io.File
import java.lang.Exception


private val GSON = Gson()


fun readConfig(): Config {
    val configFile = findConfigFile()
    return GSON.fromJson(configFile.readText(), Config::class.java)
}


private const val CONFIG_FILE_LOCATION = "module_data/email/config.json"

private fun findConfigFile(): File {
    var cwd = File(System.getProperty("user.dir"))

    var suspectedConfigFile = File(cwd, CONFIG_FILE_LOCATION)
    println("Looking for config file in ${suspectedConfigFile.path}")
    if(suspectedConfigFile.exists())
        return suspectedConfigFile

    // root/content/entry -> root/content -> root
    suspectedConfigFile = File(cwd.parentFile.parentFile, CONFIG_FILE_LOCATION)
    println("Looking for config file in ${suspectedConfigFile.path}")
    if(!suspectedConfigFile.exists())
        throw Exception("No email config file found")
    return suspectedConfigFile
}
