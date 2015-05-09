package today.gacha.gachaframework;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Namhoon
 */
public class GachaFragmentActivity extends FragmentActivity {
	private List<Service> services = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		for (Service service : services) {
			service.onCreate(savedInstanceState);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		for (Service service : services) {
			service.onStart();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		for (Service service : services) {
			service.onRestart();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		for (Service service : services) {
			service.onResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		for (Service service : services) {
			service.onPause();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		for (Service service : services) {
			service.onStop();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		for (Service service : services) {
			service.onDestroy();
		}
	}

	public void registerService(Service service) {
		services.add(service);
	}

	public void removeService(Service service) {
		services.remove(service);
	}
}
