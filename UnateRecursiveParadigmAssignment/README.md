### To use

```

import edu.illinois.vlsicad.urp.FileFormatChecker
import edu.illinois.vlsicad.urp.URPException

FileFormatChecker fc = new FileFormatChecker()
fc.setFile(new File("ValidExample.cubes"))
try {
  fc.checkFileFormat()
} catch (URPException urp) {
 // Do stuff
}
```

