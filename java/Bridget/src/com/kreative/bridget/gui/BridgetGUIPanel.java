package com.kreative.bridget.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import com.kreative.bridget.*;

public class BridgetGUIPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	protected BridgetBoard bb;
	protected boolean oTurn;
	protected boolean p2Turn;
	protected BridgetPlayer p1;
	protected BridgetPlayer p2;
	
	protected BridgetBoardView bbv;
	protected JLabel status;
	
	public BridgetGUIPanel(BridgetBoard bb, boolean oTurn, boolean p2Turn, BridgetPlayer p1, BridgetPlayer p2) {
		super(new BorderLayout(8, 8));
		
		this.bb = bb;
		this.oTurn = oTurn;
		this.p2Turn = p2Turn;
		this.p1 = p1;
		this.p2 = p2;
		
		this.bbv = new BridgetBoardView(this.bb);
		this.bbv.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		
		JPanel board = new JPanel(new BorderLayout());
		board.add(this.bbv, BorderLayout.CENTER);
		board.add(makeColumns(this.bb.getSize()), BorderLayout.NORTH);
		board.add(makeColumns(this.bb.getSize()), BorderLayout.SOUTH);
		board.add(makeRows(this.bb.getSize()), BorderLayout.WEST);
		board.add(makeRows(this.bb.getSize()), BorderLayout.EAST);
		
		this.status = new JLabel();
		this.status.setFont(this.status.getFont().deriveFont(Font.BOLD, 16.0f));
		this.status.setHorizontalAlignment(JLabel.CENTER);
		
		this.add(board, BorderLayout.CENTER);
		this.add(status, BorderLayout.PAGE_START);
		this.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

		this.bbv.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent me) {
				if (!BridgetGUIPanel.this.bb.isGameOver()) {
					BridgetPoint bp = BridgetGUIPanel.this.bbv.getClickedPoint(me.getPoint());
					if (BridgetGUIPanel.this.bb.isValidMove(bp)) {
						if (BridgetGUIPanel.this.makeMove(bp)) BridgetGUIPanel.this.playGame();
					}
				}
			}
		});
		
		playGame();
	}
	
	protected void playGame() {
		while (true) {
			status.setText(oTurn ? "O's Turn" : "X's Turn");
			BridgetPoint move = (p2Turn ? p2 : p1).getMove(bb, oTurn);
			if (move == null) break; // wait for human to move
			if (!makeMove(move)) break; // break if game ended
		}
	}
	
	protected boolean makeMove(BridgetPoint move) {
		bb.makeMove(move, oTurn);
		if (bb.isGameOver()) {
			if (bb.xHasWon()) {
				status.setText("X Wins!");
			}
			else if (bb.oHasWon()) {
				status.setText("O Wins!");
			}
			else {
				status.setText("Game Over!");
			}
			return false;
		}
		oTurn = !oTurn;
		p2Turn = !p2Turn;
		return true;
	}
	
	public void write(ObjectOutputStream oos) throws IOException {
		oos.writeInt(0x47506E6C);
		oos.writeInt(0x00030000);
		oos.writeByte(oTurn ? 0 : 1);
		oos.writeByte(p2Turn ? 2 : 1);
		oos.writeShort(-1);
		oos.writeObject(bb);
		oos.writeObject(p1);
		oos.writeObject(p2);
		oos.writeObject(status.getText());
		oos.writeInt(0x6C6E5047);
	}
	
	public static BridgetGUIPanel read(ObjectInputStream ois) throws IOException {
		if (ois.readInt() != 0x47506E6C) throw new IOException("No magic");
		if (ois.readInt() != 0x00030000) throw new IOException("Wrong version");
		int ot = ois.readByte(); if (ot < 0 || ot > 1) throw new IOException("Invalid oTurn flag");
		int p2 = ois.readByte(); if (p2 < 1 || p2 > 2) throw new IOException("Invalid p2Turn flag");
		if (ois.readShort() != -1) throw new IOException("Invalid reserved field");
		BridgetBoard brb;
		BridgetPlayer pl1;
		BridgetPlayer pl2;
		String st;
		try {
			brb = (BridgetBoard)ois.readObject();
			pl1 = (BridgetPlayer)ois.readObject();
			pl2 = (BridgetPlayer)ois.readObject();
			st = (String)ois.readObject();
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
		if (brb == null || pl1 == null || pl2 == null || st == null) throw new IOException("Null objects");
		if (ois.readInt() != 0x6C6E5047) throw new IOException("No magic");
		BridgetGUIPanel bp = new BridgetGUIPanel(brb, (ot > 0), (p2 > 1), pl1, pl2);
		bp.status.setText(st);
		return bp;
	}
	
	private JPanel makeColumns(int w) {
		JPanel p;
		JLabel l;
		p = new JPanel(new FSGridLayout(1,w));
		for (int i = 1; i <= w; i++) {
			p.add(l = new JLabel(Integer.toString(i)));
			l.setForeground(Color.green.darker());
			l.setHorizontalAlignment(JLabel.CENTER);
		}
		
		JPanel pp = new JPanel(new BorderLayout());
		pp.add(l = new JLabel(""), BorderLayout.WEST);
		l.setForeground(Color.green.darker());
		l.setHorizontalAlignment(JLabel.CENTER);
		l.setMinimumSize(new Dimension(24, l.getMinimumSize().height));
		l.setPreferredSize(new Dimension(24, l.getPreferredSize().height));
		pp.add(p, BorderLayout.CENTER);
		pp.add(l = new JLabel(""), BorderLayout.EAST);
		l.setForeground(Color.green.darker());
		l.setHorizontalAlignment(JLabel.CENTER);
		l.setMinimumSize(new Dimension(28, l.getMinimumSize().height));
		l.setPreferredSize(new Dimension(28, l.getPreferredSize().height));
		return pp;
	}
	
	private JPanel makeRows(int w) {
		JPanel p;
		JLabel l;
		p = new JPanel(new FSGridLayout(w,1));
		for (int i = 1; i <= w; i++) {
			p.add(l = new JLabel(Integer.toString(i)));
			l.setForeground(Color.red.darker());
			l.setHorizontalAlignment(JLabel.CENTER);
			l.setMinimumSize(new Dimension(20, l.getMinimumSize().height));
			l.setPreferredSize(new Dimension(20, l.getPreferredSize().height));
		}
		
		p.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
		return p;
	}
}
