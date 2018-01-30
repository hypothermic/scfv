package nl.hypothermic.scfv;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
	
	/** To-do list:
	 * - Media controls, ln 135
	 * - rtsp?
	 */
	
	// default values. constructor changes them if passed correctly.
	private int width = 810;
	private int height = 480;
	private static String usr;
	private static String passwd;
	private static InetAddress addr;
	private int port = 88;
	private int interval = 500;

	public scfvMain(int width, int height, String usr, String passwd, InetAddress addr, int port, int interval, boolean isConstructed, boolean newThread) {
		Logger x = Logger.getLogger("scfvMain"); x.log(Level.INFO, "Created scfvMain instance");
		if (width <= 10) { if ((this.width) > 10) { x.log(Level.FINER, "Info: using initialized value for width."); } else { x.log(Level.SEVERE, "Critical error: both initialized width and passed width are not compliant to standards."); return; }}
		if (height <= 10) { if ((this.height) > 10) { x.log(Level.FINER, "Info: using initialized value for height."); } else { x.log(Level.SEVERE, "Critical error: both initialized height and passed height are not compliant to standards."); return; }}
		if (port <= 0 && port >= 25566) { if (this.port > 0 && this.port < 25566) { x.log(Level.FINER, "Info: using initialized value for port."); } else { x.log(Level.SEVERE, "Critical error: both initialized port and passed port are not compliant to standards."); return; }}
		if (usr != null) { scfvMain.usr = usr; } else { x.log(Level.FINER, "Info: using initialized value for usr."); }
		if (passwd != null) { scfvMain.passwd = passwd; } else { x.log(Level.FINER, "Info: using initialized value for passwd."); }
		if (addr != null) { scfvMain.addr = addr; } else { x.log(Level.FINER, "Info: using initialized value for addr."); }
		if (interval <= 40 && interval >= Integer.MAX_VALUE) { if (this.interval > 40 && this.interval < Integer.MAX_VALUE) { x.log(Level.FINER, "Info: using initialized value for port."); } else { x.log(Level.SEVERE, "Critical error: both initialized interval and passed interval are not compliant to standards."); return; }}
		if (!newThread) {
			if (isConstructed == true) { main(); Logger.getLogger("scfvMain").log(Level.INFO, "Not created new thread");}
		} else {
			if (isConstructed == true) { new Thread() { public void run() { new scfvMain(width, height, usr, passwd, addr, port, interval, true, false); }}.start();; Logger.getLogger("scfvMain").log(Level.INFO, "Created new thread");}
		}
	}
	
    private static void initAndShowGUI() {
    	Logger.getLogger("scfvMain").log(Level.INFO, "Created GUI instance");
        scfvMain cl = new scfvMain(0, 0, usr, passwd, addr, 0, 0, false, false);
        final JFrame frame = new JFrame("scfv - " + scfvMain.addr + cl.port);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        final JFXPanel fxPanel = new JFXPanel();
        frame.getContentPane().add(fxPanel);
        frame.setSize(cl.width, cl.height);
        frame.setVisible(true);

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
    	VBox ctrls = new VBox();
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
        
        Button mirror = new Button("Mirror");
        /* TODO scrroot.getChildren().add(ctrls);
         * ctrls.setAlignment(Pos.BOTTOM_CENTER);
         * ctrls.getChildren().add(mirror);*/
        mirror.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
            	mirror.setVisible(false);
                System.out.println("Mirroring screen...");
                post();
                mirror.setVisible(true);
            }
            
            private void post() {
            	try {
            	scfvMain cl = new scfvMain(0, 0, usr, passwd, addr, 0, 0, false, false);
                StringBuffer sb = new StringBuffer();
                sb.append( URLEncoder.encode("cmd") + "=" );
                sb.append( URLEncoder.encode("getProductModel"));
                sb.append( "&" + URLEncoder.encode("usr") + "=" );
                sb.append( URLEncoder.encode(usr));
                sb.append( "&" + URLEncoder.encode("pwd") + "=" );
                sb.append( URLEncoder.encode(passwd));
                String formData = sb.toString();
                System.out.println("[INFO] Connecting to: http://" + addr.toString() + ":" + cl.port + "/cgi-bin/CGIProxy.fcgi");
                URL url = new URL("http:/" + addr.toString() + ":" + cl.port + "/cgi-bin/CGIProxy.fcgi");
                HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
                urlcon.setRequestMethod("POST");
                urlcon.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
                urlcon.setDoOutput(true);
                urlcon.setDoInput(true);
                System.out.println("[INFO] Sending data: " + formData);
                PrintWriter pout = new PrintWriter(new OutputStreamWriter(urlcon.getOutputStream(),"8859_1"), true);
                pout.print(formData);
                pout.flush();
                DataInputStream input = new DataInputStream(urlcon.getInputStream());
                StringBuffer out = new StringBuffer(); String xs;
                while (null != ((xs = input.readLine()))) {
                	out.append(xs);
                }
                String res = cl.parseCgiResult(out.toString());
                if (res == null | res == "" | res == " ") { System.out.println("[ERROR] Error: invalid CGI response"); 
                } else if (res.contains("0")) {
                	// success
                } else if (res.contains("-1")) {
                	System.out.println("[ERROR] Received CGI code -1: request string format error");
                } else if (res.contains("-2")) {
                	System.out.println("[ERROR] Received CGI code -2: username or password error");
                } else if (res.contains("-3")) {
                	System.out.println("[ERROR] Received CGI code -3: access denied");
                } else if (res.contains("-4")) {
                	System.out.println("[ERROR] Received CGI code -4: cgi execute fail");
                } else if (res.contains("-5")) {
                	System.out.println("[ERROR] Received CGI code -5: timeout");
                } else if (res.contains("-6")) {
                	System.out.println("[ERROR] Received CGI code -6: <reserved code>");
                } else if (res.contains("-7")) {
                	System.out.println("[ERROR] Received CGI code -7: unknown error");
                } else if (res.contains("-8")) {
                	System.out.println("[ERROR] Received CGI code -8: <reserved code>");
                }
                input.close();
            	} catch (Exception x) {
            		x.printStackTrace();
            	}
            }
        });
        
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
            	//rtsp not supported e.getEngine().load("rtsp://" + addr + ":" + port + "/videoMain");
                e.getEngine().load("http://" + addr + ":" + port + "/cgi-bin/CGIProxy.fcgi?cmd=snapPicture2&usr=" + usr + "&pwd=" + passwd);
            	//e.getEngine().load("http://" + addr + ":" + port + "/cgi-bin/CGIProxy.fcgi?cmd=flipVideo&isFlip=1&usr=" + usr + "&pwd=" + passwd);
            }
        });
    }
    
    private static boolean initialized = false;

    private static void main() {
    	Logger.getLogger("scfvMain").log(Level.INFO, "launched main");
    	if (initialized == false) {
    		initialized = true;
    		SwingUtilities.invokeLater(new Runnable() {
    			@Override
    			public void run() {
    				initAndShowGUI();
    			}
    	});}
    	scfvMain cl = new scfvMain(0, 0, usr, passwd, addr, 0, 0, false, false);
    	int intervalx = cl.interval;
		scfvUpdater x = new scfvUpdater(intervalx);
    }
    
    private String parseCgiResult(final String xml) {
    	String result;
    	try {
    		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    		DocumentBuilder db = dbf.newDocumentBuilder();
    		ByteArrayInputStream bis = new ByteArrayInputStream(xml.getBytes());
    		Document doc = db.parse(bis);
    		Node n = doc.getFirstChild();
    		NodeList nl = n.getChildNodes();
    		Node an,an2;

    		for (int i=0; i < nl.getLength(); i++) {
    	    	an = nl.item(i);
    	    	if(an.getNodeType()==Node.ELEMENT_NODE) {
    	        	NodeList nl2 = an.getChildNodes();

    	        	for(int i2=0; i2<nl2.getLength(); i2++) {
    	            	an2 = nl2.item(i2);
    	            	if (nl2.toString().contains("result")) {
    	            		result = an2.getNodeValue();
    	            	}
    	        	}
    	    	}
    		}
    	} catch (Exception x) {
    		x.printStackTrace();
    	}
		return xml;
    }
}
