package today.gacha.android.core;

import lombok.Getter;

/**
 * @author Namhoon
 */
public abstract class GachaEvent<T> {
	@Getter
	private final Throwable throwable;

	private final T data;

	@SuppressWarnings("unchecked")
	public GachaEvent(final Object data) {
		if (data == null) {
			throwable = new GachaEventException("Event data is null.");
			this.data = null;
		} else if (data instanceof Throwable) {
			throwable = (Throwable) data;
			this.data = null;
		} else {
			throwable = null;
			this.data = (T) data;
		}
	}

	public boolean isSuccess() {
		return throwable == null;
	}

	public String getThrowableMessage() {
		if (throwable == null)
			return "";
		return throwable.getMessage();
	}

	public T getData() {
		return data;
	}
}
