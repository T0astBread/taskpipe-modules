package cc.t0ast.taskpipe.modules.mark

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader

private val GSON = Gson()
private val markingsFile = File(System.getProperty("user.dir"), "module_data/markings.json")

public fun readMarkings(): MutableMap<String, Marking> {
    if (!markingsFile.exists()) {
        return mutableMapOf()
    }

    val typeToken = object : TypeToken<MutableMap<String, Marking>>() {}
    return GSON.fromJson(FileReader(markingsFile), typeToken.type)
}

public fun saveMarkings(markings: Map<String, Marking>) {
    markingsFile.writeText(GSON.toJson(markings))
}
