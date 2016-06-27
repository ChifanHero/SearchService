package com.sohungry.search.google.converter;

import java.util.List;

import se.walkercrou.places.Place;

public interface GooglePlacesInternalResponseBuilder<E> {
	
	public E build(List<Place> places);
}
