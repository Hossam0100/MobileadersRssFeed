package com.mobileaders.app;

import java.util.Timer;
 
public class MainClass {
	public static void main(String[] args) {
		App ap = new App();
		Timer t = new Timer();
		t.scheduleAtFixedRate(ap, 0, 30 * 60 * 1000);
	}

}
