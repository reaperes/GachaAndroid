package today.gacha.android.core;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import today.gacha.android.service.GachaService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Namhoon
 */
public class ExtendedFragmentActivity extends FragmentActivity {
	private static final String TAG = ExtendedFragmentActivity.class.getSimpleName();

	private List<GachaService> services = new ArrayList<>(3);

	protected void addService(GachaService service) {
		if (services.contains(service)) {
			Log.w(TAG, "Added duplicated service instance");
		}
		services.add(service);
	}

	@Override
	protected void onPause() {
		super.onPause();

		for (GachaService service : services) {
			if (service instanceof OnActivityPauseListener) {
				((OnActivityPauseListener) service).onActivityPaused();
			}
		}
	}

	public interface OnActivityPauseListener {
		void onActivityPaused();
	}
}
