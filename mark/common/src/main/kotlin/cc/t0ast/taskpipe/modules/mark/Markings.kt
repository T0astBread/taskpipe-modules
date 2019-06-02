package cc.t0ast.taskpipe.modules.mark

public data class Marking(
    val name: String,
    var isInExcludedDir: Boolean,
    val markings: MutableList<String> = mutableListOf()
)
