package org.j8ql.query;

import org.jomni.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Case {

	private String on;
	private List<Pair<Object,Object>> whenThenList = new ArrayList<>();
	private Object orElse;
	private String alias;

	private Case(){
	}

	private Case(Case source) {
		on = source.on;
		whenThenList.addAll(0, source.whenThenList);
		orElse = source.orElse;
		alias = source.alias;
	}

	public String getAlias() {
		return alias;
	}

	public String getOn() {
		return on;
	}

	public Object getOrElse() {
		return orElse;
	}

	public List<Pair<Object, Object>> getWhenThenList() {
		return whenThenList;
	}

	// --------- Builder --------- //
	public static class CaseBuilder{
		private Case c = new Case();

		public CaseBuilder on(String expression){
			c.on = expression;
			return this;
		}

		public CaseBuilder whenThen(Object expression, Object value) {
			c.whenThenList.add(new Pair(expression, value));
			return this;
		}

		public CaseBuilder orElse(Object value) {
			c.orElse = value;
			return this;
		}

		public CaseBuilder alias(String alias) {
			c.alias = alias;
			return this;
		}

		public Case build() {
			return new Case(c);
		}
	}
	// --------- /Builder --------- //
}
