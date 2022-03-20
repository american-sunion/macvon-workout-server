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
 * user membership
 * 
 * @author brian
 *
 */
@Entity
@Table(name="user_membership")
public class UserMembership extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2752558758106429883L;
	@Id
	@Column(name="ID")
	private long id;
	@Column(name="MEMBERSHIP_ID")
	private long membershipId;
	@Column(name="USER_ID")
	private long userId;
	@Column(name="PRIMARY_USER_ID")
	private long primaryUserId;
	@Column(name="IS_PRIMARY")
    private String isPrimary;  
	@Column(name="STATUS")
    private String status;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EFFECTIVE_DATE")
	private Date effectiveDate;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="EXPIRATION_DATE")
	private Date expirationDate;
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
	public long getMembershipId() {
		return membershipId;
	}
	public void setMembershipId(long membershipId) {
		this.membershipId = membershipId;
	}
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getIsPrimary() {
		return isPrimary;
	}
	public void setIsPrimary(String isPrimary) {
		this.isPrimary = isPrimary;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getEffectiveDate() {
		return effectiveDate;
	}
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}
	public Date getExpirationDate() {
		return expirationDate;
	}
	public void setExpirationDate(Date expirationDate) {
		this.expirationDate = expirationDate;
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
	public long getPrimaryUserId() {
		return primaryUserId;
	}
	public void setPrimaryUserId(long primaryUserId) {
		this.primaryUserId = primaryUserId;
	}
	
}
