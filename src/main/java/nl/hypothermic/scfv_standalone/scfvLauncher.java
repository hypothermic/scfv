package nl.hypothermic.scfv_standalone;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextPane;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.SystemColor;
import javax.swing.JLabel;

public class scfvLauncher {

	// TODO: alles hier
	
	private JFrame mainpanel;
	private JTextField addrif;
	private JTextField portif;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					scfvLauncher window = new scfvLauncher();
					window.mainpanel.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public scfvLauncher() {
		ui();
	}

	private void ui() {
		// constructueer fxml + toewijzen rsc
	}
}
