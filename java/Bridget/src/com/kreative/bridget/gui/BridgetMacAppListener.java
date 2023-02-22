package com.kreative.bridget.gui;

import java.awt.desktop.AboutEvent;
import java.awt.desktop.AboutHandler;
import java.awt.desktop.OpenFilesEvent;
import java.awt.desktop.OpenFilesHandler;
import java.awt.desktop.PrintFilesEvent;
import java.awt.desktop.PrintFilesHandler;
import java.awt.desktop.QuitEvent;
import java.awt.desktop.QuitHandler;
import java.awt.desktop.QuitResponse;
import java.io.File;
import java.lang.reflect.Method;
import javax.swing.JOptionPane;

public class BridgetMacAppListener {
	private static final String[][] classAndMethodNames = {
		{ "java.awt.Desktop", "getDesktop" },
		{ "com.kreative.ual.eawt.NewApplicationAdapter", "getInstance" },
		{ "com.kreative.ual.eawt.OldApplicationAdapter", "getInstance" },
	};
	
	private final BridgetGUI g;
	
	public BridgetMacAppListener(final BridgetGUI g) {
		this.g = g;
		for (String[] classAndMethodName : classAndMethodNames) {
			try {
				Class<?> cls = Class.forName(classAndMethodName[0]);
				Method getInstance = cls.getMethod(classAndMethodName[1]);
				Object instance = getInstance.invoke(null);
				cls.getMethod("setAboutHandler", AboutHandler.class).invoke(instance, about);
				cls.getMethod("setOpenFileHandler", OpenFilesHandler.class).invoke(instance, open);
				cls.getMethod("setPrintFileHandler", PrintFilesHandler.class).invoke(instance, print);
				cls.getMethod("setQuitHandler", QuitHandler.class).invoke(instance, quit);
				System.out.println("Registered app event handlers through " + classAndMethodName[0]);
				return;
			} catch (Exception e) {
				System.out.println("Failed to register app event handlers through " + classAndMethodName[0] + ": " + e);
			}
		}
	}
	
	private void readFile(final File f) {
		try {
			g.read(f);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(g, "That file was not recognized as a saved game file.");
			g.reset();
		}
	}
	
	private final AboutHandler about = new AboutHandler() {
		@Override
		public void handleAbout(final AboutEvent e) {
			new Thread() {
				public void run() {
					new BridgetAbout();
				}
			}.start();
		}
	};
	
	private final OpenFilesHandler open = new OpenFilesHandler() {
		@Override
		public void openFiles(final OpenFilesEvent e) {
			new Thread() {
				public void run() {
					for (Object o : e.getFiles()) {
						readFile((File)o);
					}
				}
			}.start();
		}
	};
	
	private final PrintFilesHandler print = new PrintFilesHandler() {
		@Override
		public void printFiles(final PrintFilesEvent e) {
			new Thread() {
				public void run() {
					for (Object o : e.getFiles()) {
						readFile((File)o);
					}
				}
			}.start();
		}
	};
	
	private final QuitHandler quit = new QuitHandler() {
		@Override
		public void handleQuitRequestWith(final QuitEvent e, final QuitResponse r) {
			new Thread() {
				public void run() {
					try { g.write(g.getSaveFile()); }
					catch (Exception e) { e.printStackTrace(); }
					r.performQuit();
					System.exit(0);
				}
			}.start();
		}
	};
}
