package org.j8ql.test.app;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Ticket {
	public enum Type{
		bug,request;
	}
	// --------- Entity Properties --------- //
	private Long id;
	private Long projectId;
	private String subject;
	private Type type;
	private LocalDateTime dueDate;
	private String environment;
	private String description;
	private User creator;
	// --------- /Entity Properties --------- //

	private String projectName;
	private List<Label> labels;

	// --------- Entity Property Accessors --------- //
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Long getProjectId() {
		return projectId;
	}
	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}

	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}

	public LocalDateTime getDueDate() {
		return dueDate;
	}
	public void setDueDate(LocalDateTime dueDate) {
		this.dueDate = dueDate;
	}

	public String getEnvironment() {
		return environment;
	}
	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public User getCreator() {
		return creator;
	}
	public void setCreator(User creator) {
		this.creator = creator;
	}
	// --------- /Entity Property Accessors --------- //

	// --------- Join Property Accessor --------- //
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	// --------- /Join Property Accessor --------- //

	// --------- Label Accessors --------- //
	public Ticket addLabels(List<Label> labelsToAdd){
		labels = (labels != null) ? labels : new ArrayList<>();
		labels.addAll(labelsToAdd);
		return this;
	}
	public Ticket addLabel(Label label) {
		labels = (labels != null) ? labels : new ArrayList<>();
		labels.add(label);
		return this;
	}

	public List<Label> getLabels() {
		return labels;
	}

	public void setLabels(List<Label> labels) {
		this.labels = labels;
	}
	// --------- /Label Accessors --------- //

}
