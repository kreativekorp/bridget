package com.kreative.bridget.gui;

import java.io.File;
import javax.swing.JOptionPane;

@SuppressWarnings("deprecation")
public class BridgetMacAppListener implements com.apple.eawt.ApplicationListener {
	private BridgetGUI g;
	
	public BridgetMacAppListener(BridgetGUI g) {
		this.g = g;
		com.apple.eawt.Application a = com.apple.eawt.Application.getApplication();
		a.addAboutMenuItem();
		a.setEnabledAboutMenu(true);
		a.removePreferencesMenuItem();
		a.addApplicationListener(this);
	}

	public void handleAbout(com.apple.eawt.ApplicationEvent arg0) {
		new BridgetAbout();
		arg0.setHandled(true);
	}

	public void handleOpenApplication(com.apple.eawt.ApplicationEvent arg0) {
		// nothing
	}
	
	public void handleOpenFile(com.apple.eawt.ApplicationEvent arg0) {
		File f = new File(arg0.getFilename());
		try {
			g.read(f);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(g, "That file was not recognized as a saved game file.");
			g.reset();
		}
		arg0.setHandled(true);
	}

	public void handlePreferences(com.apple.eawt.ApplicationEvent arg0) {
		// nothing
	}

	public void handlePrintFile(com.apple.eawt.ApplicationEvent arg0) {
		handleOpenFile(arg0); // no printing though
		arg0.setHandled(true);
	}

	public void handleQuit(com.apple.eawt.ApplicationEvent arg0) {
		try { g.write(g.getSaveFile()); } catch (Exception e) { e.printStackTrace(); }
		System.exit(0);
	}

	public void handleReOpenApplication(com.apple.eawt.ApplicationEvent arg0) {
		// nothing
	}
}
