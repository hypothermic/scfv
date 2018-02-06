package nl.hypothermic.scfv_standalone;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class scfvLogin {
	
	public static void doLaunch(String[] args) {
		final scfvLogin tmp = new scfvLogin();
		try {
			tmp.show(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void show(String[] args) throws IOException{
		if (!(args.length < 2) && !(args.length > 2) && args[0] != null && args[1] != null) {
			final scfvLogin tmp = new scfvLogin(args[0], args[1]);
		    Parent root = FXMLLoader.load(tmp.getClass().getResource("scfvFxLoginscn.fxml"));
		    Scene scene = new Scene(root, 599, 93);
		    Stage stage = scfvLauncher.getStage();
		    stage.setScene(scene);
		    stage.show();
		} else {
			System.out.println("[ERROR] scfvLogin: Args not set properly");
		}
	}
	
	scfvLogin(String addr, String port) {
		// recursive setter
		this.addr = addr;
		this.port = Integer.parseInt(port);
	}
	
	scfvLogin() {
		// lege var constructor, anders zouden getters static moeten zijn.
	}
	
	private static String addr;
	private static int port;
	
	public static String getAddr() {
		return addr;
	}
	
	public static int getPort() {
		return port;
	}
}
