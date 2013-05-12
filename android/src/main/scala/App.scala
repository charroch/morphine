import com.typesafe.scalalogging.slf4j.Logger
import java.io.File
import java.net.URLClassLoader
import java.util
import javassist.{CtNewMethod, CtClass, ClassPool, LoaderClassPath}
import org.slf4j.{LoggerFactory, Logger}
import scala.util.matching.Regex

object Main extends App {
  val logger = LoggerFactory.getLogger("Main")

  lazy val classPool = new ClassPool(ClassPool.getDefault());

  val dir = "/home/acsia/dev/android/tesco/app/target/classes/"
  val output = "/tmp/classes/"
  Console.println("Hello world");

  debugClassLoader(classPool)

  def debugClassLoader(classPool: ClassPool) {
    if (!logger.isDebugEnabled()) {
      return;
    }
   // logger.debug(" - classPool: " + classPool.toString());
    init(dir)
    //    def iter(expr: => ClassLoader, acc: List[ClassLoader]): List[ClassLoader] =
    //      expr match {
    //        case null => acc
    //        case w => iter(expr.getParent, w :: acc)
    //      }
    //
    //    iter(classLoader, Nil).foreach(cl => logger.debug(cl.getClass().getName()))
    recursiveListFiles(new File(dir), """.*\.class$""".r).map(
      f =>
        f.getName.substring(0, f.getName.lastIndexOf('.')).toString
    ).filter(removeDollar).map(
      s => {
        classPool.importPackage(s)
        val a = classPool.get(s)
        initializeClass(a)
        a
      }
    ).filter(all).foreach(
      f =>
        javassist(f)
    )
  }

  def removeDollar(s: String): Boolean = {
    s.contains("Activity")
  }

  def initializeClass(candidateClass: CtClass) {
    candidateClass.subtypeOf(ClassPool.getDefault().get(classOf[Object].getName()));
  }

  def all(a: CtClass): Boolean = {
    a.getName.contains("Tesco")
  }

  def javassist(kl: CtClass) {
    toStringIt(kl);
    kl.writeFile(dir);
   // logger.debug("Class " + kl + " instrumented by {}");
  }

  def toStringIt(classToTransform: CtClass) {
    val toStringMethod = classToTransform.getDeclaredMethod("toString");
    classToTransform.removeMethod(toStringMethod);

    val hackedToStringMethod = CtNewMethod.make(
      "public String toString() { return \"toString() hacked by Javassist\"; }",
      classToTransform);
    classToTransform.addMethod(hackedToStringMethod);
  }

  def init(dir: String) {
    classPool.childFirstLookup = true;
    classPool.appendClassPath(dir);
    classPool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
    classPool.appendSystemPath();
  }

  def recursiveListFiles(f: File, r: Regex): Array[File] = {
    val these = f.listFiles
    val good = these.filter(f => r.findFirstIn(f.getName).isDefined)
    good ++ these.filter(_.isDirectory).flatMap(recursiveListFiles(_, r))
  }

}
