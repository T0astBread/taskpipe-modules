package cc.t0ast.taskpipe.modules.mark.cli

import cc.t0ast.taskpipe.modules.mark.readMarkings
import cc.t0ast.taskpipe.modules.mark.saveMarkings
import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.mainBody

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = mainBody {
            ArgParser(args).parseInto(::Args).run {
                val args = this
                val markings = readMarkings()

                args.markingDiff.applyTo(args.editedEntries, markings)

                if (args.excluded != null)
                    applyExclusion(args.excluded!!, args.editedEntries, markings)

                saveMarkings(markings)
            }
        }
    }
}