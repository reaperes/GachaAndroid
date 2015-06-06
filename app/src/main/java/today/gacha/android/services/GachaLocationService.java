package today.gacha.android.services;

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
import lombok.Getter;
import today.gacha.android.core.ExtendedFragmentActivity;
import today.gacha.android.utils.LogUtils;

import static com.google.android.gms.common.api.GoogleApiClient.*;

/**
 * Location service class. Multi thread requests are not supported yet.
 *
 * @author Namhoon
 */
public class GachaLocationService implements GachaService, ExtendedFragmentActivity.OnActivityLifeCycleListener {
	private static final String TAG = LogUtils.makeTag(GachaLocationService.class);

	static volatile GachaLocationService singleton = null;

	private final LocationManager locationManager;
	private final GoogleApiClient googleApiClient;

	/**
	 * State's state diagram.
	 *
	 *               Connecting (when google api request)
	 *               ▼ ▲      ▼
	 *               ▼ ▲      ▼
	 * (onPause) NotReady ► ► Ready (onResume)
	 *                    ◄ ◄
	 */
	@Getter
	private State state = State.NotReady;

	GachaLocationService(LocationManager locationManager, GoogleApiClient googleApiClient) {
		this.locationManager = locationManager;
		this.googleApiClient = googleApiClient;
	}

	public static GachaLocationService getService(Context context) {
		if (singleton == null) {
			synchronized (GachaLocationService.class) {
				if (singleton == null) {
					LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
					GoogleApiClient googleApiClient = new Builder(context.getApplicationContext())
							.addApi(LocationServices.API)
							.build();
					singleton = new GachaLocationService(locationManager, googleApiClient);
				}
			}
		}
		return singleton;
	}

	public void requestCurrentLocation(@NonNull final LocationCallback callback) {
		if (state != State.Ready) {
			failRequest(callback, "Location service is not ready yet.");
			return;
		}

		if (!isGpsEnable()) {
			failRequest(callback, "GPS is not enabled.");
			return;
		}

		final ConnectionCallbacks connectionCallbacks = new AbstractConnectionCallbacks() {
			@Override
			public void onConnected(Bundle bundle) {
				Log.d(TAG, "Google api connection connected.");

				LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, createLocationRequest(),
						new LocationListener() {
							@Override
							public void onLocationChanged(Location location) {
								successRequest(location, callback);
							}
						});
			}
		};

		final OnConnectionFailedListener connectionFailedListener = new AbstractConnectionFailedListener() {
			@Override
			public void onConnectionFailed(ConnectionResult result) {
				Log.w(TAG, "Google api with LocationServices connection failed - " + result.toString());

				failRequest(callback, result.toString());
				state = State.Ready;
			}
		};

		connectWithCallbacks(connectionCallbacks, connectionFailedListener);
	}

	/**
	 * Get user's last location. Must use this method during start and stop activity life cycle.
	 */
	public void requestLastLocation(@NonNull final LocationCallback callback) {
		if (state != State.Ready) {
			failRequest(callback, "Location service is not ready yet.");
			return;
		}

		final ConnectionCallbacks connectionCallbacks = new AbstractConnectionCallbacks() {
			@Override
			public void onConnected(Bundle bundle) {
				Log.d(TAG, "Google api connection connected.");

				successRequest(LocationServices.FusedLocationApi.getLastLocation(googleApiClient), callback);
			}
		};

		final OnConnectionFailedListener connectionFailedListener = new AbstractConnectionFailedListener() {
			@Override
			public void onConnectionFailed(ConnectionResult result) {
				Log.w(TAG, "Google api with LocationServices connection failed - " + result.toString());

				failRequest(callback, result.toString());
				state = State.Ready;
			}
		};

		connectWithCallbacks(connectionCallbacks, connectionFailedListener);
	}

	public boolean isGpsEnable() {
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	@Override
	public void onActivityCreated() {
	}

	@Override
	public void onActivityStarted() {
	}

	@Override
	public void onActivityResumed() {
		if (state == State.NotReady) {
			state = State.Ready;
		}
	}

	@Override
	public void onActivityPaused() {
		state = State.NotReady;

		if (googleApiClient != null) {
			Log.d(TAG, "Google api client disconnected.");
			googleApiClient.disconnect();
		}
	}

	@Override
	public void onActivityStopped() {
	}

	@Override
	public void onActivityDestroyed() {
	}

	private void successRequest(Location location, @NonNull LocationCallback callback) {
		state = State.Ready;
		callback.onCompleted(location, null);
	}

	private void failRequest(@NonNull LocationCallback callback, String message) {
		FailReason reason = FailReason.DEFAULT;
		reason.setMessage(message);
		callback.onCompleted(null, reason);
	}

	private void connectWithCallbacks(ConnectionCallbacks connectionCallbacks,
			OnConnectionFailedListener connectionFailedListener) {
		state = State.Connecting;

		googleApiClient.registerConnectionCallbacks(connectionCallbacks);
		googleApiClient.registerConnectionFailedListener(connectionFailedListener);
		googleApiClient.connect();
	}

	private LocationRequest createLocationRequest() {
		/**
		 * <a href="https://developer.android.com/training/location/receive-location-updates.html">Reference</a>
		 */
		LocationRequest request = new LocationRequest();
		request.setInterval(1000);
		request.setFastestInterval(1000);
		request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
		request.setExpirationDuration(10000);  // 10 seconds
		request.setNumUpdates(1);
		return request;
	}

	public enum FailReason {
		DEFAULT;

		String message;

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
	}

	/**
	 * Service state. Google api can be used specific life cycle, between start and stop.
	 * State is {@link .State.Ready} or {@link .State.Connecting}, during start and stop.
	 * Others it is {@link .State.NotReady}. It may be {@link .State.Connecting} during google api request something.
	 */
	enum State {
		NotReady,
		Ready,
		Connecting;
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
