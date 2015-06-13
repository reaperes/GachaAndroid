package today.gacha.android.core;

/**
 * @author Namhoon
 */
public class ServiceException extends RuntimeException {
	public ServiceException(String message) {
		this(message, null);
	}

	public ServiceException(String message, Throwable t) {
		super(message, t);
	}
}
