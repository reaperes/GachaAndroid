package today.gacha.android.utils;

/**
 * @author Namhoon
 */
public class ConnectionResultUtils {
	public static String toString(int statusCode) {
		switch (statusCode) {
			case 0:
				return "SUCCESS";
			case 1:
				return "SERVICE_MISSING";
			case 2:
				return "SERVICE_VERSION_UPDATE_REQUIRED";
			case 3:
				return "SERVICE_DISABLED";
			case 4:
				return "SIGN_IN_REQUIRED";
			case 5:
				return "INVALID_ACCOUNT";
			case 6:
				return "RESOLUTION_REQUIRED";
			case 7:
				return "NETWORK_ERROR";
			case 8:
				return "INTERNAL_ERROR";
			case 9:
				return "SERVICE_INVALID";
			case 10:
				return "DEVELOPER_ERROR";
			case 11:
				return "LICENSE_CHECK_FAILED";
			case 12:
			default:
				return "UNKNOWN_ERROR_CODE(" + statusCode + ")";
			case 13:
				return "CANCELED";
			case 14:
				return "TIMEOUT";
			case 15:
				return "INTERRUPTED";
			case 16:
				return "API_UNAVAILABLE";
			case 17:
				return "SIGN_IN_FAILED";
			case 18:
				return "SERVICE_UPDATING";
		}
	}
}
