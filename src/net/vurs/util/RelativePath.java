package net.vurs.util;

import java.io.File;

public class RelativePath {

	public static File root() {
		String comPath = Thread.currentThread()
				.getContextClassLoader()
				.getResource("com".replace('.', '/'))
				.getFile();

		File rootFile = new File(comPath).getParentFile()
				.getParentFile()
				.getParentFile()
				.getParentFile();
		
		return rootFile;
	}
	
}
