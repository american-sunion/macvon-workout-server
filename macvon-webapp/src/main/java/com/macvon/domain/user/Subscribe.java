package com.macvon.domain.user;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.macvon.domain.BaseDomain;
/**
 * user Subscribe
 * 
 * @author brian
 *
 */
@Entity
@Table(name="subscribe")
public class Subscribe  extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2581406434273923215L;
	@Id
	@Column(name="ID")
	private long id;
	@Column(name="EMAIL")
	private String email;	
	@Column(name="PROCESSED")
	private int processed;
	@Column(name="CREATED_DATE")
	public Date createdDate;	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATE_DATE")
	public Date lastUpdateDate;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getProcessed() {
		return processed;
	}
	public void setProcessed(int processed) {
		this.processed = processed;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	
	
}
