package today.gacha.android.core;

import lombok.Getter;

/**
 * @author Namhoon
 */
public abstract class GachaEvent {
	@Getter
	private final Throwable throwable;

	public GachaEvent(final Throwable t) {
		throwable = t;
	}

	public boolean isSuccess() {
		return throwable == null;
	}

	public String getThrowableMessage() {
		if (throwable == null)
			return "";
		return throwable.getMessage();
	}
}
