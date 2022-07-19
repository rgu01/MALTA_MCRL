package edu.collaboration.pathplanning;

import java.util.ArrayList;
import java.util.List;

import com.afarcloud.thrift.Task;

/**
 * 
 * @author rgu01 A path is a set of path segments that shows the way of going
 *         from one node to another
 */
public class Path {
	public Node start;
	public Node end;
	public List<Node> segments;
	private double length = -1;

	public Path(Node s, Node e) {
		this.start = s;
		this.end = e;
		this.segments = new ArrayList<Node>();
	}

	public Path(Node s, Node e, List<Node> segs) {
		this.start = s;
		this.end = e;
		this.segments = segs;
	}
	
	public boolean equals(Node s, Node e) {
		return start.equals(s) && end.equals(e);
	}
	
	public void reverse() {
		List<Node> reversedSegments = new ArrayList<Node>();
		for(int i = this.segments.size() - 1; i >= 0; i--) {
			reversedSegments.add(this.segments.get(i));
		}
		Node olds = this.start;
		this.start = this.end;
		this.end = olds;
		this.segments  = reversedSegments;
	}
	
	public void setLength(double value) {
		this.length = value;
	}

	public double length() {
		if (this.length > -1) {
			return this.length;
		}
		double len = 0.0;
		Node n1 = null, n2 = null;
		PathSegment ps = null;

		if (this.segments.size() > 1) {
			n1 = this.segments.get(0);
			for (int i = 1; i < this.segments.size(); i++) {
				n2 = this.segments.get(i);
				ps = new PathSegment(n1, n2);
				len += ps.directLength();
				n1 = n2;
			}
		}

		return len;
	}

	/**
	 * 
	 * @param n1 One end of the path
	 * @param n2 Another end of the path
	 * @return if the path is connecting this two points
	 */
	public boolean isThePath(Node n1, Node n2) {
		boolean result = false;

		if (this.start.equals(n1) && this.end.equals(n2)) {
			result = true;
		} else if (this.start.equals(n2) && this.end.equals(n1)) {
			result = true;
		}

		return result;
	}

	@Override
	public boolean equals(Object o) {
		boolean result = false;
		// If the object is compared with itself then return true
		if (o == this) {
			return true;
		}

		/*
		 * Check if o is an instance of Path or not "null instanceof [type]" also
		 * returns false
		 */
		if (!(o instanceof Path)) {
			return false;
		}

		// typecast o to Path so that we can compare data members
		Path p = (Path) o;

		if (this.start.equals(p.start) && this.end.equals(p.end)) {
			result = true;
		} else if (this.start.equals(p.end) && this.end.equals(p.start)) {
			result = true;
		}

		return result;
	}

	@Override
	public Path clone() {
		Path copy = new Path(this.start.clone(), this.end.clone());
		for (Node node : this.segments) {
			copy.segments.add(node.clone());
		}

		return copy;
	}

	@Override
	public String toString() {
		return this.start.toString() + "->" + this.end.toString();
	}
}
