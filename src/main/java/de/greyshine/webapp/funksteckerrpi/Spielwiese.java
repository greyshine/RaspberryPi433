package de.greyshine.webapp.funksteckerrpi;

import java.io.File;
import java.io.IOException;

public class Spielwiese {
	public static void main(String[] args) throws IOException {
		Utils.console(null, null, null, new File("/Users/dirkschumacher/progging/projects/FunksteckerSteuerung/src/test/local"), "./dummy-codesend.sh");
	}
}
