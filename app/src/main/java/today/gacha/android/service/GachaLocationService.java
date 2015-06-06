package today.gacha.android.service;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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

	private GoogleApiClient googleApiClient;

	private GachaLocationService(Context context) {
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

	/**
	 * Get user's last location. Must use this method during start and stop activity life cycle.
	 */
	public void getLastLocation(@NonNull final LastLocationCallback callback) {
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

	/**
	 * @see #getLastLocation(LastLocationCallback)
	 */
	public interface LastLocationCallback {
		/**
		 * Callback listener
		 *
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
