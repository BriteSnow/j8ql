/*
 * Copyright: 2014 Jeremy Chone
 * License: Apache V2 http://www.apache.org/licenses/LICENSE-2.0
 */

package org.j8ql.test.app;

import java.util.List;

// @Table("contact")
public class Contact{

    
    private Long id;
    private String name;
    private String firstName;
    private String lastName;
	private String title;
	private Mood mainMood;


	private List<Team> teams;

	public Contact() {
	}

	public Contact(Long id, String name) {
		this.id = id;
		this.name = name;
	}

	public Long getId() {
        return id;
    }

    public Contact setId(Long id) {
        this.id = id;
        return this;
    } 
    
    public String getName() {
        return name;
    }

    public Contact setName(String name) {
        this.name = name;
		return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

	public String getTitle() {
		return title;
	}

	public Contact setTitle(String title) {
		this.title = title;
		return this; // chainable setter, to make sure it works. 
	}

	public Mood getMainMood() {
		return mainMood;
	}

	public void setMainMood(Mood mainMood) {
		this.mainMood = mainMood;
	}

	public List<Team> getTeams() {
		return teams;
	}

	public void setTeams(List<Team> teams) {
		this.teams = teams;
	}
}
