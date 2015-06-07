package today.gacha.android;

import android.app.Application;
import com.squareup.otto.Bus;

/**
 * @author Namhoon
 */
public class GachaApplication extends Application {
	private static final Bus bus = new Bus();

	public Bus getEventBus() {
		return bus;
	}
}
