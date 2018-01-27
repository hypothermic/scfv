package nl.hypothermic.scfv;

import java.awt.EventQueue;
import java.net.InetAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.BorderLayout;

public class scfvMain {
	
	/** Security Camera Feed Viewer Library
	 * @class scfvMain.java
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
	
	// default values. constructor changes them if passed correctly.
	private int width = 810;
	private int height = 480;
	private static String usr;
	private static String passwd;
	private static InetAddress addr;
	private int port = 88;
	private int interval = 500;

	public scfvMain(int width, int height, String usr, String passwd, InetAddress addr, int port, int interval, boolean isConstructed) {
		Logger x = Logger.getLogger("scfvMain"); x.log(Level.FINER, "Created scfvMain instance");
		if (width <= 10) { if ((this.width) > 10) { x.log(Level.FINER, "Info: using initialized value for width."); } else { x.log(Level.SEVERE, "Critical error: both initialized width and passed width are not compliant to standards."); return; }}
		if (height <= 10) { if ((this.height) > 10) { x.log(Level.FINER, "Info: using initialized value for height."); } else { x.log(Level.SEVERE, "Critical error: both initialized height and passed height are not compliant to standards."); return; }}
		if (port <= 0 && port >= 25566) { if (this.port > 0 && this.port < 25566) { x.log(Level.FINER, "Info: using initialized value for port."); } else { x.log(Level.SEVERE, "Critical error: both initialized port and passed port are not compliant to standards."); return; }}
		if (usr != null) { scfvMain.usr = usr; } else { x.log(Level.FINER, "Info: using initialized value for usr."); }
		if (passwd != null) { scfvMain.passwd = passwd; } else { x.log(Level.FINER, "Info: using initialized value for passwd."); }
		if (addr != null) { scfvMain.addr = addr; } else { x.log(Level.FINER, "Info: using initialized value for addr."); }
		if (interval <= 40 && interval >= Integer.MAX_VALUE) { if (this.interval > 40 && this.interval < Integer.MAX_VALUE) { x.log(Level.FINER, "Info: using initialized value for port."); } else { x.log(Level.SEVERE, "Critical error: both initialized interval and passed interval are not compliant to standards."); return; }}
		if (isConstructed == true) { main(); }
	}
	
    private static void initAndShowGUI() {
    	Logger.getLogger("scfvMain").log(Level.INFO, "Created GUI instance");
        scfvMain cl = new scfvMain(0, 0, usr, passwd, addr, 0, 0, false);
    	System.out.println("debug addr: " + scfvMain.addr);
        JFrame frame = new JFrame("scfv - " + scfvMain.addr + cl.port);
        final JFXPanel fxPanel = new JFXPanel();
        frame.getContentPane().add(fxPanel);
        frame.setSize(cl.width, cl.height);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Platform.runLater(new Runnable() {
        	@Override
        	public void run() {
                initFX(fxPanel);
            }
        });
    }

    private static void initFX(JFXPanel fxPanel) {
        Scene scene = createScene();
        fxPanel.setScene(scene);
    }

    private static Scene createScene() {
        VBox picstream = new VBox();
        setPicStream(picstream);
        picstream.setAlignment(Pos.CENTER);
        VBox splashex = new VBox();
        splashex.setAlignment(Pos.CENTER);
        VBox splashef = new VBox();
        splashef.setAlignment(Pos.CENTER);
        HBox scrroot = new HBox();
        setScrRoot(scrroot);
        scrroot.getChildren().add(splashex);
        scrroot.getChildren().add(splashef);
        Scene scene = new Scene(scrroot);
        
        WebView x = new WebView();
        
        setWebView(x);

        Text xt = new Text();
        Text xti = new Text();
        xt.setText("Powered by Hypothermic's SCFV");
        xti.setText("https://github.com/hypothermic https://hypothermic.nl");
        xt.setFont(new Font(25));
        xti.setFont(new Font(11));
        picstream.getChildren().add(x);
        xt.setTextAlignment(TextAlignment.CENTER);
        splashef.getChildren().add(xt);
        xti.setTextAlignment(TextAlignment.CENTER);
        splashex.getChildren().add(xti);
        x.setVisible(false);

        return (scene);
    }
    
    private static void setWebView(WebView x) {
    	e = x;
    }
    
    private static void setScrRoot(HBox x) {
    	scrroot = x;
    }
    
    private static void setPicStream(VBox x) {
    	picstream = x;
    }

    /*comment: overbodig. public WebView getWebView() {
    	return e;
    }*/
    
    private static WebView e;
    private static HBox scrroot;
    private static VBox picstream;

    public void updateWebView() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                scrroot.getChildren().clear();
                scrroot.getChildren().add(picstream);
            	e.setVisible(true);
                e.getEngine().load("http://" + addr + ":" + port + "/cgi-bin/CGIProxy.fcgi?cmd=snapPicture2&usr=" + usr + "&pwd=" + passwd);
            }
        });
    }
    
    private static boolean initialized = false;

    private static void main() {
    	if (initialized == false) {
    		initialized = true;
    		SwingUtilities.invokeLater(new Runnable() {
    			@Override
    			public void run() {
    				initAndShowGUI();
    			}
    	});}
    	scfvMain cl = new scfvMain(0, 0, usr, passwd, addr, 0, 0, false);
    	int intervalx = cl.interval;
		scfvUpdater x = new scfvUpdater(intervalx);
    }
}
