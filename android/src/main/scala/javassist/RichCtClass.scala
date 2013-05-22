package javassist

import java.io.File

object RichCtClass {


  implicit class RichCtClass(ctClass: CtClass) {

    def is() {}

    def `extends`() {}

    def implements() {}

    //def methods() : List[Method] = {}
  }

  type Method = CtMethod

  implicit class RichMethod(method: CtMethod) {

    def log() {
      method.insertBefore (
        """
          |android.util.Log.i("Aspect", ">> entering" + this );
        """.stripMargin
      )
    }

  }

}
