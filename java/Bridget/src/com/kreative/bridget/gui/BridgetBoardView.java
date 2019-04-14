package com.kreative.bridget.gui;

import java.awt.*;

import javax.swing.*;

import com.kreative.bridget.*;

public class BridgetBoardView extends JComponent implements BridgetListener {
	private static final long serialVersionUID = 1L;
	
	private final Image water = this.getToolkit().createImage(BridgetBoardView.class.getResource("water.jpg"));
	private BridgetBoard bb;
	
	public BridgetBoardView() {
		this.bb = null;
	}
	
	public BridgetBoardView(BridgetBoard bb) {
		this.bb = bb;
		this.bb.addListener(this);
	}
	
	public void setBridgetBoard(BridgetBoard bb) {
		if (this.bb != null) this.bb.removeListener(this);
		this.bb = bb;
		this.bb.addListener(this);
		repaint();
	}
	
	protected void finalize() throws Throwable {
		if (this.bb != null) this.bb.removeListener(this);
	}
	
	public BridgetBoard getBridgetBoard() {
		return bb;
	}
	
	protected void paintComponent(Graphics g) {
		Rectangle bounds = new Rectangle(0, 0, getWidth(), getHeight());
		Insets insets = getInsets();
		bounds.x += insets.left;
		bounds.y += insets.top;
		bounds.width -= (insets.left+insets.right);
		bounds.height -= (insets.top+insets.bottom);
		if (g instanceof Graphics2D)
			((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.clipRect(bounds.x, bounds.y, bounds.width, bounds.height);
		
		{
			int ww = water.getWidth(this);
			int wh = water.getHeight(this);
			if (ww > 0 && wh > 0) {
				for (int y = bounds.y; y < bounds.y+bounds.height; y += wh) {
					for (int x = bounds.x; x < bounds.x+bounds.width; x += ww) {
						g.drawImage(water, x, y, this);
					}
				}
			}
		}
		
		if (bb == null) return;
		int s = bb.getSize();
		int hw = Math.min(bounds.width, bounds.height);
		double dsx = ((double)bounds.x + ((double)(bounds.width-hw) / 2.0)) + ((double)(hw*-1) / (double)(s*3-1));
		double dmsx = ((double)bounds.x + ((double)(bounds.width-hw) / 2.0)) + ((double)(hw*0) / (double)(s*3-1));
		double dsy = ((double)bounds.y + ((double)(bounds.height-hw) / 2.0)) + ((double)(hw*-1) / (double)(s*3-1));
		double dmsy = ((double)bounds.y + ((double)(bounds.height-hw) / 2.0)) + ((double)(hw*0) / (double)(s*3-1));
		double di = (double)(hw*3) / (double)(s*3-1);
		double dw = (double)(hw*4) / (double)(s*3-1);
		double dmw = (double)(hw*2) / (double)(s*3-1);
		
		{
			BridgetPoint bp = new BridgetPoint(1,1);
			bp.y = 1; double dy = dsy; double dmy = dmsy;
			while (bp.y <= s) {
				bp.x = 1; double dx = dsx; double dmx = dmsx;
				while (bp.x <= s) {
					int what = bb.getBoardAt(bp);
					switch (what) {
					case BridgetBoard.BOARD_X_MARKER:
					{
						int x = (int)Math.round(dmx);
						int y = (int)Math.round(dmy);
						int w = (int)Math.round(dmx+dmw) - x;
						int h = (int)Math.round(dmy+dmw) - y;
						int t = (int)Math.round(dmw/10.0);
						g.setColor(Color.black);
						g.fillRect(x, y, w, h);
						g.setColor(new Color(0xFFFFDDDD));
						g.fillRect(x+t, y+t, w-t-t, h-t-t);
						g.setColor(new Color(0xFFFF6644));
						for (int o = 0; o < t; o++) {
							g.drawLine(x+t+t+o, y+t+t, x+w-t-t-1, y+h-t-t-o-1);
							g.drawLine(x+t+t, y+t+t+o, x+w-t-t-o-1, y+h-t-t-1);
							g.drawLine(x+t+t+o, y+h-t-t-1, x+w-t-t-1, y+t+t+o);
							g.drawLine(x+t+t, y+h-t-t-o-1, x+w-t-t-o-1, y+t+t);
						}
						
					}
						break;
					case BridgetBoard.BOARD_O_MARKER:
					{
						int x = (int)Math.round(dmx);
						int y = (int)Math.round(dmy);
						int w = (int)Math.round(dmx+dmw) - x;
						int h = (int)Math.round(dmy+dmw) - y;
						int t = (int)Math.round(dmw/10.0);
						int r1 = (int)Math.round(dmw/2.0);
						int r2 = (int)Math.round(dmw/2.0-dmw/5.0);
						g.setColor(Color.black);
						g.fillRoundRect(x, y, w, h, r1, r1);
						g.setColor(new Color(0xFFEEFFDD));
						g.fillRoundRect(x+t, y+t, w-t-t, h-t-t, r2, r2);
						g.setColor(new Color(0xFF33BB77));
						g.fillOval(x+t+t, y+t+t, w-t-t-t-t, h-t-t-t-t);
						g.setColor(new Color(0xFFEEFFDD));
						g.fillOval(x+t+t+t, y+t+t+t, w-t-t-t-t-t-t, h-t-t-t-t-t-t);
					}
						break;
					case BridgetBoard.BOARD_HORIZ_BRIDGE:
					{
						int x = (int)Math.round(dx);
						int y = (int)Math.round(dy+dw/2.0-dmw*5.0/16.0);
						int w = (int)Math.round(dx+dw) - x;
						int h = (int)Math.round(dy+dw/2.0+dmw*5.0/16.0) - y;
						int t = (int)Math.round(dmw/10.0);
						g.setColor(Color.black);
						g.fillRect(x, y, w, h);
						g.setColor(new Color(0xFFBB8811));
						g.fillRect(x, y+t, w, h-t-t);
						g.setColor(new Color(0xFF995500));
						for (int xx = x; xx < x+w; xx+=2) {
							g.drawLine(xx, y+t, xx, y+h-t-1);
						}
					}
						break;
					case BridgetBoard.BOARD_VERT_BRIDGE:
						int x = (int)Math.round(dx+dw/2.0-dmw*5.0/16.0);
						int y = (int)Math.round(dy);
						int w = (int)Math.round(dx+dw/2.0+dmw*5.0/16.0) - x;
						int h = (int)Math.round(dy+dw) - y;
						int t = (int)Math.round(dmw/10.0);
						g.setColor(Color.black);
						g.fillRect(x, y, w, h);
						g.setColor(new Color(0xFFBB8811));
						g.fillRect(x+t, y, w-t-t, h);
						g.setColor(new Color(0xFF995500));
						for (int yy = y; yy < y+h; yy+=2) {
							g.drawLine(x+t, yy, x+w-t-1, yy);
						}
						break;
					}
					bp.x++; dx += di; dmx += di;
				}
				bp.y++; dy += di; dmy += di;
			}
		}
	}
	
	public Dimension getMinimumSize() {
		if (bb == null) return super.getMinimumSize();
		Insets insets = getInsets();
		int s = bb.getSize();
		return new Dimension(insets.left+insets.right+3*(s*3-1), insets.top+insets.bottom+3*(s*3-1));
	}
	
	public Dimension getPreferredSize() {
		if (bb == null) return super.getPreferredSize();
		Insets insets = getInsets();
		int s = bb.getSize();
		return new Dimension(insets.left+insets.right+8*(s*3-1), insets.top+insets.bottom+8*(s*3-1));
	}
	
	public BridgetPoint getClickedPoint(Point p) {
		if (bb == null) return null;
		Rectangle bounds = new Rectangle(0, 0, getWidth(), getHeight());
		Insets insets = getInsets();
		bounds.x += insets.left;
		bounds.y += insets.top;
		bounds.width -= (insets.left+insets.right);
		bounds.height -= (insets.top+insets.bottom);
		int s = bb.getSize();
		int hw = Math.min(bounds.width, bounds.height);
		double o = (double)(-hw) / (double)(s*3-1) / 2.0;
		double d = (double)(hw*3) / (double)(s*3-1);
		int px = (int)Math.floor((p.x - ((double)bounds.x + ((double)(bounds.width-hw) / 2.0) + o)) / d);
		int py = (int)Math.floor((p.y - ((double)bounds.y + ((double)(bounds.height-hw) / 2.0) + o)) / d);
		return new BridgetPoint(px+1, py+1);
	}
	
	public void bridgetBoardCleared(BridgetBoard b) {
		repaint();
	}
	
	public void bridgetBoardCloned(BridgetBoard b) {
		repaint();
	}
	
	public void bridgetMove(BridgetBoard b, BridgetPoint p, boolean osTurn, boolean vertical) {
		repaint();
	}
}
