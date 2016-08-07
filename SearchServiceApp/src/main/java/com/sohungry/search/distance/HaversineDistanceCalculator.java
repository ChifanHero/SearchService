package com.sohungry.search.distance;

public class HaversineDistanceCalculator {
	
	private static final double EARTH_RADIUS_MI = 3958.75;
	private static final double EARTH_RADIUS_KM = 6371.0;
	

	public static Double getDistanceInMi(Coordinates pos1, Coordinates pos2) {
		return getDistance(EARTH_RADIUS_MI, pos1, pos2);
	}
	
	public static Double getDistanceInKm(Coordinates pos1, Coordinates pos2) {
		return getDistance(EARTH_RADIUS_KM, pos1, pos2);
	}
	
	public static Double getDistanceInKm(Double lat1, Double lon1, Double lat2, Double lon2) {
		if (lat1 == null || lat2 == null || lon1 == null || lon2 == null) return null;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lon2 - lon1);
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
		double a = Math.pow(sindLat, 2)
				+ Math.pow(sindLng, 2) * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = EARTH_RADIUS_KM * c;
		return dist;
	}
	
	private static Double getDistance(double earthRadius, Coordinates pos1, Coordinates pos2) {
		if (pos1 == null || pos2 == null) return null;
		double dLat = Math.toRadians(pos2.getLat() - pos1.getLat());
		double dLng = Math.toRadians(pos2.getLon() - pos1.getLon());
		double sindLat = Math.sin(dLat / 2);
		double sindLng = Math.sin(dLng / 2);
		double a = Math.pow(sindLat, 2)
				+ Math.pow(sindLng, 2) * Math.cos(Math.toRadians(pos1.getLat())) * Math.cos(Math.toRadians(pos2.getLat()));
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double dist = earthRadius * c;
		return dist;
	}

}
