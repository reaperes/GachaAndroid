package today.gacha.android.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.squareup.otto.Subscribe;
import today.gacha.android.R;
import today.gacha.android.core.GachaFragmentActivity;
import today.gacha.android.domain.Restaurant;
import today.gacha.android.services.GachaLocationService;
import today.gacha.android.services.GachaLocationService.CurrentLocationEvent;
import today.gacha.android.services.GachaLocationService.LastLocationEvent;
import today.gacha.android.services.RestaurantsService.RestaurantsDataEvent;
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

	/**
	 * @see today.gacha.android.services.GachaLocationService
	 */
	@Subscribe
	public void onLastLocation(LastLocationEvent event) {
		if (event.isSuccess()) {
			mapComponent.animateToCamera(event.getData());
			return;
		}

		Log.d(TAG, "Get last location failed - " + event.getThrowableMessage());
		locationService.requestCurrentLocation();
	}

	/**
	 * @see today.gacha.android.services.GachaLocationService
	 */
	@Subscribe
	public void onCurrentLocation(CurrentLocationEvent event) {
		if (event.isSuccess()) {
			mapComponent.animateToCamera(event.getData());
			return;
		}

		Log.w(TAG, "Request current location failed - " + event.getThrowableMessage());
	}

	@Subscribe
	public void onRestaurantsDataReceived(RestaurantsDataEvent event) {
		if (event.isSuccess()) {
			for (Restaurant restaurant : event.getData()) {
				mapComponent.addMarker(restaurant);
			}
		}
	}

	/**************************************************
	 * For debugging
	 **************************************************/
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, Menu.NONE, "addMarkerTest");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case 0:
				Restaurant restaurant = Restaurant.builder()
						.latitude(37.40208147037274d)
						.longitude(127.10891090333462d)
						.name("NHN NEXT")
						.score(100)
						.build();

				mapComponent.addMarker(restaurant);
				break;
		}
		return false;
	}
}
