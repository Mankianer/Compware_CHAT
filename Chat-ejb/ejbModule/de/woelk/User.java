package de.woelk;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import de.fh_dortmund.inf.cw.chat.server.entities.UserStatistic;

@Entity
@NamedQueries(value = { @NamedQuery(name = "allUser", query = "select u from User u"),
		@NamedQuery(name = "countUser", query = "select COUNT(u) from User u") })
public class User extends MyEntitySuperClass implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	String name;
	
	@Basic(optional=false)
	@Column(nullable = false)
	String password;
	

	public User() {
		super();
	}

	public User(String name, String password) {
		super();
		this.name = name;
		this.password = password;
	}
	
	@PrePersist
	@PreUpdate
	public void preUpdate() {
		updatedAt = new Date();
	}
}
