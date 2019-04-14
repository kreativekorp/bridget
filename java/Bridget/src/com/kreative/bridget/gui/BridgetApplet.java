package com.kreative.bridget.gui;

import java.awt.event.*;
import java.util.Random;

import javax.swing.*;

import com.kreative.bridget.*;

public class BridgetApplet extends JApplet implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private boolean twoPlayer = true;
	private int size = 11;
	private boolean oStarts = true;
	private Thread gtw = null;
	
	private JMenuBar mbar;
	private JMenu fmenu;
	private JMenuItem miNew;
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
	
	public BridgetApplet() {
		mbar = new JMenuBar();
		mbar.add(fmenu = new JMenu("File"));
		fmenu.add(miNew = new JMenuItem("New Game"));
		mbar.add(menu = new JMenu("Options"));
		menu.add(mi1P = new JRadioButtonMenuItem("Single-Player"));
		menu.add(mi2P = new JRadioButtonMenuItem("Two-Player"));
		menu.addSeparator();
		menu.add(miSm = new JRadioButtonMenuItem("Small"));
		menu.add(miMe = new JRadioButtonMenuItem("Medium"));
		menu.add(miLa = new JRadioButtonMenuItem("Large"));
		menu.add(miHu = new JRadioButtonMenuItem("Huge"));
		menu.add(miGi = new JRadioButtonMenuItem("Gigantic"));
		menu.addSeparator();
		menu.add(miGTW = new JCheckBoxMenuItem("Global Thermonuclear War"));
		mbar.add(hmenu = new JMenu("Help"));
		hmenu.add(miHelp = new JMenuItem("How to Play"));
		hmenu.add(miAbout = new JMenuItem("About Bridget II"));
		
		miNew.addActionListener(this);
		mi1P.setSelected(!twoPlayer); mi1P.addActionListener(this);
		mi2P.setSelected(twoPlayer); mi2P.addActionListener(this);
		miSm.setSelected(size == BridgetBoard.SIZE_SMALL); miSm.addActionListener(this);
		miMe.setSelected(size == BridgetBoard.SIZE_MEDIUM); miMe.addActionListener(this);
		miLa.setSelected(size == BridgetBoard.SIZE_LARGE); miLa.addActionListener(this);
		miHu.setSelected(size == BridgetBoard.SIZE_HUGE); miHu.addActionListener(this);
		miGi.setSelected(size == BridgetBoard.SIZE_GIGANTIC); miGi.addActionListener(this);
		miGTW.addActionListener(this);
		miHelp.addActionListener(this);
		miAbout.addActionListener(this);
		
		setJMenuBar(mbar);
		reset();
		setVisible(true);
	}
	
	public void reset() {
		setContentPane(new BridgetGUIPanel(new BridgetBoard(size), oStarts = !oStarts, false, new BridgetGUIPlayer(), twoPlayer ? new BridgetGUIPlayer() : new BridgetAI()));
		validate();
		repaint();
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
						BridgetBoard bb = new BridgetBoard(BridgetApplet.this.size);
						BridgetGUIPanel bp = new BridgetGUIPanel(bb, BridgetApplet.this.oStarts = !BridgetApplet.this.oStarts, false, new BridgetGUIPlayer(), new BridgetGUIPlayer());
						BridgetApplet.this.setContentPane(bp);
						BridgetApplet.this.validate();
						BridgetApplet.this.repaint();
						Thread.sleep(5);
						while (true) {
							BridgetPoint p = new BridgetPoint(1+r.nextInt(BridgetApplet.this.size), 1+r.nextInt(BridgetApplet.this.size));
							while (!bb.isValidMove(p)) {
								p = new BridgetPoint(1+r.nextInt(BridgetApplet.this.size), 1+r.nextInt(BridgetApplet.this.size));
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
					BridgetGUIPanel bp = (BridgetGUIPanel)BridgetApplet.this.getContentPane();
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
}
