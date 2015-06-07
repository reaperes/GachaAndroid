package today.gacha.android.ui;

import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.squareup.otto.Subscribe;
import today.gacha.android.R;
import today.gacha.android.core.GachaFragmentActivity;
import today.gacha.android.services.GachaLocationService;
import today.gacha.android.services.GachaLocationService.CurrentLocationEvent;
import today.gacha.android.services.GachaLocationService.LastLocationEvent;
import today.gacha.android.ui.component.GoogleMapComponent;
import today.gacha.android.utils.LogUtils;

/**
 * Activity step
 *
 * 1. Launch activity with default map position (NHN NEXT).
 * 2. Get user's last location.
 * 3. Get user's current location.
 */
public class MapsActivity extends GachaFragmentActivity {

	private static final String TAG = LogUtils.makeTag(MapsActivity.class);

	private GachaLocationService locationService;
	private GoogleMapComponent mapComponent;

	private GoogleMap googleMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		locationService = GachaLocationService.getService(this);
		addActivityLifeCycleListener(locationService);

		GoogleMap googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		mapComponent = new GoogleMapComponent(googleMap);
	}

	@Override
	protected void onResume() {
		super.onResume();

		locationService.requestLastLocation();
	}

	@Subscribe
	public void onLastLocation(LastLocationEvent event) {
		if (event.isSuccess()) {
			mapComponent.animateToCamera(event.getData());
			return;
		}

		Log.d(TAG, "Get last location failed - " + event.getThrowableMessage());
		locationService.requestCurrentLocation();
	}

	@Subscribe
	public void onCurrentLocation(CurrentLocationEvent event) {
		if (event.isSuccess()) {
			mapComponent.animateToCamera(event.getData());
			return;
		}

		Log.w(TAG, "Request current location failed - " + event.getThrowableMessage());
	}
}
