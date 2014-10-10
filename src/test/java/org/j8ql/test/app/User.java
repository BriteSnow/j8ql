/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.test.app;

public class User {

    private Long id;
    private String username;
    private String title;
	private Integer since;
	private Pref pref;

    
    public Long getId() {
        return id;
    }
    public User setId(Long id) {
        this.id = id;
		return this;
    }

	public String getUsername() {
        return username;
    }
    public User setUsername(String username) {
        this.username = username;
		return this;
    }

	public String getTitle() {
		return title;
	}
	public User setTitle(String title) {
		this.title = title;
		return this;
	}

	public Integer getSince() {
		return since;
	}
	public void setSince(Integer since) {
		this.since = since;
	}

	public Pref getPref() {
		return pref;
	}
	public void setPref(Pref pref) {
		this.pref = pref;
	}

	public static class Pref {
		private String lang;
		private Long rank;
		private String type;

		public String getLang() {
			return lang;
		}

		public void setLang(String lang) {
			this.lang = lang;
		}

		public Long getRank() {
			return rank;
		}

		public void setRank(Long rank) {
			this.rank = rank;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}
}
