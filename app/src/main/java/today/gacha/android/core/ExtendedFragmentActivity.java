package today.gacha.android.core;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import today.gacha.android.services.GachaService;
import today.gacha.android.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Namhoon
 */
public class ExtendedFragmentActivity extends FragmentActivity {
	private static final String TAG = LogUtils.makeTag(ExtendedFragmentActivity.class);

	private List<GachaService> services = new ArrayList<>(3);

	protected void addService(GachaService service) {
		if (services.contains(service)) {
			Log.w(TAG, "Added duplicated service instance");
		}
		services.add(service);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		for (GachaService service : services)
			if (service instanceof OnActivityCreateListener)
				((OnActivityCreateListener) service).onActivityCreated();
	}

	@Override
	protected void onStart() {
		super.onStart();
		for (GachaService service : services)
			if (service instanceof OnActivityStartListener)
				((OnActivityStartListener) service).onActivityStarted();
	}

	@Override
	protected void onResume() {
		super.onResume();
		for (GachaService service : services)
			if (service instanceof OnActivityResumeListener)
				((OnActivityResumeListener) service).onActivityResumed();
	}

	@Override
	protected void onPause() {
		super.onPause();
		for (GachaService service : services)
			if (service instanceof OnActivityPauseListener)
				((OnActivityPauseListener) service).onActivityPaused();
	}

	@Override
	protected void onStop() {
		super.onStop();
		for (GachaService service : services)
			if (service instanceof OnActivityStopListener)
				((OnActivityStopListener) service).onActivityStopped();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		for (GachaService service : services)
			if (service instanceof OnActivityDestroyedListener)
				((OnActivityDestroyedListener) service).onActivityDestroyed();
	}

	public interface OnActivityCreateListener {
		void onActivityCreated();
	}

	public interface OnActivityStartListener {
		void onActivityStarted();
	}

	public interface OnActivityResumeListener {
		void onActivityResumed();
	}

	public interface OnActivityPauseListener {
		void onActivityPaused();
	}

	public interface OnActivityStopListener {
		void onActivityStopped();
	}

	public interface OnActivityDestroyedListener {
		void onActivityDestroyed();
	}

	public interface OnActivityLifeCycleListener
			extends OnActivityCreateListener,
			OnActivityStartListener,
			OnActivityResumeListener,
			OnActivityStopListener,
			OnActivityDestroyedListener {
	}
}
