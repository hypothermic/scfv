package nl.hypothermic.scfv_standalone;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class scfvConnectService extends Service<Boolean> {
	
	scfvConnectService(String addr, String port) {
		this.addr = addr;
		this.port = Integer.parseInt(port);
	}
	
	private String addr;
	private int port;
	
    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
            	if (connAttempt()) {
            		System.out.println("[DEBUG] Unreachable.");
            		throw new scfvAttemptException("1: unreachable");
            	}
            	System.out.println("[DEBUG] Reachable.");
                return true;
            }
        };
    }
    
    private boolean connAttempt() {
    	boolean err = true;
    	try {
        	StringBuffer sb = new StringBuffer();
        	sb.append( URLEncoder.encode("cmd") + "=" );
        	sb.append( URLEncoder.encode("getMultiDevDetailInfo"));
        	sb.append( "&" + URLEncoder.encode("usr") + "=" );
        	sb.append( URLEncoder.encode("loginTestAttempt"));
        	sb.append( "&" + URLEncoder.encode("pwd") + "=" );
        	sb.append( URLEncoder.encode("waarschijnlijkFout"));
        	String formData = sb.toString();
        	System.out.println("[INFO] Connecting to: http://" + addr.toString() + ":" + port + "/cgi-bin/CGIProxy.fcgi?" + formData);
        	StringBuilder result = new StringBuilder();
        	URL url = new URL("http://" + addr.toString() + ":" + port + "/cgi-bin/CGIProxy.fcgi?" + formData);
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
        	} else if (res.contains("0") || res.contains("-1") || res.contains("-2") || res.contains("-3")) {
        		// camera bereikbaar
        		err = false;
        	}
    	} catch (Exception x) {
    		System.out.println("[DEBUG] Exception in connAttempt");
    		x.printStackTrace();
    	}
    	return err;
    }
}