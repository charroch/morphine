import java.io.{FileInputStream, File}
import javassist.{CtClass, ClassPool, LoaderClassPath}

class ClassPath {

  lazy val classPool: ClassPool = {
    val classPool = new ClassPool(ClassPool.getDefault());
    classPool.childFirstLookup = true;
    classPool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
    classPool.appendSystemPath();
    classPool
  }

  implicit def a: File => CtClass = file => {
      classPool.makeClass(new FileInputStream(file))
  }

  def findClass(name: String) {
    classPool.get(name);
  }
}
