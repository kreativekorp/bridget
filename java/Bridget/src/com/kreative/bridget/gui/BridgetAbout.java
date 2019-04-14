package com.kreative.bridget.gui;

import java.awt.event.*;
import javax.swing.*;

public class BridgetAbout extends JFrame {
	private static final long serialVersionUID = 1L;

	public BridgetAbout() {
		setContentPane(new JLabel(new ImageIcon(BridgetAbout.class.getResource("about.png"))));
		setUndecorated(true);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				dispose();
			}
		});
	}
}
