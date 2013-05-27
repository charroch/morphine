package javassist

import java.io.File
import javassist.expr.ExprEditor

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
      method.instrument(new CodeConverter)
      method.addLocalVariable("start", CtClass.longType)
      method.insertBefore(
        """
          |start = System.currentTimeMillis();
        """.stripMargin
      )
      method.insertAfter(
        """
          |long end = System.currentTimeMillis() - start;
          |android.util.Log.i("morphine", this.getClass().getSimpleName() + ":s:" + start + ":e:" + end);
        """.stripMargin
      )
    }

  }

}
