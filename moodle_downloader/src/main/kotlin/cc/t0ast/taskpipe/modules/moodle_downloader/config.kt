package cc.t0ast.taskpipe.modules.moodle_downloader

import com.github.kittinunf.fuel.core.FuelManager

var moodleBaseURL: String? = null
    set(value) {
        field = value
        FuelManager.instance.basePath = value
    }