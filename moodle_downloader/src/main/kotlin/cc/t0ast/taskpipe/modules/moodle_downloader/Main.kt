package cc.t0ast.taskpipe.modules.moodle_downloader

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            if (!checkArgs(args)) System.exit(1)

            moodleBaseURL = args[0]
            logIn(args[1], args[2])
            println("Logged in")
            downloadAssignmentSolutions(args[3].toInt())
            println("Downloaded assignment solutions")
        }

        private fun checkArgs(args: Array<String>): Boolean {
            val areValid = args.size == 4
                    && !args[0].endsWith("/")
                    && Regex("\\d+").matches(args[3])
            if (!areValid) {
                println("Invalid command line arguments")
                println("The arguments need to have the following structure: MOODLE_BASE_URL USERNAME PASSWORD ASSIGNMENT_ID")
                println("Mind that the MOODLE_BASE_URL has to look like this: http[s]://my.school.com/path/to/moodle (no trailing slash)")
            }
            return areValid
        }
    }
}