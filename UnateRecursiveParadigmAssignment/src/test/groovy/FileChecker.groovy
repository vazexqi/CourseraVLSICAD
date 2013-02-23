import edu.illinois.vlsicad.urp.FileFormatChecker
import edu.illinois.vlsicad.urp.URPException

class FileChecker extends GroovyTestCase {
    void testValidFile() {
        FileFormatChecker fc = new FileFormatChecker()
        fc.setFile(new File("ValidExample.cubes"))
        fc.checkFileFormat()
    }

    void testInvalidFile() {
        def msg = shouldFail(URPException) {
            FileFormatChecker fc = new FileFormatChecker()
            fc.setFile(new File("InvalidExample.cubes"))
            fc.checkFileFormat()
        }
        assert 'First value of line #4 is not a number. Saw % but expected a number.' == msg
    }
}
