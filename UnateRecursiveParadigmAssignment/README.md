### To use

```

import edu.illinois.vlsicad.urp.FileFormatChecker
import edu.illinois.vlsicad.urp.URPException

FileFormatChecker inputChecker = new FileFormatChecker(file: originalInput)
try {
  inputChecker.checkFileFormat()
} catch (URPException urp) {
 // Do stuff
}
```

