package nl.hypothermic.scfv_standalone;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

public class scfvLoginService extends Service<Boolean> {
	
	scfvLoginService(String user, String passwd, String addr, int port) {
		this.user = user;
		this.passwd = passwd;
		this.addr = addr;
		this.port = port;
	}
	
	private String user;
	private String passwd;
	private String addr;
	private int port;
	
    @Override
    protected Task<Boolean> createTask() {
        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
            	if (connAttempt()) {
            		System.out.println("[DEBUG] Login failed.");
            		throw new scfvAttemptException("6: loginfail");
            	}
            	System.out.println("[DEBUG] Login successful.");
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
        	sb.append( URLEncoder.encode(user));
        	sb.append( "&" + URLEncoder.encode("pwd") + "=" );
        	sb.append( URLEncoder.encode(passwd));
        	String formData = sb.toString();
        	System.out.println("[INFO] Connecting to: http://" + this.addr + ":" + this.port + "/cgi-bin/CGIProxy.fcgi?" + formData);
        	StringBuilder result = new StringBuilder();
        	URL url = new URL("http://" + this.addr + ":" + this.port + "/cgi-bin/CGIProxy.fcgi?" + formData);
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
        		// ingelogd.
        		err = false;
        	} else if (res.contains("-1")) {
        		throw new scfvAttemptException("C1: request string format error");
        	} else if (res.contains("-2")) {
        		throw new scfvAttemptException("C2: username or password error");
        	} else if (res.contains("-3")) {
        		throw new scfvAttemptException("C3: access denied");
        	} else if (res.contains("-4")) {
        		throw new scfvAttemptException("C4: cgi execute fail");
        	} else if (res.contains("-5")) {
        		throw new scfvAttemptException("C5: timeout");
        	} else if (res.contains("-7")) {
        		throw new scfvAttemptException("C7: unknown error");
        	}
    	} catch (Exception x) {
    		System.out.println("[DEBUG] Exception in connAttempt");
    		x.printStackTrace();
    	}
    	return err;
    }
}