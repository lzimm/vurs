package net.vurs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.start.Main;

public class Vurs {
	
	public static void main(String[] args) {
		List<String> opts = new ArrayList<String>();
		
		Main m = new Main();
		
		try {
			m.start(opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
