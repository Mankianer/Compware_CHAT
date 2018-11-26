package de.fh_dortmund.inf.cw.chat.server.entities;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

@Entity
@NamedQuery(name="getAllCommStats", query="select c from CommonStatistic c ORDER BY c.startingDate desc")
public class CommonStatistic extends Statistic {
	
	private static final long serialVersionUID = 1L;
	
	@Basic(optional=false)
	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date startingDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	
	public CommonStatistic() {
		super();
		startingDate = new Date();
	}
	
	@PrePersist
	@PreUpdate
	public void preUpdate() {
		updatedAt = new Date();
	}

	public Date getStartingDate() {
		return startingDate;
	}

	public void setStartingDate(Date startingDate) {
		this.startingDate = startingDate;
	}

	public Date getEndDate() {
		return endDate == null ? new Date() : endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	@Override
	public String toString() {
		return "\n\t\tAnmeldungen: " + getLogins() +
				"\n\t\tAbmeldungen: " + getLogouts() +
				"\n\t\tNachrichten: " + getMessages();
	}
}
