package nl.hypothermic.scfv_standalone;

import java.io.DataInputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class scfvUpdater {
	
	/** Security Camera Feed Viewer Standalone
	 * @class scfvUpdater.java
	 * @author hypothermic
	 * @version v1.0
	 * https://github.com/hypothermic
	 * https://hypothermic.nl
     * Tested for Foscam FI-9900P - x.getEngine().load("http://INSERT_ADDRESS:INSERT_PORT/cgi-bin/CGIProxy.fcgi?cmd=snapPicture2&usr=INSERT_USERNAME&pwd=INSERT_PASSWORD");
	 */
	
	public scfvUpdater(final int interval) {
		final scfvMain cl = new scfvMain(0, 0, null, null, null, 0, 0, false, false);
        new Thread() {
            public void run() {
            	try {
            		// sleep voor splash screen
					Thread.sleep(1500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
            	String rs = cl.post("getDevInfo");
            	System.out.println(rs);
            	cl.rmSplashScreen();
            	if (rs.contains("0")) {
            		while (true) {
                		try {
							Thread.sleep(interval);
						} catch (InterruptedException e) {
							e.printStackTrace();
							System.exit(1);
						}
                		cl.updateWebView();
                	}
            	} else if (rs.contains("-2")) {
            		System.out.println("[ERROR] Account does not exist."); System.exit(1);
            	} else if (rs.contains("-3")) {
            		System.out.println("[ERROR] Access denied."); System.exit(1);
            	} else {
            		System.out.println(cl.post("getDevInfo"));
            	}
            }
        }.start();
	}
}
