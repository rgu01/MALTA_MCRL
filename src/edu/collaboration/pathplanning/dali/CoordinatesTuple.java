package edu.collaboration.pathplanning.dali;

public class CoordinatesTuple {
	public final double lat;
	public final double lon;
	
	public CoordinatesTuple(double lat, double lon) {
		this.lat = lat;
		this.lon = lon;		
	}
	
	@Override
	public boolean equals(Object o) {
	    if (!(o instanceof CoordinatesTuple)) return false;
	    CoordinatesTuple co = (CoordinatesTuple) o;
	    return this.lat == co.lat && this.lon == co.lon;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = (int) (result * 31 + this.lat);
		result = (int) (result * 31 + this.lon);		
		return result;
	}
}
