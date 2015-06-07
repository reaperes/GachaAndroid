package today.gacha.android.services;

/**
 * @author Namhoon
 */
public class LocationServiceException  extends RuntimeException {
	LocationServiceException(String message) {
		this(message, null);
	}

	LocationServiceException(String message, Throwable t) {
		super(message, t);
	}
}
