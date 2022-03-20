package com.macvon.utils;

public class GlobalConstants {
	public static class WEB {
		public static final String LOGIN = "login_url";
		public static final String ACCESS_TOKEN = "accessToken";
		public static final String REFRESH_TOKEN = "refreshToken";
		public static final String ID_TOKEN = "idToken";
		public static final String ENTITLE = "entitle";
		public static final String DISPLAY_NMAE = "displayName";
	}

	public static class EXCEPTION {
		public static final String SYSTEM_ERROR = "We are unable to process your request. Please retry later or contact our support representative";
		public static final String AUTH_DENY_ERROR = "Access is denied.";
		public static final String DATA_ACCESS_ERROR = "Data issues occurred while processing the request";
		public static final String DATA_NOT_FOUND_ERROR = "Data not found";
		public static final String MISS_REQUIRED_PARAMS = "Required parameter are not present.";
		public static final String PASSWORD_NO_MATCHED = "password not match";
		public static final String INVALID_LOGIN = "Invalid Login, please try again.";
		public static final String INVALID_LOGIN_ACCT = "Invalid Login, please check your user name and password.";
		public static final String DUPLICATE_KEY = "User already exist.";
		public static final String TOO_MANY_LOGIN_ATTEMPTS = "Too many failed log in attempts. Please wait and try again.";
	}
	public static enum STATUS {
		CREATED, INIT, BANKINFO, DOCSIGN, ERESET, PARTIAL_DOCUMENT, WAITING, PENDING, REVIEW, VERIFIED, CANCELLED, SUBMITTED, REMOVED, APPROVED, APPROVER_PROCESSED, PROCESSED, APPROVER_SUBMITTED, APPROVED_FINAL, ADJUSTED, COMPLETE, IN_PROGRESS, REJECTED, READY, ONHOLD, EXPIRED, LOCKED, FAILED, ACTIVED, SIGNEDIN, LOGOUT
	}
	public static class MAIL {
		public final static String MAIL_SMTP_HOST="email.smtp.host";
		public final static String MAIL_ENABLE="email.enable";
		public final static String MAIL_STARTTLS_ENABLE="email.starttls.enable";
		public final static String MAIL_SMTP_USER="email.smtp.user";
		public final static String MAIL_SMTP_PWD="email.smtp.password";
		public final static String MAIL_SMTP_PORT="email.smtp.port";
		public final static String MAIL_SMTP_AUTH="email.smtp.auth";
		public final static String MAIL_FROM="email.from";
		public final static String MAIL_CC="email.cc";
		public final static String MAIL_WELCOME_SUBJECT="email.welcome.subject";
		public final static String MAIL_NEW_USER_TEMPLATE="email.template.newuser";
		
		public final static String MAIL_DIGEST="email.digest";
	}
	public static class XML {
		public static class NODE {
			/** Configuration file XML root Node */
			public static final String ROOT_SQL_MAP_CONFIG = "sqlMap";

			/** Configuration file XML first level Node */
			public static final String DAO = "dao";
			/** Configuration file XML first level Node */
			public static final String QUERY = "query";
		}

		public static class ATTRIB {
			/** Configuration file XML Attribute */
			public static final String CLASS_NAME = "className";
			/** Configuration file XML Attribute */
			public static final String NAME = "name";
			/** Configuration file XML Attribute */
			public static final String ID = "id";
			public static final String MAPPED_ENTITY="mappedToEntity";
		}
	}
}
