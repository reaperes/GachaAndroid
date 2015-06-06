package today.gacha.android;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import today.gacha.android.service.GachaLocationService;
import today.gacha.android.service.GachaLocationService.LastLocationCallback;

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

		locationService.getLastLocation(new LastLocationCallback() {
			@Override
			public void onCompleted(Location location, GachaLocationService.FailReason reason) {
				if (location == null) {
					Log.e(TAG, "Get last location failed - " + reason.getMessage());
					return;
				}

				animateGoogleMapCamera(location);
			}
		});
	}

	//	@Override
	//	public void onConnected(Bundle bundle) {
	//		setUpLocationManager();
	//		if (!isGpsEnable()) {
	//			Toast.makeText(this, "GPS is not enabled. Please go on settings menu, and switch on GPS.",
	//				Toast.LENGTH_LONG).show();
	//			return ;
	//		}
	//
	//		/**
	//		 * Location request reference
	//		 * <a href="https://developer.android.com/training/location/receive-location-updates.html">Reference</a>
	//		 */
	//		LocationRequest request = new LocationRequest();
	//		request.setInterval(1000);
	//		request.setFastestInterval(1000);
	//		request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
	//		request.setExpirationDuration(10000);	// 10 seconds
	//		request.setNumUpdates(1);
	//
	//		LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request, this);
	//	}
	//
	//	@Override
	//	public void onLocationChanged(Location location) {
	//		animateGoogleMapCamera(location);
	//	}
	//
	//
	//	private void setUpLocationManager() {
	//		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	//	}
	//
	private void animateGoogleMapCamera(Location lastLocation) {
		CameraPosition cameraPosition = new CameraPosition.Builder()
			.target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
			.zoom(DEFAULT_ZOOM_LEVEL)
			.build();

		Log.d(TAG, "Move google map camera to location - " + lastLocation.toString());
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	//	private boolean isGpsEnable() {
	//		boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	//		Log.e(TAG, "GPS : " + String.valueOf(gpsEnabled));
	//
	//		return gpsEnabled;
	//	}

	//	private void validateGooglePlayServicesAvailable() {
	//		int state = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
	//		if (state != ConnectionResult.SUCCESS) {
	//			crashAndReportConnectionResult(state);
	//		}
	//	}

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

	//	private void crashAndReportConnectionResult(int state) {
	//		throw new AssertionError(
	//			"Google api service can not connected. Connection result state - "
	//				+ GooglePlayConnectionResult.find(state).name());
	//	}
}
