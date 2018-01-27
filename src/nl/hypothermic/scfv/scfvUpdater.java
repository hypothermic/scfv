package nl.hypothermic.scfv;

public class scfvUpdater {
	
	/** Security Camera Feed Viewer Library
	 * @class scfvUpdater.java
	 * @author hypothermic
	 * @version v1.0
	 * https://github.com/hypothermic
	 * https://hypothermic.nl
     * Tested for Foscam FI-9900P - x.getEngine().load("http://INSERT_ADDRESS:INSERT_PORT/cgi-bin/CGIProxy.fcgi?cmd=snapPicture2&usr=INSERT_USERNAME&pwd=INSERT_PASSWORD");
	 */
	
	/** How to use this library:
	 * - Import this library into your project:
	 * >> import nl.hypothermic.scfv.*;
	 * - Call the main method:
	 * >> scfvMain(int width, int height, String usr, String passwd, InetAddress addr, int port, int interval, true);
	 */
	
	public scfvUpdater(final int interval) {
		scfvMain cl = new scfvMain(0, 0, null, null, null, 0, 0, false);
        new Thread() {
            public void run() {
            	try {
            		// sleep voor splash screen
					Thread.sleep(1500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
                while (true) {
                	try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
						e.printStackTrace();
						System.exit(1);
					}
                	cl.updateWebView();
                }
            }
        }.start();
	}
}
