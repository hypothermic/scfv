package nl.hypothermic.scfv_standalone;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextPane;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.CardLayout;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.SystemColor;
import java.util.concurrent.CountDownLatch;

import javax.swing.JLabel;

public class scfvLauncher extends Application {

	// TODO: alles hier

	public static void main(String[] args) {
		launch(args);
	}

    public void start(Stage stage) throws Exception {
    	Parent root = FXMLLoader.load(getClass().getResource("scfvFxConnectscn.fxml"));
        Scene scene = new Scene(root, 599, 200);
        stage.setScene(scene);
        stage.show();
        setStage(stage);
    }
    
    private static Stage primaryStage; // **Declare static Stage**

    private void setStage(Stage stage) {
        scfvLauncher.primaryStage = stage;
    }

    static public Stage getStage() {
        return scfvLauncher.primaryStage;
    }
}
