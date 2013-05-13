import java.io.File

class ClassDir(root: File) {
  def tree(): Stream[File] = {
    def innerTree(root: File, skipHidden: Boolean = false): Stream[File] = {
      if (!root.exists || (skipHidden && root.isHidden)) Stream.empty
      else root #:: (
        root.listFiles match {
          case null => Stream.empty
          case files => files.toStream.flatMap(innerTree(_, skipHidden))
        })
    }
    innerTree(root)
  }
}

object ClassDir {
  def apply(file: String) = new ClassDir(new File(file))
}
