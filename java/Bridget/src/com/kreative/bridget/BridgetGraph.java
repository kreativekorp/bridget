package com.kreative.bridget;

import java.io.Serializable;
import java.util.*;

public class BridgetGraph<T> implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	
	private Map<T,Map<T,Integer>> m;
	
	public BridgetGraph() {
		m = new HashMap<T,Map<T,Integer>>();
	}
	
	public BridgetGraph(BridgetGraph<T> g) {
		m = new HashMap<T,Map<T,Integer>>();
		for (T a : g.m.keySet()) {
			Map<T,Integer> n = new HashMap<T,Integer>();
			for (T b : g.m.get(a).keySet()) {
				n.put(b, g.m.get(a).get(b));
			}
			m.put(a, n);
		}
	}
	
	public void clear() {
		m = new HashMap<T,Map<T,Integer>>();
	}
	
	public boolean isEmpty() {
		return m.isEmpty();
	}
	
	public int size() {
		return m.size();
	}
	
	public void add(T a, T b) {
		put(a, b, 1);
	}
	
	public void add(T a, T b, int weight) {
		put(a, b, weight);
	}
	
	public void put(T a, T b, int weight) {
		Map<T,Integer> n;
		if ((n = m.get(a)) == null) {
			n = new HashMap<T,Integer>();
			n.put(b, weight);
			m.put(a, n);
		} else {
			n.put(b, weight);
		}
		if ((n = m.get(b)) == null) {
			n = new HashMap<T,Integer>();
			n.put(a, weight);
			m.put(b, n);
		} else {
			n.put(a, weight);
		}
	}
	
	public void remove(T a, T b) {
		Map<T,Integer> n;
		if ((n = m.get(a)) != null) {
			n.remove(b);
			if (n.isEmpty()) m.remove(a);
		}
		if ((n = m.get(b)) != null) {
			n.remove(a);
			if (n.isEmpty()) m.remove(b);
		}
	}
	
	public boolean contains(T a, T b) {
		Map<T,Integer> n;
		return ((n = m.get(a)) != null && n.containsKey(b)) || ((n = m.get(b)) != null && n.containsKey(a));
	}
	
	public int get(T a, T b) {
		Map<T,Integer> n;
		if ((n = m.get(a)) != null && n.containsKey(b)) return n.get(b);
		else if ((n = m.get(b)) != null && n.containsKey(a)) return n.get(a);
		else return Integer.MAX_VALUE;
	}
	
	public boolean containsPath(T a, T b) {
		if (!m.containsKey(a) || !m.containsKey(b)) return false;
		Set<T> visited = new HashSet<T>();
		Vector<T> toVisit = new Vector<T>();
		toVisit.add(a);
		while (!toVisit.isEmpty()) {
			T c = toVisit.remove(0);
			if (c.equals(b)) return true;
			else {
				visited.add(c);
				toVisit.addAll(m.get(c).keySet());
				toVisit.removeAll(visited);
			}
		}
		return false;
	}
	
	public Vector<T> getShortestPath(T a, T b) {
		// THIS IS DIJKSTRA'S ALGORITHM
		// SO DON'T USE THIS ON GRAPHS WITH NEGATIVE WEIGHTS!
		Map<T,Long> dist = new HashMap<T,Long>();
		Map<T,T> prev = new HashMap<T,T>();
		Set<T> q = new HashSet<T>();
		// dist[source] := 0
		dist.put(a, 0L);
		// Q := the set of all nodes in Graph
		q.addAll(m.keySet());
		// while Q is not empty:
		while (!q.isEmpty()) {
			// u := node in Q with smallest dist[]
			T u = null;
			long d = Long.MAX_VALUE;
			for (T t : q) {
				if (dist.containsKey(t) && (u == null || dist.get(t) < d)) {
					u = t;
					d = dist.get(t);
				}
			}
			// remove u from Q
			q.remove(u);
			// for each neighbor v of u:
			for (T v : m.get(u).keySet()) {
				// (where v has not yet been removed from Q)
				if (q.contains(v)) {
					// alt := dist[u] + dist_between(u, v)
					long alt = d + get(u,v);
					// if alt < dist[v]
					if (!dist.containsKey(v) || alt < dist.get(v)) {
						// dist[v] := alt
						dist.put(v, alt);
						// previous[v] := u
						prev.put(v, u);
					}
				}
			}
		}
		Vector<T> path = new Vector<T>();
		T u = b;
		while (u != null) {
			path.add(0, u);
			u = prev.get(u);
		}
		return path;
	}
	
	public Object clone() {
		return new BridgetGraph<T>(this);
	}
	
	public boolean equals(Object o) {
		return m.equals( (o instanceof BridgetGraph) ? ((BridgetGraph<?>)o).m : o );
	}
	
	public int hashCode() {
		return m.hashCode();
	}
}
