package today.gacha.android;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

/**
 * Activity step
 *
 * 1. Launch activity with default map position (NHN NEXT).
 * 2. Get user's last location.
 */
public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
	LocationListener {

	private static final String TAG = MapsActivity.class.getSimpleName();

	// default position
	private static final LatLng NHN_NEXT = new LatLng(37.40208147037274d, 127.10891090333462);
	private static final float DEFAULT_ZOOM_LEVEL = 14f;

	private GoogleMap googleMap;
	private GoogleApiClient googleApiClient;
	private LocationManager locationManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);

		setUpGoogleMap();
		setUpGoogleApiClient();
	}

	private void setUpLocationManager() {
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	protected void onResume() {
		super.onResume();

		googleApiClient.connect();
	}

	@Override
	protected void onPause() {
		super.onPause();

		googleApiClient.disconnect();
	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.d(TAG, "Google api service has connected.");
		validateGooglePlayServicesAvailable();
		Location lastLocation = getLastLocation();
		if (lastLocation != null) {
			animateGoogleMapCamera(lastLocation);
			return;
		}

		setUpLocationManager();
		if (!isGpsEnable()) {
			Toast.makeText(this, "GPS is not enabled. Please go on settings menu, and switch on GPS.",
				Toast.LENGTH_LONG).show();
			return ;
		}

		/**
		 * Location request reference
		 * <a href="https://developer.android.com/training/location/receive-location-updates.html">Reference</a>
		 */
		LocationRequest request = new LocationRequest();
		request.setInterval(1000);
		request.setFastestInterval(1000);
		request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		request.setExpirationDuration(10000);	// 10 seconds
		request.setNumUpdates(1);

		LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request, this);
	}

	@Override
	public void onLocationChanged(Location location) {
		animateGoogleMapCamera(location);
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.v(TAG, "Google api connection suspended.");
	}

	private void animateGoogleMapCamera(Location lastLocation) {
		CameraPosition cameraPosition = new CameraPosition.Builder()
			.target(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()))
			.zoom(DEFAULT_ZOOM_LEVEL)
			.build();

		Log.d(TAG, "Move google map camera to location - " + lastLocation.toString());
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	private boolean isGpsEnable() {
		boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		Log.e(TAG, "GPS : " + String.valueOf(gpsEnabled));

		return gpsEnabled;
	}

	private void validateGooglePlayServicesAvailable() {
		int state = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (state != ConnectionResult.SUCCESS) {
			crashAndReportConnectionResult(state);
		}
	}

	/**
	 * Get last location from google api
	 *
	 * @return Location if fails returns null.
	 */
	private Location getLastLocation() {
		return LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
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

	private void setUpGoogleApiClient() {
		googleApiClient = new GoogleApiClient.Builder(this)
			.addConnectionCallbacks(this)
			.addApi(LocationServices.API)
			.build();
	}

	private void crashAndReportConnectionResult(int state) {
		throw new AssertionError(
			"Google api service can not connected. Connection result state - "
				+ GooglePlayConnectionResult.find(state).name());
	}

	private enum GooglePlayConnectionResult {
		SUCCESS(0),
		SERVICE_MISSING(1),
		SERVICE_VERSION_UPDATE_REQUIRED(2),
		SERVICE_DISABLED(3),
		SIGN_IN_REQUIRED(4),
		INVALID_ACCOUNT(5),
		RESOLUTION_REQUIRED(6),
		NETWORK_ERROR(7),
		INTERNAL_ERROR(8),
		SERVICE_INVALID(9),
		DEVELOPER_ERROR(10),
		LICENSE_CHECK_FAILED(11),
		CANCELED(13),
		TIMEOUT(14),
		INTERRUPTED(15),
		API_UNAVAILABLE(16),
		SIGN_IN_FAILED(17),
		SERVICE_UPDATING(18),

		CAN_NOT_FIND_STATE_CODE(999);

		int state;

		GooglePlayConnectionResult(int state) {
			this.state = state;
		}

		private static GooglePlayConnectionResult find(int state) {
			for (GooglePlayConnectionResult element : values()) {
				if (element.state == state) {
					return element;
				}
			}
			return CAN_NOT_FIND_STATE_CODE;
		}
	}
}
