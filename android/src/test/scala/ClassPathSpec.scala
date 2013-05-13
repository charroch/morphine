import java.io.File
import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

class ClassDirSpec extends FlatSpec with ShouldMatchers {

  "A directory with classes" should "list the correct number of files" in {
    val file = new File("./src/test/resources/output")
    val dir = new ClassDir(file);
    dir.tree().size should equal (5)
  }
}
