package de.woelk;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
public class MyEntitySuperClass {
	
	@Basic(optional=false)
	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date createAt;
	
	@Basic(optional=false)
	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	protected Date updatedAt;
	
	public MyEntitySuperClass() {
		createAt = new Date();
	}
}
