package com.kreative.bridget;

import java.io.Serializable;
import java.util.*;

public class BridgetAI implements BridgetPlayer, Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	private Random rand = new Random();
	
	public BridgetPoint getMove(BridgetBoard b, boolean osTurn) {
		Vector<BridgetPoint> cpath, upath;
		if (osTurn) {
			cpath = b.getShortestOPath();
			upath = b.getShortestXPath();
		} else {
			cpath = b.getShortestXPath();
			upath = b.getShortestOPath();
		}
		Vector<BridgetPoint> crsq = new Vector<BridgetPoint>();
		Iterator<BridgetPoint> cit = cpath.iterator();
		BridgetPoint clast = (cit.hasNext() ? cit.next() : null);
		while (cit.hasNext()) {
			BridgetPoint ccurr = cit.next();
			BridgetPoint sq = new BridgetPoint((ccurr.x+clast.x)/2, (ccurr.y+clast.y)/2);
			if (b.isValidMove(sq)) crsq.add(sq);
			clast = ccurr;
		}
		Vector<BridgetPoint> ursq = new Vector<BridgetPoint>();
		Iterator<BridgetPoint> uit = upath.iterator();
		BridgetPoint ulast = (uit.hasNext() ? uit.next() : null);
		while (uit.hasNext()) {
			BridgetPoint ucurr = uit.next();
			BridgetPoint sq = new BridgetPoint((ucurr.x+ulast.x)/2, (ucurr.y+ulast.y)/2);
			if (b.isValidMove(sq)) ursq.add(sq);
			ulast = ucurr;
		}
		
		BridgetPoint move = null;
		if (crsq.size() < 1 && ursq.size() < 1) {
			// no clue; go random
			move = new BridgetPoint();
			do {
				move.x = 1 + rand.nextInt(b.getSize());
				move.y = 1 + rand.nextInt(b.getSize());
			} while (!b.isValidMove(move));
		}
		else if (ursq.size() < 1) {
			// no user shortest path; shorten computer shortest path
			move = crsq.get(crsq.size()/2);
			while (!b.isValidMove(move)) {
				move = crsq.get(rand.nextInt(crsq.size()));
			}
		}
		else if (crsq.size() < 1) {
			// no computer shortest path; block user shortest path
			move = ursq.get(ursq.size()/2);
			while (!b.isValidMove(move)) {
				move = ursq.get(rand.nextInt(ursq.size()));
			}
		}
		else if (crsq.size() == 1) {
			// one square remaining on computer side; take it and win!
			move = crsq.get(0);
		}
		else if (ursq.size() == 1) {
			// one square remaining on user side; block it!
			move = ursq.get(0);
		}
		else if (crsq.size() <= ursq.size()) {
			// computer has fewer moves left; try to win
			Collections.sort(crsq, new DfpComparator(ursq));
			Iterator<BridgetPoint> i = crsq.iterator();
			do {
				if (i.hasNext()) move = i.next();
				else move = new BridgetPoint(1 + rand.nextInt(b.getSize()), 1 + rand.nextInt(b.getSize()));
			} while (!b.isValidMove(move));
		}
		else {
			// user has fewer moves left; try to prevent user winning
			Collections.sort(ursq, new DfpComparator(crsq));
			Iterator<BridgetPoint> i = ursq.iterator();
			do {
				if (i.hasNext()) move = i.next();
				else move = new BridgetPoint(1 + rand.nextInt(b.getSize()), 1 + rand.nextInt(b.getSize()));
			} while (!b.isValidMove(move));
		}
		return move;
	}
	
	private static class DfpComparator implements Comparator<BridgetPoint> {
		private Collection<BridgetPoint> path;
		public DfpComparator(Collection<BridgetPoint> path) {
			this.path = path;
		}
		public int compare(BridgetPoint a, BridgetPoint b) {
			return distancePointToPath(a,path).compareTo(distancePointToPath(b,path));
		}
		private static Integer distancePointToPath(BridgetPoint p, Collection<BridgetPoint> c) {
			int d = Integer.MAX_VALUE;
			for (BridgetPoint q : c) {
				int dd = p.distanceSq(q);
				if (dd < d) d = dd;
			}
			return d;
		}
	}
}
