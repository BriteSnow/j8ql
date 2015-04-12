/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.query;

import org.j8ql.DB;
import org.j8ql.util.Immutables;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.j8ql.query.Query.fovs;

/**
 * <p></p>
 */
public class Condition implements Elem{
	private static final List<Elem> empty = Immutables.of(new Elem[0]);

	public enum Type {
		AND,OR;
	}

	private final Type type;
	private final List<Elem> elems;

	public Condition(Type type, Elem... elems) {
		this.type = type;
		this.elems = Immutables.of(elems);
	}

	public Condition(Type type, List<Elem> elems) {
		this.type = type;
		this.elems = Immutables.of(elems);
	}

	public Condition or(Object... nextElems){
		return or(fovs(nextElems));
	}

	public Condition or(Elem... nextElems){
		Condition current = new Condition(type,elems);
		Condition next = new  Condition(Type.OR,nextElems);
		return new Condition(Type.OR, current, next);
	}

	public Condition and(Object... nextElems){
		return and(fovs(nextElems));
	}

	public Condition and(Elem... nextElems){
		Condition current = new Condition(type,elems);
		Condition next = new Condition(Type.AND,nextElems);
		return new Condition(Type.AND, current, next);
	}

	public String toSql(DB db){
		return buildSql(db, new StringBuilder()).toString();
	}

	public Object[] toValues(DB db) {
		return buildValues(db, new ArrayList<>()).toArray();
	}

	// --------- Elem Implementation --------- //
	public List buildValues(DB db, List values){
		for (Elem elem : elems){
			elem.buildValues(db, values);
		}
		return values;
	}

	@Override
	public StringBuilder buildSql(DB db, StringBuilder sb) {
		boolean first = true;
		for (Elem elem : elems){
			if (!first){
				sb.append(' ').append(type).append(' ');
			}else{
				first = false;
			}
			boolean bracket = false;
			if (elem instanceof Condition){
				bracket = elem.getElems().size() > 1 && type != ((Condition)elem).type;
			}
			if (bracket) sb.append('(');
			elem.buildSql(db, sb);
			if (bracket) sb.append(')');
		}
		return sb;
	}

	// probably need to return an immutable array
	@Override
	public List<Elem> getElems(){
		return elems;
	}
	// --------- /Elem Implementation --------- //


	@Override
	public String toString(){
		String content = elems.stream().map(elem -> elem.toString()).collect(joining(" " + type + " "));
		if (elems.size() > 1){
			return "(" + content + ")";
		}else{
			return content;
		}

	}
}


