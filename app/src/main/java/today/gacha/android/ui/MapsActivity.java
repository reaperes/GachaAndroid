package today.gacha.android.ui;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import today.gacha.android.core.ExtendedFragmentActivity;
import today.gacha.android.R;
import today.gacha.android.services.GachaLocationService;
import today.gacha.android.services.GachaLocationService.FailReason;
import today.gacha.android.services.GachaLocationService.LocationCallback;
import today.gacha.android.utils.LogUtils;

/**
 * Activity step
 *
 * 1. Launch activity with default map position (NHN NEXT).
 * 2. Get user's last location.
 * 3. Get user's current location.
 */
public class MapsActivity extends ExtendedFragmentActivity {

	private static final String TAG = LogUtils.makeTag(MapsActivity.class);

	// default position
	private static final LatLng NHN_NEXT = new LatLng(37.40208147037274d, 127.10891090333462);
	private static final float DEFAULT_ZOOM_LEVEL = 14f;

	private GachaLocationService locationService;

	private GoogleMap googleMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		locationService = GachaLocationService.getService(this);

		setUpGoogleMap();
	}

	@Override
	protected void onResume() {
		super.onResume();

		locationService.requestLastLocation(new LocationCallback() {
			@Override
			public void onCompleted(Location location, FailReason reason) {
				if (location != null) {
					animateGoogleMapCamera(location);
					return;
				}
				Log.d(TAG, "Get last location failed - " + reason.getMessage());

				locationService.requestCurrentLocation(new LocationCallback() {
					@Override
					public void onCompleted(Location location, FailReason reason) {
						if (location != null) {
							animateGoogleMapCamera(location);
							return;
						}
						Log.w(TAG, "Request current location failed - " + reason.getMessage());
					}
				});
			}
		});
	}

	private void animateGoogleMapCamera(Location lastLocation) {
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
				.zoom(DEFAULT_ZOOM_LEVEL)
				.build();

		Log.d(TAG, "Move google map camera to location - " + lastLocation.toString());
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	private void setUpGoogleMap() {
		if (googleMap == null) {
			googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NHN_NEXT, DEFAULT_ZOOM_LEVEL));
			googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
				@Override
				public void onMapClick(LatLng latLng) {
					Log.e(TAG, "lat: " + String.valueOf(latLng.latitude));
					Log.e(TAG, "lng: " + String.valueOf(latLng.longitude));
				}
			});
		}
	}
}
