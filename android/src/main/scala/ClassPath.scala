import javassist.{ClassPool, LoaderClassPath}

class ClassPath {

  lazy val classPool: ClassPool = {
    val classPool = new ClassPool(ClassPool.getDefault());
    classPool.childFirstLookup = true;
    classPool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
    classPool.appendSystemPath();
    classPool
  }

  def findClass(name: String) {
    classPool.get(name);
  }
}
