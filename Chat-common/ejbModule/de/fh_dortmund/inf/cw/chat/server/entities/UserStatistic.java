package de.fh_dortmund.inf.cw.chat.server.entities;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@NamedQuery(name="findByName", query="select u from UserStatistic u where u.user = ?1")
public class UserStatistic extends Statistic {
	private static final long serialVersionUID = 1L;
	
	@Basic(optional=false)
	@Column(unique=true, nullable=false)
	private String user;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastLogin;
	
	public UserStatistic() {
		super();
	}
	
	@PrePersist
	@PreUpdate
	public void preUpdate() {
		updatedAt = new Date();
	}
	
	public UserStatistic(String user) {
		this.user = user;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}
}
