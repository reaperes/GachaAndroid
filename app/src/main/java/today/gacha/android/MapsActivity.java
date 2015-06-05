package today.gacha.android;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
import com.urqa.clientinterface.URQAController;

/**
 * Activity step
 *
 * 1. Launch activity with default map position (NHN NEXT).
 * 2. Get user's last location.
 */
public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
	GoogleApiClient.OnConnectionFailedListener, LocationListener {

	private static final String TAG = MapsActivity.class.getSimpleName();

	// default position
	private static final LatLng NHN_NEXT = new LatLng(37.40208147037274d, 127.10891090333462);
	private static final float NHN_NEXT_ZOOM = 12f;

	private GoogleApiClient mGoogleApiClient;
	private Location mLastLocation;

	private GoogleMap googleMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		URQAController.InitializeAndStartSession(getApplicationContext(), "C3AC5133");

		setContentView(R.layout.activity_maps);

		setUpMapIfNeeded();
		buildGoogleApiClient();
	}

	@Override
	protected void onResume() {
		super.onResume();

		mGoogleApiClient.connect();
	}

	@Override
	protected void onPause() {
		super.onPause();

		mGoogleApiClient.disconnect();
	}

	@Override
	public void onConnected(Bundle bundle) {
		Log.d(TAG, "Google api service has connected.");

		int state = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (state != ConnectionResult.SUCCESS) {
			crashAndReportConnectionResult(state);
		}

		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if (mLastLocation != null) {
			Log.e(TAG, "Get last location success");

			double latitude = mLastLocation.getLatitude();
			double longitude = mLastLocation.getLongitude();

			CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(latitude, longitude))
				.zoom(14.5f).build();

			Log.e(TAG, "Move camera to lat : " + String.valueOf(latitude) + ", lng : " + String.valueOf(longitude));
			googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		} else {
			Log.e(TAG, "Request location update");

			LocationRequest mLocationRequest = LocationRequest.create()
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
				.setInterval(10 * 1000)        // 10 seconds, in milliseconds
				.setFastestInterval(1 * 1000); // 1 second, in milliseconds

			LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.e(TAG, "onLocationChanged");

		double latitude = location.getLatitude();
		double longitude = location.getLongitude();

		Log.e(TAG, "lat : " + String.valueOf(latitude));
		Log.e(TAG, "lng : " + String.valueOf(longitude));
	}

	@Override
	public void onConnectionSuspended(int i) {
		Log.e(TAG, "onConnectionSuspended");
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.e(TAG, "onConnectionFailed");
	}

	private void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.addApi(LocationServices.API)
			.build();
	}

	private void setUpMapIfNeeded() {
		if (googleMap == null) {
			googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NHN_NEXT, NHN_NEXT_ZOOM));
		}
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
