package pirene.util

import scala.quoted.*

given staging.Compiler = staging.Compiler.make(getClass.getClassLoader)
