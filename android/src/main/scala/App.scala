import android.app.Activity
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.slf4j.Logger
import java.io.{FileInputStream, File}
import java.net.URLClassLoader
import java.util
import javassist._
import org.slf4j.{LoggerFactory, Logger}
import scala.util.matching.Regex

object Main extends App {

  val logger = LoggerFactory.getLogger("App")

  val conf = ConfigFactory.load();
  import scala.collection.JavaConversions._
  conf.entrySet().iterator().foreach(println)

  import java.io.File

  implicit class RichFile(file: File) {

    def children = new Iterable[File] {
      def iterator = if (file.isDirectory) file.listFiles.iterator else Iterator.empty
    }

    def tree: Iterable[File] = Seq(file) ++ children.flatMap(child => new RichFile(child).tree)
  }

  val dir = "/home/acsia/dev/android/tesco/app/target/classes/"
  val output = "/tmp/classes/"


  for (f <- new File(dir).tree; if f.getName.endsWith("class")) tryLoading(f)


  def tryLoading(f: File) {
    val cl = classPool.makeClass(new FileInputStream(f))
    val activity = classPool.get(classOf[Activity].getName);
    try {
      if (cl.subclassOf(activity)) {
        Console.println(f.getName)
        modify(cl)
      } else {
        //if (f.getName.contains("Activity")) Console.println("==> " + f.getName)
      }
    } catch {
      case e: NotFoundException => {
        Console.println(e)
      }
    }
  }

  def modify(cl: CtClass) {
    import RichCtClass._
    val original = cl.getDeclaredMethod("onCreate");
    original.log();
    cl.writeFile(output);
  }

  lazy val classPool: ClassPool = {
    val classPool = new ClassPool(ClassPool.getDefault());
    classPool.childFirstLookup = true;
    classPool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
    classPool.appendSystemPath();
    classPool.appendClassPath(dir)
    classPool
  }
}
