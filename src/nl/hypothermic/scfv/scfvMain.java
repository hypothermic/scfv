package nl.hypothermic.scfv;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
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
	 * @version v1.1-beta
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
	private int width = 1065; // prev 810
	private int height = 656; // prev 520
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
		if (interval <= 40 && interval >= Integer.MAX_VALUE) { if (this.interval > 40 && this.interval < Integer.MAX_VALUE) { x.log(Level.FINER, "Info: using initialized value for port."); } else { x.log(Level.SEVERE, "Critical error: both initialized interval and passed interval are not compliant to standards."); return; }} else { /*<!--commented: bug? nullpointerexception spam!--> this.interval = interval;*/ }
		if (!newThread) {
			if (isConstructed == true) { main(); Logger.getLogger("scfvMain").log(Level.INFO, "Not created new thread");}
		} else {
			if (isConstructed == true) { new Thread() { public void run() { new scfvMain(width, height, usr, passwd, addr, port, interval, true, false); }}.start();; Logger.getLogger("scfvMain").log(Level.INFO, "Created new thread");}
		}
	}
	
    private static void initAndShowGUI() {
    	Logger.getLogger("scfvMain").log(Level.INFO, "Created GUI instance");
        scfvMain cl = new scfvMain(0, 0, usr, passwd, addr, 0, 0, false, false);
        frame = new JFrame("scfv - " + scfvMain.addr + cl.port);
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
        VBox picstream = new VBox();
        setPicStream(picstream);
        picstream.setAlignment(Pos.CENTER);
        VBox splashex = new VBox();
        splashex.setAlignment(Pos.CENTER);
        VBox splashef = new VBox();
        splashef.setAlignment(Pos.CENTER);
        BorderPane scrroot = new BorderPane();
        setScrRoot(scrroot);
        scrroot.getChildren().add(splashex);
        scrroot.getChildren().add(splashef);
        Scene scene = new Scene(scrroot);
        
        WebView x = new WebView();
        
        setWebView(x);

        Text xt = new Text();
        //Text xti = new Text();
        xt.setText("Powered by Hypothermic's SCFV\nhttps://github.com/hypothermic https://hypothermic.nl");
        //xti.setText("https://github.com/hypothermic https://hypothermic.nl");
        xt.setFont(new Font(25));
        //xti.setFont(new Font(11));
        picstream.getChildren().add(x);
        xt.setTextAlignment(TextAlignment.CENTER);
        splashef.getChildren().add(xt);
        //xti.setTextAlignment(TextAlignment.CENTER);
        //splashex.getChildren().add(xti);
        x.setVisible(false);

        return (scene);
    }
    
    private boolean requestXPost(String command, String param, String setter) {
		final scfvMain cl = new scfvMain(0, 0, usr, passwd, addr, 0, 0, false, false);
    	StringBuffer sb = new StringBuffer();
    	sb.append( URLEncoder.encode("cmd") + "=" );
    	sb.append( URLEncoder.encode(command));
    	sb.append( "&" + URLEncoder.encode(param) + "=" );
    	sb.append( URLEncoder.encode(setter));
    	sb.append( "&" + URLEncoder.encode("usr") + "=" );
    	sb.append( URLEncoder.encode(usr));
    	sb.append( "&" + URLEncoder.encode("pwd") + "=" );
    	sb.append( URLEncoder.encode(passwd));
    	String formData = sb.toString();
    	return xpost(formData);
    }
    
    private boolean xpost(String formData) {
    	boolean err = true;
    	try {
    		final scfvMain cl = new scfvMain(0, 0, usr, passwd, addr, 0, 0, false, false);
        	System.out.println("[INFO] Connecting to: http://" + addr.toString() + ":" + cl.port + "/cgi-bin/CGIProxy.fcgi" + formData);
        	StringBuilder result = new StringBuilder();
        	URL url = new URL("http:/" + addr.toString() + ":" + cl.port + "/cgi-bin/CGIProxy.fcgi?" + formData);
        	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        	conn.setRequestMethod("GET");
        	BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        	String line;
        	while ((line = rd.readLine()) != null) {
        		result.append(line);
        	}
        	rd.close();
        	System.out.println(result.toString());
        	String res = result.toString();
        	if (res == null | res == "" | res == " ") { System.out.println("[ERROR] Error: invalid CGI response"); 
        	} else if (res.contains("0")) {
        	// success
        	err = false;
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
    	} catch (Exception x) {
    		x.printStackTrace();
    	}
    	return err;
    }
    
    private boolean requestDataPost(String command, String regex) {
    	StringBuffer sb = new StringBuffer();
    	sb.append( URLEncoder.encode("cmd") + "=" );
    	sb.append( URLEncoder.encode(command));
    	sb.append( "&" + URLEncoder.encode("usr") + "=" );
    	sb.append( URLEncoder.encode(usr));
    	sb.append( "&" + URLEncoder.encode("pwd") + "=" );
    	sb.append( URLEncoder.encode(passwd));
    	String formData = sb.toString();
    	String res = null;
    	boolean flip = false;
    	try {
    		final scfvMain cl = new scfvMain(0, 0, usr, passwd, addr, 0, 0, false, false);
        	System.out.println("[INFO] Connecting to: http://" + addr.toString() + ":" + cl.port + "/cgi-bin/CGIProxy.fcgi" + formData);
        	StringBuilder result = new StringBuilder();
        	URL url = new URL("http:/" + addr.toString() + ":" + cl.port + "/cgi-bin/CGIProxy.fcgi?" + formData);
        	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        	conn.setRequestMethod("GET");
        	BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        	String line;
        	while ((line = rd.readLine()) != null) {
        		result.append(line);
        	}
        	rd.close();
        	System.out.println(parseCgiResult(result.toString()));
        	res = parseCgiResult(result.toString());
        	if (res == null | res == "" | res == " ") { System.out.println("[ERROR] Error: invalid CGI response"); System.exit(1); }
        	final Pattern pattern = Pattern.compile(regex);
        	final Matcher matcher = pattern.matcher(res);
        	matcher.find();
        	if (Integer.parseInt(matcher.group(1)) == 0) {
        		flip = false;
        	} else if (Integer.parseInt(matcher.group(1)) == 1) {
        		flip = true;
        	}
    	} catch (Exception x) {
    		x.printStackTrace(); System.exit(1);
    	}
    	return flip;
    }
    
    private static void setWebView(WebView x) {
    	e = x;
    }
    
    private static void setScrRoot(BorderPane x) {
    	scrroot = x;
    }
    
    private static void setPicStream(VBox x) {
    	picstream = x;
    }
    
    private static JFrame frame;
    
    private static WebView e;
    private static BorderPane scrroot;
    private static VBox picstream;
    

    public void updateWebView() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
            	e.setVisible(true);
            	//rtsp not supported e.getEngine().load("rtsp://" + addr + ":" + port + "/videoMain");
                e.getEngine().load("http://" + addr + ":" + port + "/cgi-bin/CGIProxy.fcgi?cmd=snapPicture2&usr=" + usr + "&pwd=" + passwd);
            	//e.getEngine().load("http://" + addr + ":" + port + "/cgi-bin/CGIProxy.fcgi?cmd=flipVideo&isFlip=1&usr=" + usr + "&pwd=" + passwd);
            }
        });
    }
    
    public void rmSplashScreen() {
    	// verwijder splashscreen
    	Platform.runLater(new Runnable() {
    		@Override
    		public void run() {
    			HBox ctrls = new HBox();
    			Button mirror = new Button("Mirror");
    			Button flip = new Button("Flip");
    	        ctrls.getChildren().add(mirror);
    	        ctrls.getChildren().add(flip);
                ctrls.setAlignment(Pos.CENTER_LEFT);
                picstream.setAlignment(Pos.CENTER);
                scrroot.getChildren().clear();
    	        //scrroot.getChildren().add(ctrls);
    	        //scrroot.getChildren().add(picstream);
    	        scrroot.setBottom(ctrls);
    	        scrroot.setCenter(picstream);
    	        scrroot.setAlignment(ctrls, Pos.BOTTOM_CENTER);
    	        scrroot.setAlignment(picstream, Pos.CENTER);
    	        mirror.setOnAction(new EventHandler<ActionEvent>() {
    	            @Override public void handle(ActionEvent e) {
    	                final scfvMain cl = new scfvMain(0, 0, usr, passwd, addr, 0, 0, false, false);
    	            	mirror.setVisible(false);
    	            	boolean isMirror = cl.requestDataPost("getMirrorAndFlipSetting", "<isMirror>(.+?)</isMirror>");
    	                System.out.println("[DEBUG] Mirror val retrieved: " + isMirror);
    	            	int state = 1;
    	            	if (isMirror) { state = 0; }
    	                System.out.println("[ACTION] Mirroring screen to " + state);
    	                mirror.setVisible(true);
    	                cl.requestXPost("mirrorVideo", "isMirror", "" + state);
    	                System.out.println("[INFO] Done mirroring screen!");
    	            }
    	        });
    	        flip.setOnAction(new EventHandler<ActionEvent>() {
    	            @Override public void handle(ActionEvent e) {
    	                final scfvMain cl = new scfvMain(0, 0, usr, passwd, addr, 0, 0, false, false);
    	                flip.setVisible(false);
    	                boolean isFlip = cl.requestDataPost("getMirrorAndFlipSetting", "<isFlip>(.+?)</isFlip>");
    	                System.out.println("[DEBUG] Flip val retrieved: " + isFlip);
    	            	int state = 1;
    	            	if (isFlip) { state = 0; }
    	                System.out.println("[ACTION] Flipping screen to " + state);
    	                flip.setVisible(true);
    	                cl.requestXPost("flipVideo", "isFlip", "" + state);
    	                System.out.println("[INFO] Done mirroring screen!");
    	            }
    	        });
    		}
    	});
    }
    
    private static boolean initialized = false;

    private static void main() {
    	Logger.getLogger("scfvMain").log(Level.INFO, "launched main");
    	boolean iss = true;
    	try {
    		if (frame.isShowing()) {
    			iss = false;
    		}
    	} catch (Exception x) { /*niets want dit is normaal*/}
    	if (/*initialized == false*/ iss) {
    		/*initialized = true;*/
    		frame = null;
    		scrroot = null;
    		picstream = null;
    		e = null;
    		
    		SwingUtilities.invokeLater(new Runnable() {
    			@Override
    			public void run() {
    				initAndShowGUI();
    			}
    	});}
    	final scfvMain cl = new scfvMain(0, 0, usr, passwd, addr, 0, 0, false, false);
    	final int intervalx = cl.interval;
    	System.out.println("Launching updater with interval of: " + intervalx + " ms");
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
    	            	if (nl2.toString().contains("isFlip")) {
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
    
    protected String post(String command) {
    	String res = null;
    	try {
    		final scfvMain cl = new scfvMain(0, 0, usr, passwd, addr, 0, 0, false, false);
        	StringBuffer sb = new StringBuffer();
        	sb.append( URLEncoder.encode("cmd") + "=" );
        	sb.append( URLEncoder.encode(command));
        	sb.append( "&" + URLEncoder.encode("usr") + "=" );
        	sb.append( URLEncoder.encode(usr));
        	sb.append( "&" + URLEncoder.encode("pwd") + "=" );
        	sb.append( URLEncoder.encode(passwd));
        	String formData = sb.toString();
        	System.out.println("[INFO] Connecting to: http://" + addr.toString() + ":" + cl.port + "/cgi-bin/CGIProxy.fcgi");
            StringBuilder result = new StringBuilder();
            URL url = new URL("http:/" + addr.toString() + ":" + cl.port + "/cgi-bin/CGIProxy.fcgi?" + formData);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
               result.append(line);
            }
            rd.close();
            res = parseCgiResult(result.toString());
    	} catch (Exception x) { x.printStackTrace(); System.exit(1); }
    	return res;
    }
}
