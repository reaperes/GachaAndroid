package today.gacha.android;

import android.app.Dialog;
import android.location.Location;
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

public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
	GoogleApiClient.OnConnectionFailedListener, LocationListener {

	private static final String TAG = MapsActivity.class.getSimpleName();

	private GoogleApiClient mGoogleApiClient;
	private Location mLastLocation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_maps);

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
		Log.e(TAG, "onConnected");

		int state = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if (state == ConnectionResult.SUCCESS) {
			Log.e(TAG, "PlayService is SUCCESS");
		}

		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
		if (mLastLocation != null) {
			Log.e(TAG, "Get last location success");

			double latitude = mLastLocation.getLatitude();
			double longitude = mLastLocation.getLongitude();

			Log.e(TAG, "lat : " + String.valueOf(latitude));
			Log.e(TAG, "lng : " + String.valueOf(longitude));
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

	private synchronized void buildGoogleApiClient() {
		mGoogleApiClient = new GoogleApiClient.Builder(this)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.addApi(LocationServices.API)
			.build();
	}
}
