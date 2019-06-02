package cc.t0ast.taskpipe.modules.write_grading_mail

import cc.t0ast.taskpipe.modules.mark.Marking

fun Marking?.isPlag() = this?.markings?.contains("plagiarism") == true
