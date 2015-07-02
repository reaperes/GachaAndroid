package today.gacha.android.core.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.squareup.otto.Bus;
import today.gacha.android.GachaApplication;
import today.gacha.android.core.service.*;
import today.gacha.android.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Namhoon
 */
public class GachaFragmentActivity extends FragmentActivity {
	private static final String TAG = LogUtils.makeTag(GachaFragmentActivity.class);

	private Bus bus;

	private List<ActivityLifeCycleListener> lifeCycleListeners = new ArrayList<>(3);

	protected void addActivityLifeCycleListener(ActivityLifeCycleListener lifeCycleListener) {
		if (lifeCycleListeners.contains(lifeCycleListener)) {
			Log.w(TAG, "Added duplicated lifeCycleListener instance");
		}
		lifeCycleListeners.add(lifeCycleListener);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bus = ((GachaApplication) getApplicationContext()).getEventBus();
		for (ActivityLifeCycleListener listener : lifeCycleListeners)
			if (listener instanceof OnActivityCreateListener)
				((OnActivityCreateListener) listener).onActivityCreated();
	}

	@Override
	protected void onStart() {
		super.onStart();
		bus.register(this);
		for (ActivityLifeCycleListener listener : lifeCycleListeners)
			if (listener instanceof OnActivityStartListener)
				((OnActivityStartListener) listener).onActivityStarted();
	}

	@Override
	protected void onResume() {
		super.onResume();
		for (ActivityLifeCycleListener listener : lifeCycleListeners)
			if (listener instanceof OnActivityResumeListener)
				((OnActivityResumeListener) listener).onActivityResumed();
	}

	@Override
	protected void onPause() {
		super.onPause();
		for (ActivityLifeCycleListener listener : lifeCycleListeners)
			if (listener instanceof OnActivityPausedListener)
				((OnActivityPausedListener) listener).onActivityPaused();
	}

	@Override
	protected void onStop() {
		super.onStop();
		bus.unregister(this);
		for (ActivityLifeCycleListener listener : lifeCycleListeners)
			if (listener instanceof OnActivityStopListener)
				((OnActivityStopListener) listener).onActivityStopped();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		for (ActivityLifeCycleListener listener : lifeCycleListeners)
			if (listener instanceof OnActivityDestroyedListener)
				((OnActivityDestroyedListener) listener).onActivityDestroyed();
	}
}
