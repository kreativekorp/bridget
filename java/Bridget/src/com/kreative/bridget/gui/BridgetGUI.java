package com.kreative.bridget.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Random;
import javax.swing.*;

import com.kreative.bridget.*;

public class BridgetGUI extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {}
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new BridgetGUI();
			}
		});
	}
	
	private boolean twoPlayer = true;
	private int size = 11;
	private boolean oStarts = true;
	private Thread gtw = null;
	
	private JMenuBar mbar;
	private JMenu fmenu;
	private JMenuItem miNew;
	private JMenuItem miOpen;
	private JMenuItem miSave;
	private JMenuItem miExit;
	private JMenu menu;
	private JRadioButtonMenuItem mi1P;
	private JRadioButtonMenuItem mi2P;
	private JRadioButtonMenuItem miSm;
	private JRadioButtonMenuItem miMe;
	private JRadioButtonMenuItem miLa;
	private JRadioButtonMenuItem miHu;
	private JRadioButtonMenuItem miGi;
	private JCheckBoxMenuItem miGTW;
	private JMenu hmenu;
	private JMenuItem miHelp;
	private JMenuItem miAbout;
	
	public BridgetGUI() {
		super("Bridget II");
		boolean mac = false;
		try {
			mac = System.getProperty("os.name").toUpperCase().contains("MAC OS");
			if (mac) try {
				System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Bridget II");
				System.setProperty("apple.laf.useScreenMenuBar", "true");
				System.setProperty("apple.awt.use-file-dialog-packages", "true");
			} catch (Exception ee) {}
		} catch (Exception e) {
			mac = false;
		}

		mbar = new JMenuBar();
		mbar.add(fmenu = new JMenu("File")); if (!mac) fmenu.setMnemonic(KeyEvent.VK_F);
		fmenu.add(miNew = new JMenuItem("New Game")); miNew.setMnemonic(KeyEvent.VK_N); miNew.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, this.getToolkit().getMenuShortcutKeyMask()));
		fmenu.add(miOpen = new JMenuItem("Open Saved Game...")); miOpen.setMnemonic(KeyEvent.VK_O); miOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, this.getToolkit().getMenuShortcutKeyMask()));
		fmenu.add(miSave = new JMenuItem("Save Game As...")); miSave.setMnemonic(KeyEvent.VK_S); miSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, this.getToolkit().getMenuShortcutKeyMask()));
		if (!mac) { fmenu.add(miExit = new JMenuItem("Exit")); miExit.setMnemonic(KeyEvent.VK_Q); miExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, this.getToolkit().getMenuShortcutKeyMask())); }
		mbar.add(menu = new JMenu("Options")); if (!mac) menu.setMnemonic(KeyEvent.VK_O);
		menu.add(mi1P = new JRadioButtonMenuItem("Single-Player")); mi1P.setMnemonic(KeyEvent.VK_R); mi1P.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, this.getToolkit().getMenuShortcutKeyMask()));
		menu.add(mi2P = new JRadioButtonMenuItem("Two-Player")); mi2P.setMnemonic(KeyEvent.VK_T); mi2P.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, this.getToolkit().getMenuShortcutKeyMask()));
		menu.addSeparator();
		menu.add(miSm = new JRadioButtonMenuItem("Small")); miSm.setMnemonic(KeyEvent.VK_S); miSm.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, this.getToolkit().getMenuShortcutKeyMask()));
		menu.add(miMe = new JRadioButtonMenuItem("Medium")); miMe.setMnemonic(KeyEvent.VK_M); miMe.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, this.getToolkit().getMenuShortcutKeyMask()));
		menu.add(miLa = new JRadioButtonMenuItem("Large")); miLa.setMnemonic(KeyEvent.VK_L); miLa.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, this.getToolkit().getMenuShortcutKeyMask()));
		menu.add(miHu = new JRadioButtonMenuItem("Huge")); miHu.setMnemonic(KeyEvent.VK_H); miHu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_6, this.getToolkit().getMenuShortcutKeyMask()));
		menu.add(miGi = new JRadioButtonMenuItem("Gigantic")); miGi.setMnemonic(KeyEvent.VK_G); miGi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_7, this.getToolkit().getMenuShortcutKeyMask()));
		menu.addSeparator();
		menu.add(miGTW = new JCheckBoxMenuItem("Global Thermonuclear War")); miGTW.setMnemonic(KeyEvent.VK_W); miGTW.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, this.getToolkit().getMenuShortcutKeyMask() | InputEvent.SHIFT_MASK));
		mbar.add(hmenu = new JMenu("Help")); if (!mac) hmenu.setMnemonic(KeyEvent.VK_H);
		hmenu.add(miHelp = new JMenuItem("How to Play")); miHelp.setMnemonic(KeyEvent.VK_H); miHelp.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, this.getToolkit().getMenuShortcutKeyMask()));
		if (!mac) { hmenu.add(miAbout = new JMenuItem("About Bridget II")); miAbout.setMnemonic(KeyEvent.VK_A); }
		
		miNew.addActionListener(this);
		miOpen.addActionListener(this);
		miSave.addActionListener(this);
		if (!mac) { miExit.addActionListener(this); }
		mi1P.setSelected(!twoPlayer); mi1P.addActionListener(this);
		mi2P.setSelected(twoPlayer); mi2P.addActionListener(this);
		miSm.setSelected(size == BridgetBoard.SIZE_SMALL); miSm.addActionListener(this);
		miMe.setSelected(size == BridgetBoard.SIZE_MEDIUM); miMe.addActionListener(this);
		miLa.setSelected(size == BridgetBoard.SIZE_LARGE); miLa.addActionListener(this);
		miHu.setSelected(size == BridgetBoard.SIZE_HUGE); miHu.addActionListener(this);
		miGi.setSelected(size == BridgetBoard.SIZE_GIGANTIC); miGi.addActionListener(this);
		miGTW.addActionListener(this);
		miHelp.addActionListener(this);
		if (!mac) { miAbout.addActionListener(this); }
		
		setJMenuBar(mbar);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				try { write(getSaveFile()); } catch (Exception e) { e.printStackTrace(); }
				System.exit(0);
			}
		});
		
		try {
			read(getSaveFile());
		} catch (Exception e) {
			e.printStackTrace();
			reset();
		}
		setResizable(false);
		setLocationRelativeTo(null);
		setVisible(true);
		
		if (mac) try {
			new BridgetMacAppListener(this);
		} catch (Exception e) {}
	}
	
	public void reset() {
		setContentPane(new BridgetGUIPanel(new BridgetBoard(size), oStarts = !oStarts, false, new BridgetGUIPlayer(), twoPlayer ? new BridgetGUIPlayer() : new BridgetAI()));
		pack();
	}
	
	public void startgtw() {
		if (gtw != null) {
			gtw.interrupt();
		}
		gtw = new Thread() {
			public void run() {
				try {
					Random r = new Random();
					while (true) {
						BridgetBoard bb = new BridgetBoard(BridgetGUI.this.size);
						BridgetGUIPanel bp = new BridgetGUIPanel(bb, BridgetGUI.this.oStarts = !BridgetGUI.this.oStarts, false, new BridgetGUIPlayer(), new BridgetGUIPlayer());
						BridgetGUI.this.setContentPane(bp);
						BridgetGUI.this.pack();
						Thread.sleep(5);
						while (true) {
							BridgetPoint p = new BridgetPoint(1+r.nextInt(BridgetGUI.this.size), 1+r.nextInt(BridgetGUI.this.size));
							while (!bb.isValidMove(p)) {
								p = new BridgetPoint(1+r.nextInt(BridgetGUI.this.size), 1+r.nextInt(BridgetGUI.this.size));
							}
							bp.makeMove(p);
							Thread.sleep(5);
							if (bb.isGameOver()) {
								Thread.sleep(95);
								break;
							}
						}
					}
				} catch (InterruptedException ie) {
					BridgetGUIPanel bp = (BridgetGUIPanel)BridgetGUI.this.getContentPane();
					bp.status.setText("The only winning move is not to play.");
				}
			}
		};
		gtw.start();
	}
	
	public void stopgtw() {
		if (gtw != null) {
			gtw.interrupt();
			gtw = null;
		}
	}
	
	public void actionPerformed(ActionEvent ae) {
		Object src = ae.getSource();
		if (src == miNew) {
			reset();
		}
		else if (src == miOpen) {
			FileDialog fd = new FileDialog(new Frame(), "Open Saved Game", FileDialog.LOAD);
			fd.setFilenameFilter(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".bri") || new File(dir, name).isDirectory();
				}
			});
			fd.setVisible(true);
			if (fd.getFile() != null) {
				File f = new File(fd.getDirectory()+System.getProperty("file.separator")+fd.getFile());
				try {
					read(f);
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, "That file was not recognized as a saved game file.");
					reset();
				}
			}
		}
		else if (src == miSave) {
			FileDialog fd = new FileDialog(new Frame(), "Save Game As", FileDialog.SAVE);
			fd.setFilenameFilter(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.endsWith(".bri") || new File(dir, name).isDirectory();
				}
			});
			fd.setVisible(true);
			if (fd.getFile() != null) {
				String f = fd.getDirectory()+System.getProperty("file.separator")+fd.getFile();
				if (!f.endsWith(".bri")) f += ".bri";
				try {
					write(new File(f));
				} catch (Exception e) {
					e.printStackTrace();
					JOptionPane.showMessageDialog(this, "Could not save to that file.");
					reset();
				}
			}
		}
		else if (src == miExit) {
			try { write(getSaveFile()); } catch (Exception e) { e.printStackTrace(); }
			System.exit(0);
		}
		else if (src == mi1P) {
			twoPlayer = false;
			mi1P.setSelected(true);
			mi2P.setSelected(false);
			reset();
		}
		else if (src == mi2P) {
			twoPlayer = true;
			mi1P.setSelected(false);
			mi2P.setSelected(true);
			reset();
		}
		else if (src == miSm) {
			size = BridgetBoard.SIZE_SMALL;
			miSm.setSelected(true);
			miMe.setSelected(false);
			miLa.setSelected(false);
			miHu.setSelected(false);
			miGi.setSelected(false);
			reset();
		}
		else if (src == miMe) {
			size = BridgetBoard.SIZE_MEDIUM;
			miSm.setSelected(false);
			miMe.setSelected(true);
			miLa.setSelected(false);
			miHu.setSelected(false);
			miGi.setSelected(false);
			reset();
		}
		else if (src == miLa) {
			size = BridgetBoard.SIZE_LARGE;
			miSm.setSelected(false);
			miMe.setSelected(false);
			miLa.setSelected(true);
			miHu.setSelected(false);
			miGi.setSelected(false);
			reset();
		}
		else if (src == miHu) {
			size = BridgetBoard.SIZE_HUGE;
			miSm.setSelected(false);
			miMe.setSelected(false);
			miLa.setSelected(false);
			miHu.setSelected(true);
			miGi.setSelected(false);
			reset();
		}
		else if (src == miGi) {
			size = BridgetBoard.SIZE_GIGANTIC;
			miSm.setSelected(false);
			miMe.setSelected(false);
			miLa.setSelected(false);
			miHu.setSelected(false);
			miGi.setSelected(true);
			reset();
		}
		else if (src == miGTW) {
			if (gtw != null) {
				stopgtw();
				miGTW.setSelected(false);
			}
			else {
				miGTW.setSelected(true);
				startgtw();
			}
		}
		else if (src == miHelp) {
			JOptionPane.showMessageDialog(this, "In this game, one player attempts to form a connected\nbridge from the top to the bottom of the board by\nconnecting horizontally or vertically adjacent green\ncircles. The other player attempts to form a bridge\nfrom the left to the right by connecting red squares.\nThe first one to form a bridge wins.");
		}
		else if (src == miAbout) {
			new BridgetAbout();
		}
	}
	
	public void write(File f) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
		oos.writeLong(0x4252494447455420L);
		oos.writeInt(0x00030000);
		oos.writeByte(twoPlayer ? 2 : 1);
		oos.writeByte(size);
		oos.writeByte(oStarts ? 0 : 1);
		oos.writeByte(-1);
		((BridgetGUIPanel)getContentPane()).write(oos);
		oos.writeLong(0x2054454744495242L);
		oos.close();
	}
	
	public void read(File f) throws IOException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
		try {
			if (ois.readLong() != 0x4252494447455420L) throw new IOException("No magic");
			if (ois.readInt() != 0x00030000) throw new IOException("Wrong version");
			int tp = ois.readByte(); if (tp < 1 || tp > 2) throw new IOException("Invalid player count");
			int sz = ois.readByte(); if (sz < BridgetBoard.SIZE_MIN) throw new IOException("Invalid board size");
			int os = ois.readByte(); if (os < 0 || os > 1) throw new IOException("Invalid oStarts flag");
			if (ois.readByte() != -1) throw new IOException("Invalid reserved field");
			BridgetGUIPanel bp = BridgetGUIPanel.read(ois);
			if (ois.readLong() != 0x2054454744495242L) throw new IOException("No magic");
			twoPlayer = (tp > 1);
			size = sz;
			oStarts = (os > 0);
			mi1P.setSelected(!twoPlayer);
			mi2P.setSelected(twoPlayer);
			miSm.setSelected(size == BridgetBoard.SIZE_SMALL);
			miMe.setSelected(size == BridgetBoard.SIZE_MEDIUM);
			miLa.setSelected(size == BridgetBoard.SIZE_LARGE);
			miHu.setSelected(size == BridgetBoard.SIZE_HUGE);
			miGi.setSelected(size == BridgetBoard.SIZE_GIGANTIC);
			setContentPane(bp);
			pack();
		} finally {
			ois.close();
		}
	}
	
	public File getSaveFile() {
		try {
			String os = System.getProperty("os.name").toUpperCase();
			File u = new File(System.getProperty("user.home"));
			if (os.contains("MAC OS")) {
				File l = new File(u, "Library");
				File p = new File(l, "Preferences");
				return new File(p, "com.kreative.bridget.savedgame.bri");
			}
			else if (os.contains("WINDOWS")) {
				File a = new File(u, "Application Data"); if (!a.exists()) a.mkdir();
				File k = new File(a, "Kreative"); if (!k.exists()) k.mkdir();
				return new File(k, "BridgetSavedGame.bri");
			}
			else {
				return new File(u, ".bridget-savedgame.bri");
			}
		} catch (Exception e) {
			return new File(".bridget-savedgame.bri");
		}
	}
}
