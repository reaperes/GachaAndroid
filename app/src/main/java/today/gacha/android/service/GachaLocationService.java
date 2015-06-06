package today.gacha.android.service;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static com.google.android.gms.common.api.GoogleApiClient.*;

/**
 * Location service class.
 *
 * @author Namhoon
 */
public class GachaLocationService {
	private static final String TAG = GachaLocationService.class.getSimpleName();

	static volatile GachaLocationService singleton = null;

	private final LocationManager locationManager;
	private final GoogleApiClient googleApiClient;

	private GachaLocationService(Context context) {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		googleApiClient = new Builder(context.getApplicationContext())
				.addApi(LocationServices.API)
				.build();
	}

	public static GachaLocationService getService(Context context) {
		if (singleton == null) {
			synchronized (GachaLocationService.class) {
				if (singleton == null) {
					singleton = new GachaLocationService(context);
				}
			}
		}
		return singleton;
	}

	public void getCurrentLocation(@NonNull final LocationCallback callback) {
		if (!isGpsEnable()) {
			FailReason reason = FailReason.REASON;
			reason.setMessage("GPS is not enabled.");
			callback.onCompleted(null, reason);
			return;
		}

		final ConnectionCallbacks connectionCallbacks = new AbstractConnectionCallbacks() {
			@Override
			public void onConnected(Bundle bundle) {
				Log.d(TAG, "Google api connection connected.");

				/**
				 * <a href="https://developer.android.com/training/location/receive-location-updates.html">Reference</a>
				 */
				LocationRequest request = new LocationRequest();
				request.setInterval(1000);
				request.setFastestInterval(1000);
				request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
				request.setExpirationDuration(10000);  // 10 seconds
				request.setNumUpdates(1);

				LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, request,
						new LocationListener() {
							@Override
							public void onLocationChanged(Location location) {
								callback.onCompleted(location, null);
							}
						});
			}
		};

		final OnConnectionFailedListener connectionFailedListener = new AbstractConnectionFailedListener() {
			@Override
			public void onConnectionFailed(ConnectionResult result) {
				Log.w(TAG, "Google api with LocationServices connection failed - " + result.toString());

				FailReason reason = FailReason.REASON;
				reason.setMessage(result.toString());
				callback.onCompleted(null, reason);
			}
		};

		googleApiClient.registerConnectionCallbacks(connectionCallbacks);
		googleApiClient.registerConnectionFailedListener(connectionFailedListener);
		googleApiClient.connect();
	}

	/**
	 * Get user's last location. Must use this method during start and stop activity life cycle.
	 */
	public void getLastLocation(@NonNull final LocationCallback callback) {
		final ConnectionCallbacks connectionCallbacks = new AbstractConnectionCallbacks() {
			@Override
			public void onConnected(Bundle bundle) {
				Log.d(TAG, "Google api connection connected.");

				callback.onCompleted(LocationServices.FusedLocationApi.getLastLocation(googleApiClient), null);
			}
		};

		final OnConnectionFailedListener connectionFailedListener = new AbstractConnectionFailedListener() {
			@Override
			public void onConnectionFailed(ConnectionResult result) {
				Log.w(TAG, "Google api with LocationServices connection failed - " + result.toString());

				FailReason reason = FailReason.REASON;
				reason.setMessage(result.toString());
				callback.onCompleted(null, reason);
			}
		};

		googleApiClient.registerConnectionCallbacks(connectionCallbacks);
		googleApiClient.registerConnectionFailedListener(connectionFailedListener);
		googleApiClient.connect();
	}

	public boolean isGpsEnable() {
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	public enum FailReason {
		REASON;

		String message;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

	public interface LocationCallback {
		/**
		 * @param location it may be null, if fails
		 * @param reason failed reason
		 */
		void onCompleted(Location location, FailReason reason);
	}

	private abstract class AbstractConnectionFailedListener implements OnConnectionFailedListener {
		@Override
		public void onConnectionFailed(ConnectionResult connectionResult) {
			Log.e(TAG, "Google api with LocationServices connection failed.");
		}
	}

	private abstract class AbstractConnectionCallbacks implements ConnectionCallbacks {
		@Override
		public void onConnected(Bundle bundle) {
			Log.d(TAG, "Google api with LocationServices connected - " + bundle.toString());
		}

		@Override
		public void onConnectionSuspended(int i) {
			Log.d(TAG, "Google api with LocationServices connection suspended - " + String.valueOf(i));
		}
	}
}
