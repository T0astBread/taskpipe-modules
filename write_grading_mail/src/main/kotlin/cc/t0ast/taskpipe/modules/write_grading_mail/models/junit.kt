package cc.t0ast.taskpipe.modules.write_grading_mail.models

import org.simpleframework.xml.*

data class TestRun(
    var name: String? = null,
    var entryDir: String? = null,
    val testSuites: List<TestSuite>
)

@Root(name = "testsuite", strict = false)
data class TestSuite(
    @field:Attribute var name: String,
    @field:Attribute var tests: Int,
    @field:Attribute var skipped: Int,
    @field:Attribute var failures: Int,
    @field:Attribute var errors: Int,
    @field:Attribute(name = "timestamp") var timestampStr: String,  // The XML parser can't parse JUnit's time format
    @field:Attribute(name = "hostname") var hostName: String,
    @field:Attribute var time: Float,
    @field:ElementList(entry = "testcase", inline = true) var cases: MutableList<TestCase>
) {
    constructor() : this("", 0, 0, 0, 0, "", "", 0f, mutableListOf<TestCase>())
}

data class TestCase(
    @field:Attribute var name: String,
    @field:Attribute(name = "classname") var className: String,
    @field:Attribute var time: Float,
    @field:Element(name = "failure", required = false) var failure: Failure?
) {
    val hasFailed: Boolean
        get() = this.failure != null

    constructor() : this("", "", 0f, null)
}

data class Failure(
    @field:Attribute var message: String,
    @field:Attribute var type: String,
    @field:Text var stackTrace: String
) {
    constructor() : this("", "", "")
}
