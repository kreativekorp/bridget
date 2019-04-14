package com.kreative.bridget;

import java.io.Serializable;
import java.util.Vector;

public class BridgetBoard implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	public static final int SIZE_MIN = 3;
	public static final int SIZE_SMALL = 7;
	public static final int SIZE_MEDIUM = 11;
	public static final int SIZE_LARGE = 15;
	public static final int SIZE_HUGE = 19;
	public static final int SIZE_GIGANTIC = 23;
	
	public static final int BOARD_INVALID = -1;
	public static final int BOARD_EMPTY = 0;
	public static final int BOARD_HORIZ_BRIDGE = 1;
	public static final int BOARD_VERT_BRIDGE = 2;
	public static final int BOARD_X_MARKER = 3;
	public static final int BOARD_O_MARKER = 4;
	
	private static final int EQUIVALENT = 1;
	private static final int CONNECTED = 100;
	private static final int DISCONNECTED = 100000;
	
	private int size;
	private BridgetPoint xstart, xend;
	private BridgetPoint ostart, oend;
	private transient Vector<BridgetListener> listeners;
	
	private int[][] board;
	private BridgetGraph<BridgetPoint> connected;
	private BridgetGraph<BridgetPoint> xpaths;
	private BridgetGraph<BridgetPoint> opaths;
	
	public BridgetBoard(int size) {
		while (size % 4 != 3) size++;
		if (size < SIZE_MIN) size = SIZE_MIN;
		this.size = size;
		xstart = new BridgetPoint(1, (size+1)/2);
		xend = new BridgetPoint(size, (size+1)/2);
		ostart = new BridgetPoint((size+1)/2, 1);
		oend = new BridgetPoint((size+1)/2, size);
		listeners = new Vector<BridgetListener>();
		clear();
	}
	
	public BridgetBoard(BridgetBoard b) {
		this.size = b.size;
		xstart = new BridgetPoint(b.xstart);
		xend = new BridgetPoint(b.xend);
		ostart = new BridgetPoint(b.ostart);
		oend = new BridgetPoint(b.oend);
		listeners = new Vector<BridgetListener>();
		listeners.addAll(b.listeners);
		board = new int[b.board.length][];
		for (int i = 0; i<b.board.length; i++) {
			board[i] = new int[b.board[i].length];
			for (int j = 0; j<b.board[i].length; i++) {
				board[i][j] = b.board[i][j];
			}
		}
		connected = new BridgetGraph<BridgetPoint>(b.connected);
		xpaths = new BridgetGraph<BridgetPoint>(b.xpaths);
		opaths = new BridgetGraph<BridgetPoint>(b.opaths);
		for (BridgetListener l : listeners) l.bridgetBoardCloned(this);
	}
	
	public Object clone() {
		return new BridgetBoard(this);
	}
	
	public void addListener(BridgetListener l) {
		if (listeners == null) listeners = new Vector<BridgetListener>();
		listeners.add(l);
	}
	
	public void removeListener(BridgetListener l) {
		if (listeners == null) return;
		listeners.remove(l);
	}
	
	public BridgetListener[] getListeners() {
		if (listeners == null) return new BridgetListener[0];
		return listeners.toArray(new BridgetListener[0]);
	}
	
	public int getSize() {
		return size;
	}
	
	public boolean isValidMove(BridgetPoint p) {
		return (p.x > 1 && p.y > 1 && p.x < size && p.y < size
				&& (p.x % 2) == (p.y % 2)
				&& board[p.y-1][p.x-1] == BOARD_EMPTY);
	}
	
	public int getBoardAt(BridgetPoint p) {
		if (p.x < 1 || p.y < 1 || p.x > size || p.y > size) return BOARD_INVALID;
		else if ((p.x % 2) != (p.y % 2)) return ((p.x % 2 == 1) ? BOARD_X_MARKER : BOARD_O_MARKER);
		else return board[p.y-1][p.x-1];
	}
	
	public void clear() {
		board = new int[size][size];
		connected = new BridgetGraph<BridgetPoint>();
		xpaths = new BridgetGraph<BridgetPoint>();
		opaths = new BridgetGraph<BridgetPoint>();
		for (int i = 2; i <= size; i += 2) {
			connected.add(xstart, new BridgetPoint(1, i));
			connected.add(xend, new BridgetPoint(size, i));
			connected.add(ostart, new BridgetPoint(i, 1));
			connected.add(oend, new BridgetPoint(i, size));
			xpaths.add(xstart, new BridgetPoint(1, i), EQUIVALENT);
			xpaths.add(xend, new BridgetPoint(size, i), EQUIVALENT);
			opaths.add(ostart, new BridgetPoint(i, 1), EQUIVALENT);
			opaths.add(oend, new BridgetPoint(i, size), EQUIVALENT);
		}
		for (int y = 2; y <= size; y += 2) {
			for (int x = 2; x <= size; x += 2) {
				xpaths.add(new BridgetPoint(x-1,y), new BridgetPoint(x+1,y), DISCONNECTED);
				opaths.add(new BridgetPoint(x,y-1), new BridgetPoint(x,y+1), DISCONNECTED);
			}
		}
		for (int y = 3; y <= size-1; y += 2) {
			for (int x = 3; x <= size-1; x += 2) {
				xpaths.add(new BridgetPoint(x,y-1), new BridgetPoint(x,y+1), DISCONNECTED);
				opaths.add(new BridgetPoint(x-1,y), new BridgetPoint(x+1,y), DISCONNECTED);
			}
		}
		for (BridgetListener l : listeners) l.bridgetBoardCleared(this);
	}
	
	public void makeMove(BridgetPoint p, boolean osTurn) {
		BridgetPoint xs, xe, os, oe;
		boolean vert;
		if (p.x % 2 == 1) {
			xs = new BridgetPoint(p.x, p.y-1);
			xe = new BridgetPoint(p.x, p.y+1);
			os = new BridgetPoint(p.x-1, p.y);
			oe = new BridgetPoint(p.x+1, p.y);
			vert = !osTurn;
		} else {
			xs = new BridgetPoint(p.x-1, p.y);
			xe = new BridgetPoint(p.x+1, p.y);
			os = new BridgetPoint(p.x, p.y-1);
			oe = new BridgetPoint(p.x, p.y+1);
			vert = osTurn;
		}
		board[p.y-1][p.x-1] = vert ? BOARD_VERT_BRIDGE : BOARD_HORIZ_BRIDGE;
		if (osTurn) {
			connected.add(os, oe);
			opaths.put(os, oe, CONNECTED);
			xpaths.remove(xs, xe);
		} else {
			connected.add(xs, xe);
			xpaths.put(xs, xe, CONNECTED);
			opaths.remove(os, oe);
		}
		for (BridgetListener l : listeners) l.bridgetMove(this, p, osTurn, vert);
	}
	
	public boolean xHasWon() {
		return connected.containsPath(xstart, xend);
	}
	
	public boolean oHasWon() {
		return connected.containsPath(ostart, oend);
	}
	
	public boolean isGameOver() {
		return xHasWon() || oHasWon();
	}
	
	public BridgetGraph<BridgetPoint> getXPaths() {
		return xpaths;
	}
	
	public BridgetGraph<BridgetPoint> getOPaths() {
		return opaths;
	}
	
	public Vector<BridgetPoint> getShortestXPath() {
		return xpaths.getShortestPath(xstart, xend);
	}
	
	public Vector<BridgetPoint> getShortestOPath() {
		return opaths.getShortestPath(ostart, oend);
	}
}
