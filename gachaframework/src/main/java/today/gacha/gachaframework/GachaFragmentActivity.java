package today.gacha.gachaframework;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Namhoon
 */
public class GachaFragmentActivity extends FragmentActivity {
	private List<ActivityService> activityServices = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		for (ActivityService activityService : activityServices) {
			activityService.onPreCreating();
		}

		onCreating(savedInstanceState);

		for (ActivityService activityService : activityServices) {
			activityService.onPostCreating();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		for (ActivityService activityService : activityServices) {
			activityService.onPreStarting();
		}

		onStarting();

		for (ActivityService activityService : activityServices) {
			activityService.onPostStarting();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		for (ActivityService activityService : activityServices) {
			activityService.onPreRestarting();
		}

		onRestarting();

		for (ActivityService activityService : activityServices) {
			activityService.onPostRestarting();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		for (ActivityService activityService : activityServices) {
			activityService.onPreResuming();
		}

		onResuming();

		for (ActivityService activityService : activityServices) {
			activityService.onPostResuming();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		for (ActivityService activityService : activityServices) {
			activityService.onPrePausing();
		}

		onPausing();

		for (ActivityService activityService : activityServices) {
			activityService.onPostPausing();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		for (ActivityService activityService : activityServices) {
			activityService.onPreStopping();
		}

		onStopping();

		for (ActivityService activityService : activityServices) {
			activityService.onPostStopping();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		for (ActivityService activityService : activityServices) {
			activityService.onPreDestroying();
		}

		onDestroying();

		for (ActivityService activityService : activityServices) {
			activityService.onPostDestroying();
		}
	}

	protected void onCreating(Bundle savedInstanceState) {
	}

	protected void onStarting() {
	}

	protected void onRestarting() {
	}

	protected void onResuming() {
	}

	protected void onPausing() {
	}

	protected void onStopping() {
	}

	protected void onDestroying() {
	}

	public void registerActivityService(ActivityService activityService) {
		activityServices.add(activityService);
	}

	public void removeActivityService(ActivityService activityService) {
		activityServices.remove(activityService);
	}
}
