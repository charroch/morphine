import java.io.File

class ClassDir(root: File) {

  lazy val classFilter = """.*\.class$""".r

  def tree[T]()(implicit converter: File => T): Stream[T] = {
    def innerTree(root: File, skipHidden: Boolean = false): Stream[File] = {
      if (!root.exists || (skipHidden && root.isHidden)) Stream.empty
      else root #:: (
        root.listFiles() match {
          case null => Stream.empty
          case files => files.filter(_.isDirectory).toStream.flatMap(innerTree(_, skipHidden))
        })
    }
    innerTree(root).map(converter)
  }
}

object ClassDir {
  def apply(file: String) = new ClassDir(new File(file))
}
