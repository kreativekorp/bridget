package com.kreative.bridget;

public interface BridgetListener {
	public void bridgetBoardCleared(BridgetBoard b);
	public void bridgetBoardCloned(BridgetBoard b);
	public void bridgetMove(BridgetBoard b, BridgetPoint p, boolean osTurn, boolean vertical);
}
