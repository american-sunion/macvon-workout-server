package com.macvon.domain.user;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.macvon.domain.BaseDomain;
import com.macvon.utils.MoneySerializer;

/**
 * user membership
 * 
 * @author brian
 *
 */
@Entity
@Table(name="membership")
public class Membership  extends BaseDomain {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5011880951112240123L;

	@Id
	@Column(name="ID")
	private long id;
	@Column(name="membership_type")
	private String membershipType;	
	@Column(name="membership_desc")
	private String membershipDesc;	
	@Column(name="membership_price")
	@JsonSerialize(using = MoneySerializer.class)
	private BigDecimal price;
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
	public String getMembershipType() {
		return membershipType;
	}
	public void setMembershipType(String membershipType) {
		this.membershipType = membershipType;
	}
	public String getMembershipDesc() {
		return membershipDesc;
	}
	public void setMembershipDesc(String membershipDesc) {
		this.membershipDesc = membershipDesc;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
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
