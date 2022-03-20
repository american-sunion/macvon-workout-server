package com.macvon.utils;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.security.access.AuthorizationServiceException;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
/**
 * Token util process cognito jwt token
 * @author brianwu
 *
 */

public class TokenUtils {
	private static final int SECS = 1000;
    private static final int HEADER = 0;
    private static final int PAYLOAD = 1;
    private static final int SIGNATURE = 2;
    private static final int JWT_PARTS = 3;
    private static final String BEARER_PREFIX = "Bearer ";
	/**
	 * Returns expiration of this id token.
	 *
	 * @return id token expiration claim as {@link java.util.Date} in UTC.
	 */
	public static Date getIdTokenExpiration(JWTClaimsSet claimsSet) {
		try {
			return getDateClaim(claimsSet, "exp");
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns "not before" claim of this id token
	 *
	 * @return not before claim as {@link java.util.Date} in UTC.
	 */
	public static Date getIdTokenNotBefore(JWTClaimsSet claimsSet) {
		try {
			return getDateClaim(claimsSet, "nbf");
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns "issued at" claim of this id token
	 *
	 * @return issue at claim as {@link java.util.Date} in UTC.
	 */
	public static Date getIdTokenIssuedAt(JWTClaimsSet claimsSet) {
		try {
			return getDateClaim(claimsSet, "iat");
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
	   /**
     * Returns expiration of this access token.
     *
     * @return access token expiration in UTC as java.util.Date object.
     */
    public static List<String> getAccessTokenGroup(JWTClaimsSet claimsSet) {
        try {
            return getClaimGroup(claimsSet);
        } catch (final Exception e) {
        	throw new RuntimeException(e);
        }
    }
	   /**
  * Returns expiration of this access token.
  *
  * @return access token expiration in UTC as java.util.Date object.
  */
 public static Date getAccessTokenExpiration(JWTClaimsSet claimsSet) {
     try {
         return getDateClaim(claimsSet, "exp");
     } catch (final Exception e) {
     	throw new RuntimeException(e);
     }
 }   
    /**
     * Returns the username set in the access token.
     * @return Username.
     */
    public static String getAccessTokenUsername(JWTClaimsSet claimsSet) throws Exception {
        return getClaim(claimsSet, "username");
    }
    /**
     * Returns if the access and id tokens have not expired.
     *
     * @return boolean to indicate if the access and id tokens have not expired.
     */
    public static boolean isValid(JWTClaimsSet idTokenClaimsSet, JWTClaimsSet accessTokenClaimsSet) {
        final Date currentTimeStamp = new Date();
        try {
            return (currentTimeStamp.before(getIdTokenExpiration(idTokenClaimsSet))
                    & currentTimeStamp.before(getAccessTokenExpiration(accessTokenClaimsSet)));
        } catch (final Exception e) {
            return false;
        }
    }
	@SuppressWarnings("unchecked")
	public static JWTClaimsSet getJWTClaimsSet(String idToken, @SuppressWarnings("rawtypes") ConfigurableJWTProcessor configurableJWTProcessor) {
		try {
			return configurableJWTProcessor.process(stripBearerToken(idToken), null);
		} catch (ParseException | BadJOSEException | JOSEException e) {
			e.printStackTrace();
		}
		throw new AuthorizationServiceException("Unauthorized");
	}
	private static String stripBearerToken(String token) {
		return token.startsWith(BEARER_PREFIX) ? token.substring(BEARER_PREFIX.length()) : token;
	}
    /**
     * Returns true if this session for the threshold set in refreshThreshold.
     *
     * @return boolean to indicate if the session is valid for at least refreshThreshold seconds.
     */
    public static boolean isValidForThreshold(JWTClaimsSet claimsSet, long refreshThreshold) {
        try {
            final long currentTime = System.currentTimeMillis() - 15 * SECS;
            final long expiresInMilliSeconds = getIdTokenExpiration(claimsSet).getTime() - currentTime;
            return (expiresInMilliSeconds > refreshThreshold);
        } catch (final Exception e) {
            return false;
        }
    }
    /**
     * Returns header for a JWT as a JSON object.
     *
     * @param jwt       REQUIRED: valid JSON Web Token as String.
     * @return header as a JSONObject.
     */
    public static JSONObject getHeader(String jwt) {
        try {
            validateJWT(jwt);
            final byte[] sectionDecoded = Base64.decodeBase64(jwt.split("\\.")[HEADER]);
            final String jwtSection = new String(sectionDecoded, "UTF-8");
            return new JSONObject(jwtSection);
        } catch (final UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage());
        } catch (final JSONException e) {
        	 throw new RuntimeException(e.getMessage());
        } catch (final Exception e) {
        	 throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Returns payload of a JWT as a JSON object.
     *
     * @param jwt       REQUIRED: valid JSON Web Token as String.
     * @return payload as a JSONObject.
     */
    public static JSONObject getPayload(String jwt) {
        try {
            validateJWT(jwt);
            final String payload = jwt.split("\\.")[PAYLOAD];
            String jwtSection = new String(Base64.decodeBase64(payload), "UTF-8");
            return new JSONObject(jwtSection);
        } catch (final UnsupportedEncodingException e) {
        	 throw new RuntimeException(e.getMessage());
        } catch (final JSONException e) {
        	 throw new RuntimeException(e.getMessage());
        } catch (final Exception e) {
        	 throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Returns signature of a JWT as a String.
     *
     * @param jwt       REQUIRED: valid JSON Web Token as String.
     * @return signature as a String.
     */
    public static String getSignature(String jwt) {
        try {
            validateJWT(jwt);
            final byte[] sectionDecoded = Base64.decodeBase64(jwt.split("\\.")[SIGNATURE]);
            return new String(sectionDecoded, "UTF-8");
        } catch (final Exception e) {
        	 throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Returns a claim, from the {@code JWT}s' payload, as a String.
     *
     * @param jwt       REQUIRED: valid JSON Web Token as String.
     * @param claim     REQUIRED: claim name as String.
     * @return  claim from the JWT as a String.
     */
	public  static String getClaim(JWTClaimsSet claimsSet, String claim) {
		try {
			final Object claimValue = claimsSet.getClaims().get(claim);

			if (claimValue != null) {
				return claimValue.toString();
			}

		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return null;
	}
    /**
     * Returns a claim, from the {@code JWT}s' payload, as a String.
     *
     * @param jwt       REQUIRED: valid JSON Web Token as String.
     * @param claim     REQUIRED: claim name as String.
     * @return  claim from the JWT as a String.
     */
	@SuppressWarnings("unchecked")
	public  static List<String> getClaimGroup(JWTClaimsSet claimsSet) {
		try {
			return (List<String>) claimsSet.getClaims().get("cognito:groups");
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}
    /**
     * Returns a claim, from the {@code JWT}s' payload, as a String.
     *
     * @param jwt       REQUIRED: valid JSON Web Token as String.
     * @param claim     REQUIRED: claim name as String.
     * @return  claim from the JWT as a String.
     */
	public  static Date getDateClaim(JWTClaimsSet claimsSet, String claim) {
		try {
			return claimsSet.getDateClaim(claim);
		} catch (final Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
    /**
     * Checks if {@code JWT} is a valid JSON Web Token.
     *
     * @param jwt REQUIRED: The JWT as a {@link String}.
     */
    public static void validateJWT(String jwt) {
        // Check if the the JWT has the three parts
        final String[] jwtParts = jwt.split("\\.");
        if (jwtParts.length != JWT_PARTS) {
        	 throw new RuntimeException("not a JSON Web Token");
        }
    }    
}