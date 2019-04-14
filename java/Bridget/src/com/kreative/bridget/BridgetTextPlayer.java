package com.kreative.bridget;

import java.util.*;

public class BridgetTextPlayer implements BridgetPlayer {
	public static final BridgetPoint QUIT = new BridgetPoint(-1,-1);
	public static final BridgetPoint NEWGAME = new BridgetPoint(-2,-2);
	public static final BridgetPoint SAVE = new BridgetPoint(-3,-3);
	public static final BridgetPoint LOAD = new BridgetPoint(-4,-4);
	
	private Scanner scan;
	
	public BridgetTextPlayer() {
		scan = new Scanner(System.in);
	}
	
	public BridgetTextPlayer(Scanner scan) {
		this.scan = scan;
	}
	
	public BridgetPoint getMove(BridgetBoard b, boolean osTurn) {
		while (true) {
			System.out.print((osTurn?"O":"X")+"'s Move: ");
			if (!scan.hasNextLine()) return QUIT;
			String in = scan.nextLine().trim().toUpperCase();
			if (in.length() > 0) { 
				if (in.startsWith("QU") || in.startsWith("EX") || in.startsWith("BY")) return QUIT;
				else if (in.startsWith("NE")) return NEWGAME;
				else if (in.startsWith("SA") || in.startsWith("WR")) return SAVE;
				else if (in.startsWith("LO") || in.startsWith("RE")) return LOAD;
				else if (in.startsWith("IN") || in.startsWith("HE")) {
					System.out.println("In this game, one player attempts to form a connected bridge from the top");
					System.out.println("to the bottom of the board by connecting horizontally or vertically adjacent");
					System.out.println("circles. The other player attempts to form a bridge from the left to the");
					System.out.print  ("right by connecting red squares. The first one to form a bridge wins.");
					if (scan.hasNextLine()) scan.nextLine();
				}
				else if (in.startsWith("CO") || in.startsWith("AB")) {
					System.out.println("(C) 2008 Kreative Software");
					System.out.println("So far I have implemented this game for Apple II BASIC, Chipmunk Basic,");
					System.out.println("QuickBASIC, REALbasic, HyperCard, iPodLinux, Cocoa Touch, and here Java.");
					System.out.print  ("Apparently it's doomed to become my hallmark.");
					if (scan.hasNextLine()) scan.nextLine();
				}
				else if (in.contains(",") || in.contains(".") || in.contains(";") || in.contains(":")) {
					String[] ina = in.split("[,.;:]");
					if (ina.length == 2) {
						try {
							BridgetPoint move = new BridgetPoint(Integer.parseInt(ina[0]), Integer.parseInt(ina[1]));
							if (b.isValidMove(move)) return move;
						} catch (NumberFormatException nfe) {}
					}
					System.out.println("Invalid move. Try again.");
				}
				else if (Character.isLetter(in.charAt(0))) {
					try {
						BridgetPoint move = new BridgetPoint(in.charAt(0)-'A'+1, Integer.parseInt(in.substring(1)));
						if (b.isValidMove(move)) return move;
					} catch (NumberFormatException nfe) {}
					System.out.println("Invalid move. Try again.");
				}
				else {
					System.out.println("Invalid move. Try again.");
				}
			}
		}
	}
}
