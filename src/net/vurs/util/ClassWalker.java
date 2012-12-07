package net.vurs.util;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

public class ClassWalker implements Iterator<Class<?>> {
	public static final String ROOT_PACKAGE = "net.vurs";
	
	private LinkedBlockingQueue<Class<?>> classes = null;
	
	public ClassWalker(ClassWalkerFilter ... filters) {
		this(ROOT_PACKAGE, filters);
	}
	
	private ClassWalker(String path, ClassWalkerFilter ... filters) {
		path = ROOT_PACKAGE;
		
		File file = new File(Thread.currentThread()
									.getContextClassLoader()
									.getResource(path.replace('.', '/'))
									.getFile());
		
		this.classes = new LinkedBlockingQueue<Class<?>>();
		
		LinkedList<File> files = new LinkedList<File>();
		files.addAll(Arrays.asList(file.listFiles()));
		
		while(!files.isEmpty()) {
			File cur = files.poll();
			
			if (cur.isDirectory()) {
				files.addAll(Arrays.asList(cur.listFiles()));
			} else {
				String name = cur.getAbsolutePath();
				if (!name.endsWith(".class")) continue;
				
				name = name.substring(0, name.length() - 6).replace(File.separatorChar, '.');
				name = name.substring(name.indexOf(path));
				
				try {
					Class<?> cls = Class.forName(name);
					
					boolean visit = true;
					for (ClassWalkerFilter filter: filters) {
						if (!filter.visit(cls)) {
							visit = false;
							break;
						}
					}
					
					if (visit) {
						this.classes.add(cls);
					}
				} catch (ClassNotFoundException e) {
					ErrorControl.fatal(cur.getAbsolutePath());
					ErrorControl.logException(e);
				}
			}
		}
	}

	public boolean hasNext() {
		return !this.classes.isEmpty();
	}

	public Class<?> next() {
		return this.classes.poll();
	}

	public void remove() {
		ErrorControl.fatal("Unsupported Operation");
	}
	
}
