package today.gacha.android.ui;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import today.gacha.android.R;
import today.gacha.android.service.GachaLocationService;
import today.gacha.android.service.GachaLocationService.FailReason;
import today.gacha.android.service.GachaLocationService.LocationCallback;

/**
 * Activity step
 *
 * 1. Launch activity with default map position (NHN NEXT).
 * 2. Get user's last location.
 */
public class MapsActivity extends FragmentActivity {

	private static final String TAG = MapsActivity.class.getSimpleName();

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

		locationService.getLastLocation(new LocationCallback() {
			@Override
			public void onCompleted(Location location, FailReason reason) {
				if (location != null) {
					animateGoogleMapCamera(location);
					return ;
				}
				Log.d(TAG, "Get last location failed - " + reason.getMessage());

				locationService.getCurrentLocation(new LocationCallback() {
					@Override
					public void onCompleted(Location location, FailReason reason) {
						if (location != null) {
							animateGoogleMapCamera(location);
							return ;
						}
						Log.w(TAG, "Requet current location failed - " + reason.getMessage());
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
