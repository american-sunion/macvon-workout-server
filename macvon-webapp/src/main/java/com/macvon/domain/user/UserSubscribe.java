package com.macvon.domain.user;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.macvon.domain.BaseDomain;

@Entity
@Table(name = "user_subscribe")
public class UserSubscribe extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1850197021734153330L;
	@Id
	@Column(name="ID")
	private long id;
	@Column(name="USER_ID")
	private long userId;
	@Column(name="EMAIL")
	private String email;
	@Column(name="STATUS")
    private String status;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_DATE")
	public Date createdDate;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}	
	
}
