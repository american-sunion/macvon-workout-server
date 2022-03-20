package com.macvon.domain.auth;
import java.util.Collection;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
/**
 * user auth info
 * @author brian wu
 *
 */
public class AuthUser  extends User {

    private static final long serialVersionUID = -6350093547533414423L;

    private AuthUserProfile authUserProfile;
    
    private BasicUserInfo userInfo;
    
    public AuthUser(String username, String password,
            Collection<? extends GrantedAuthority> authorities,
            HttpHeaders headers) {
        super(username, password, authorities);
    }
    public AuthUser(String username, Collection<? extends GrantedAuthority> authorities,AuthUserProfile authUserProfile, BasicUserInfo userInfo) {
        super(username, "", authorities);
        this.authUserProfile = authUserProfile;
        this.userInfo = userInfo;
    }

	public AuthUserProfile getAuthUserProfile() {
		return authUserProfile;
	}

	public void setAuthUserProfile(AuthUserProfile authUserProfile) {
		this.authUserProfile = authUserProfile;
	}
	public BasicUserInfo getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(BasicUserInfo userInfo) {
		this.userInfo = userInfo;
	}
    
}
