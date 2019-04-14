package com.kreative.bridget;

import java.util.Scanner;

public class BridgetTextUI {
	public static void main(String[] args) {
		boolean running = true;
		Scanner scan = new Scanner(System.in);
		System.out.println("Bridget II");
		System.out.println("(c) 2008 Kreative Software");
		System.out.println();
		while (running) {
			String ss;
			int size;
			BridgetPlayer x = null, o = null;
			
			System.out.print("Board size: ");
			ss = scan.hasNextLine() ? scan.nextLine().trim().toUpperCase() : "";
			if (ss.startsWith("X")) size = BridgetBoard.SIZE_MIN;
			else if (ss.startsWith("S")) size = BridgetBoard.SIZE_SMALL;
			else if (ss.startsWith("M")) size = BridgetBoard.SIZE_MEDIUM;
			else if (ss.startsWith("L")) size = BridgetBoard.SIZE_LARGE;
			else if (ss.startsWith("H")) size = BridgetBoard.SIZE_HUGE;
			else if (ss.startsWith("G")) size = BridgetBoard.SIZE_GIGANTIC;
			else try { size = Integer.parseInt(ss); }
			catch (NumberFormatException nfe) { size = 0; }
			if (size < BridgetBoard.SIZE_MIN) break;
			
			System.out.print("Number of players: ");
			ss = scan.hasNextLine() ? scan.nextLine().trim().toUpperCase() : "";
			if (ss.startsWith("G") || ss.startsWith("W") || ss.startsWith("Z")) { x = new BridgetAI(); o = new BridgetAI(); }
			else if (ss.startsWith("C") || ss.startsWith("O")) { x = new BridgetTextPlayer(scan); o = new BridgetAI(); }
			else if (ss.startsWith("H") || ss.startsWith("T")) { x = new BridgetTextPlayer(scan); o = new BridgetTextPlayer(scan); }
			else try {
				switch (Integer.parseInt(ss)) {
				case 0: x = new BridgetAI(); o = new BridgetAI(); break;
				case 1: x = new BridgetTextPlayer(scan); o = new BridgetAI(); break;
				case 2: x = new BridgetTextPlayer(scan); o = new BridgetTextPlayer(scan); break;
				default: x = null; o = null; break;
				}
			} catch (NumberFormatException nfe) { x = null; o = null; }
			if (x == null || o == null) break;
			
			BridgetBoard b = new BridgetBoard(size);
			printBoard(b);
			boolean osTurn = false;
			while (true) {
				BridgetPoint move = (osTurn ? o : x).getMove(b, osTurn);
				if (move == BridgetTextPlayer.QUIT) {
					running = false;
					break;
				}
				else if (move == BridgetTextPlayer.NEWGAME) {
					System.out.print("\f\u001B[2J\u001B[H");
					break;
				}
				else if (move == BridgetTextPlayer.LOAD) {
					// TODO load
					continue;
				}
				else if (move == BridgetTextPlayer.SAVE) {
					// TODO save
					continue;
				}
				else {
					b.makeMove(move, osTurn);
					printBoard(b);
				}
				boolean owon = b.oHasWon();
				boolean xwon = b.xHasWon();
				if (owon || xwon) {
					System.out.println((owon ? "O" : "X")+" Wins!");
					System.out.print("New game? ");
					ss = scan.hasNextLine() ? scan.nextLine().trim().toUpperCase() : "";
					running = ss.startsWith("Y");
					break;
				}
				osTurn = !osTurn;
			}
		}
	}
	
	public static void printBoard(BridgetBoard b) {
		System.out.print("\f\u001B[2J\u001B[H");
		System.out.print("   ");
		for (int i = 1; i <= b.getSize(); i++) {
			System.out.print(" "+(char)('A'+i-1)+" ");
		}
		System.out.println();
		BridgetPoint p = new BridgetPoint();
		for (p.y = 1; p.y <= b.getSize(); p.y++) {
			if (p.y < 10) System.out.print(" "+p.y+" ");
			else System.out.print(p.y+" ");
			for (p.x = 1; p.x <= b.getSize(); p.x++) {
				switch (b.getBoardAt(p)) {
				case BridgetBoard.BOARD_HORIZ_BRIDGE: System.out.print("---"); break;
				case BridgetBoard.BOARD_VERT_BRIDGE: System.out.print(" | "); break;
				case BridgetBoard.BOARD_X_MARKER: System.out.print("[X]"); break;
				case BridgetBoard.BOARD_O_MARKER: System.out.print("(O)"); break;
				default: System.out.print("   "); break;
				}
			}
			System.out.println();
		}
	}
}
