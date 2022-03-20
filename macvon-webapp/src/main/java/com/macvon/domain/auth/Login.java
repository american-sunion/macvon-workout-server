package com.macvon.domain.auth;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.macvon.domain.BaseDomain;
import com.macvon.utils.DateUtils;

/**
 * login audit info.
 * @author brianWu
 *
 */
@Entity
@Table(name="login")
public class Login extends BaseDomain {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8427115362531565980L;
	@Id
	@Column(name="ID")
	@GeneratedValue
	private long id;
	@Column(name="USER_ID")
	private long userId;
	@Column(name="STATUS")
	private String status;
	@Column(name="LOGIN_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date loginTime;
	@Column(name="LOGOUT_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	private Date logoutTime;
	@Column(name="RETRY")
	private int retry;
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="CREATED_DATE")
	public Date createdDate;	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="LAST_UPDATE_DATE")
	public Date lastUpdateDate;
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Date getLoginTime() {
		return loginTime;
	}
	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}
	public Date getLogoutTime() {
		return logoutTime;
	}
	public void setLogoutTime(Date logoutTime) {
		this.logoutTime = logoutTime;
	}
	public int getRetry() {
		return retry;
	}
	public void setRetry(int retry) {
		this.retry = retry;
	}
	public void addRetry() {
		this.retry++;
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
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public static Login createNewLogin(long userId, String status) {
		Login loginAttempt = new Login();
		loginAttempt.setLoginTime(DateUtils.getCurrentDate());
		loginAttempt.setRetry(1);
		loginAttempt.setStatus(status);
		loginAttempt.setUserId(userId);
		loginAttempt.setCreatedDate(loginAttempt.getLoginTime());
		loginAttempt.setLastUpdateDate(loginAttempt.getLoginTime());
		return loginAttempt;
	}
}