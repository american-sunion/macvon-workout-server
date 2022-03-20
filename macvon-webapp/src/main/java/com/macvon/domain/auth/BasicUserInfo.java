package com.macvon.domain.auth;


import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author bwu
 */
public class BasicUserInfo implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 3163842059433115589L;
	private String nickname;
    private String email;
    private String userId;
    private String userName;
    private String lastName;
    private String firstName;
    private String status;
    private String phoneNumber;
    private Set<String> roles = new HashSet<>();

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }


    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName the lastName to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return the firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName the firstName to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public Set<String> getRoles() {
        return roles;
    }
        
    public void addRole(String role) {
        this.roles.add(role.toUpperCase());
    }

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getDisplayName() {
		if(userName.contains("@")) {
			if(getNickname()!=null) {
				return getNickname();
			} else {
				return getFirstName();
			}
		} else {
			return userName;
		}
	}
    
}
