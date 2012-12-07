package net.vurs.common;

import geohash.Geohash;

public class GeoPoint {

	private Double lat = null;
	private Double lon = null;
	
	public GeoPoint(String hash) {
		double[] parts = Geohash.decode(hash);
		this.lat = parts[0];
		this.lon = parts[1];
	}
	
	public GeoPoint(Double lat, Double lon) {
		this.lat = lat;
		this.lon = lon;
	}
	
	public Double getLat() { return this.lat; }
	public Double getLng() { return this.lon; }
	
	public String geohash() {
		return Geohash.encode(this.lat, this.lon);
	}
	
}
