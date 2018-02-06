package nl.hypothermic.scfv_standalone;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
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

public class scfvLauncherController implements Initializable {
    
    @FXML private TextField addrfi;
    @FXML private TextField portfi;
    @FXML private ProgressBar progbar;
    @FXML private Pane progpane;
    
    @FXML private void connectbtn(ActionEvent event) {
    	progpane.setVisible(true);
    	final String addr = addrfi.getText(); final String port = portfi.getText();
    	System.out.println("[DEBUG] Connecting to " + addr + ":" + port);
    	final scfvConnectService srv = new scfvConnectService(addrfi.getText(), portfi.getText());
        srv.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            public void handle(WorkerStateEvent workerStateEvent) {
            	// login scr
            	/*comment, werkt niet: Application.launch(scfvLogin.class, new String[] {addr, port});*/
                Stage stage = (Stage) addrfi.getScene().getWindow();
                stage.hide();
            	scfvLogin.doLaunch(new String[] {addr, port});
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
                	// attemptxc's: port geen int, cam niet bereikbaar of geen fcgi
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
    }

	public void initialize(URL arg0, ResourceBundle arg1) {
	}
}