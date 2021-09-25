package com.marlon.portalusuario.util;


public class TkUtil {


	private static boolean isEmulatorChecked = false;
	private static boolean isEmulatorCache = false;
	
	public static boolean isEmulator() {
		
		if (!isEmulatorChecked) {

			isEmulatorCache = false;
			if (android.os.Build.DEVICE.equals("generic")) {
			     if (android.os.Build.BRAND.equals("generic")) {
			          isEmulatorCache = true;
			     }
			}
			isEmulatorChecked = true;
		}
		
		return isEmulatorCache;
	}

	

}
