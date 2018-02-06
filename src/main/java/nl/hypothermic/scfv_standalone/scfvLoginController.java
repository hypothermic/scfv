package nl.hypothermic.scfv_standalone;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class scfvLoginController implements Initializable {
    
    @FXML private TextField userfi;
    @FXML private TextField passwdfi;
    @FXML private ProgressBar progbar;
    @FXML private Pane progpane;
    
    @FXML private void loginbtn(ActionEvent event) {
    	progpane.setVisible(true);
    	System.out.println("[DEBUG] Logging in as:" + userfi.getText());
    	final scfvLoginService srv = new scfvLoginService(userfi.getText(), passwdfi.getText(), scfvLogin.getAddr(), scfvLogin.getPort());
        srv.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent workerStateEvent) {
                Stage stage = (Stage) userfi.getScene().getWindow();
                stage.close();
                scfvProcess();
            }
            
            public void scfvProcess() {
            	// nieuwe thread
            	InetAddress iaddr = null;
            	try { iaddr = InetAddress.getByName(scfvLogin.getAddr()); 
            	} catch (UnknownHostException e1) { e1.printStackTrace(); }
            	final InetAddress xaddr = iaddr;
            	Thread scfvThread = new Thread() {
            		public void run() {
                        scfvMain x = new scfvMain(0, 0, userfi.getText(), passwdfi.getText(), xaddr, scfvLogin.getPort(), 0, true, false);
            		}
            	};
            	scfvThread.start();
            }
        });
        srv.setOnFailed(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent workerStateEvent) {
            	System.out.println("Failed because of ...");
            	if (srv.getException() == null) {
            		// zou onmogelijk moeten zijn
            		System.out.println("[CRITICAL] Unknown exception in scfvConnectService");
            		System.exit(1);
            	} else if (srv.getException() instanceof scfvAttemptException) {
                	// attemptxc's: verkeerde gebruikersnaam of wachtwoord.
            		System.out.println("[DEBUG] Unreachable.");
            		progpane.setVisible(false);
            	} else {
            		// andere xc
            		System.out.println("[DEBUG] Other exception.");
            		srv.getException().printStackTrace();
            		System.exit(1);
            	}
            }
        });
        srv.restart();
        System.out.println(srv.getValue());
    	System.out.println("Debug! Hello world!");
    }

	public void initialize(URL arg0, ResourceBundle arg1) {
	}
}