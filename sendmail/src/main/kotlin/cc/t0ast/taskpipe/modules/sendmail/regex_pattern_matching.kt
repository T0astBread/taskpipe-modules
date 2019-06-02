package cc.t0ast.taskpipe.modules.sendmail

fun match(inputStr: String, vararg cases: Pair<String, (MatchResult) -> Unit>, elseCase: (() -> Unit)? = null) {
    matchP(inputStr, mapOf(*cases), elseCase ?: {})
}

fun <R> match(inputStr: String, cases: Map<String, (MatchResult) -> R>, elseCase: () -> R): R =
        matchP(inputStr, cases, elseCase)!!

private fun <R> matchP(inputStr: String, cases: Map<String, (MatchResult) -> R>, elseCase: (() -> R)? = null): R? {
    cases.forEach { (pattern, action) ->
        val match = Regex(pattern).matchEntire(inputStr)
        if(match != null)
            return action(match)
    }
    return elseCase?.invoke()
}
