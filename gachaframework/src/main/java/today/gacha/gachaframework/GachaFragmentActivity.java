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
			activityService.onCreate(savedInstanceState);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		for (ActivityService activityService : activityServices) {
			activityService.onStart();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		for (ActivityService activityService : activityServices) {
			activityService.onRestart();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		for (ActivityService activityService : activityServices) {
			activityService.onResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		for (ActivityService activityService : activityServices) {
			activityService.onPause();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		for (ActivityService activityService : activityServices) {
			activityService.onStop();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		for (ActivityService activityService : activityServices) {
			activityService.onDestroy();
		}
	}

	public void registerActivityService(ActivityService activityService) {
		activityServices.add(activityService);
	}

	public void removeActivityService(ActivityService activityService) {
		activityServices.remove(activityService);
	}
}
