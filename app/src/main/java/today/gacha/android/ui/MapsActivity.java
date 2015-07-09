package today.gacha.android.ui;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.otto.Subscribe;
import today.gacha.android.R;
import today.gacha.android.core.activity.GachaFragmentActivity;
import today.gacha.android.domain.Restaurant;
import today.gacha.android.services.GachaLocationService;
import today.gacha.android.services.GachaLocationService.CurrentLocationEvent;
import today.gacha.android.services.GachaLocationService.LastLocationEvent;
import today.gacha.android.services.RestaurantsService;
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
public class MapsActivity extends GachaFragmentActivity  {

    //implements OnMapReadyCallback

	private static final String TAG = LogUtils.makeTag(MapsActivity.class);

	private GachaLocationService locationService;
	private RestaurantsService restaurantsService;
	private GoogleMapComponent mapComponent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		locationService = GachaLocationService.getService(this);
		restaurantsService = RestaurantsService.getService(this);
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
			Log.d(TAG, "Last location data received successfully - " + event.getData());
			Location location = event.getData();
			mapComponent.animateToCamera(location);
            mapComponent.addMarkerOfCorrentLocation(location);
			restaurantsService.requestRestaurants(location.getLatitude(), location.getLongitude(), 999d);
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
			Log.d(TAG, "Current location data received successfully - " + event.getData());
			Location location = event.getData();
			mapComponent.animateToCamera(location);
			restaurantsService.requestRestaurants(location.getLatitude(), location.getLongitude(), 999d);
			return;
		}

		Log.w(TAG, "Request current location failed - " + event.getThrowableMessage());
		LatLng defaultLatLng = mapComponent.getDefaultLatLng();
		restaurantsService.requestRestaurants(defaultLatLng.latitude, defaultLatLng.longitude, 999d);
	}

	@Subscribe
	public void onRestaurantsDataReceived(RestaurantsDataEvent event) {
		Log.d(TAG, "Restaurants data received - " + event.getData());
		if (event.isSuccess()) {
			for (Restaurant restaurant : event.getData()) {
				mapComponent.addRestaurantLocationMarker(restaurant);
			}
		}
	}
}
