/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.util;


import java.util.*;

/**
 * <p>Simple Immutable Map and List factories that supports null values (Guava does not supports null values on their Immutables)</p>
 */
public class Immutables {

	public static <T> List<T> of(T... elements) {
		return of(Arrays.asList(elements));
	}

	public static <T> List<T> of(List<T> source) {
		List<T> copy = new ArrayList<T>(source);
		return Collections.unmodifiableList(copy);
	}

	public static <K,V> Map<K,V> of(Map<K,V> source){
		Map<K,V> copy = new LinkedHashMap<>(source.size());
		copy.putAll(source);
		return Collections.unmodifiableMap(copy);
	}
}
