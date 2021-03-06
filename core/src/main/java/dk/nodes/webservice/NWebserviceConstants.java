package dk.nodes.webservice;

import org.apache.http.HttpResponse;

import dk.nodes.utils.NLog;
import dk.nodes.webservice.models.NResponse;

/**
 * @author Casper Rasmussen 2012
 */
public class NWebserviceConstants {

	public static int TIME_OUT = 15*1000;
	public static boolean LAST_MODIFIED_ACTIVE = false;
	public static boolean COOKIES_CONTROLL_ACTIVE = false;	

	public static final int HTTP_DEFAULT_THREAD = 0;
	public static final int HTTP_FOREGROUND_THREAD = 1;
	public static final int HTTP_BACKGROUND_THREAD = 2;
	public static final int HTTP_SAFE_THREAD = 3;

	public static final int API_SUCCESS = 200;
	public static final int API_NOT_MODIFIED = 304;
	public static final int API_UNAUTHORIZED = 401;
	public static final int API_FORBIDDEN = 403;
	public static final int API_NOT_FOUND = 404;
	public static final int API_CONNECTION_ERROR = 997;
	public static final int API_RANDOM_ERROR = 999;
	public static final int API_PARSE_ERROR = 998;


	/**
	 * Checking if code is between 200 and 299, and return true
	 * @param code
	 * @return
	 */
	public static boolean isApiSuccess(int code){
		if(code>=200 && code <300)
			return true;
		else
			return false;
	}

	/**
	 * Checking if NResponse.getResponseCode is between 200 and 299 and returning true
	 * @param response
	 * @return boolean
	 */
	public static boolean isApiSuccess(NResponse response){
		if(response == null){
			NLog.e("isApiSuccess", "NResponse was null returning false");
			return false;
		}
		if(response.getResponseCode()>=200 && response.getResponseCode() <300)
			return true;
		else
			return false;
	}
	
	public static boolean isResponseCoudeServerRelated(int code){
		return (code>= 500 && code < 600) ? true:false;
	}
	public static boolean isResponseCoudeServerRelated(HttpResponse response){
		return isResponseCoudeServerRelated(response.getStatusLine().getStatusCode());
	}
	

	/**
	 * Will return Name of the inputted httpThread
	 * @param httpThread
	 * @return String
	 */
	public static String getHttpThreadAsString(int httpThread){
		if(httpThread==HTTP_DEFAULT_THREAD)
			return "HTTP_DEFAULT_THREAD";
		else if(httpThread==HTTP_FOREGROUND_THREAD)
			return "HTTP_FOREGROUND_THREAD";
		else if(httpThread==HTTP_BACKGROUND_THREAD)
			return "HTTP_BACKGROUND_THREAD";
		else if(httpThread==HTTP_SAFE_THREAD)
			return "HTTP_SAFE_THREAD";
		else{
			NLog.w("NWebserviceConstants getHttpThreadAsString","httpThread :"+httpThread+" was not found, returning null");
			return null;
		}
	}
}
