package today.gacha.android.services;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.otto.Bus;
import lombok.Getter;
import today.gacha.android.GachaApplication;
import today.gacha.android.core.event.GachaEvent;
import today.gacha.android.core.service.*;
import today.gacha.android.utils.LogUtils;

import static com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import static com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;

/**
 * Location service class. Multi thread requests are not supported yet.
 *
 * @author Namhoon
 */
public class GachaLocationService implements GachaService, OnActivityResumeListener, OnActivityPausedListener {
	private static final String TAG = LogUtils.makeTag(GachaLocationService.class);

	private static volatile GachaLocationService singleton = null;
	private final LocationManager locationManager;
	private final GoogleApiClient googleApiClient;
	private Bus bus;

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

	GachaLocationService(Bus bus, LocationManager locationManager, GoogleApiClient googleApiClient) {
		this.bus = bus;
		this.locationManager = locationManager;
		this.googleApiClient = googleApiClient;
	}

	public static GachaLocationService getService(Context context) {
		if (singleton == null) {
			synchronized (GachaLocationService.class) {
				if (singleton == null) {
					Bus bus = ((GachaApplication) context.getApplicationContext()).getEventBus();
					LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
					GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context.getApplicationContext())
							.addApi(LocationServices.API)
							.build();
					singleton = new GachaLocationService(bus, locationManager, googleApiClient);
				}
			}
		}
		return singleton;
	}

	public void requestCurrentLocation() {
		if (state != State.Ready) {
			bus.post(new CurrentLocationEvent(new ServiceException("Location service is not ready yet.")));
			return;
		}

		if (!isGpsEnable()) {
			bus.post(new CurrentLocationEvent(new ServiceException("GPS is not enabled.")));
			return;
		}

		state = State.Connecting;
		googleApiClient.registerConnectionCallbacks(currentLocationEventProducer);
		googleApiClient.registerConnectionFailedListener(connectionFailedEventProducer);
		googleApiClient.connect();
	}

	/**
	 * Get user's last location. Must use this method during start and stop activity life cycle.
	 */
	public void requestLastLocation() {
		if (state != State.Ready) {
			bus.post(new LastLocationEvent(new ServiceException("State is not ready")));
			return;
		}

		state = State.Connecting;
		googleApiClient.registerConnectionCallbacks(lastLocationEventProducer);
		googleApiClient.registerConnectionFailedListener(lastLocationFailedEventProducer);
		googleApiClient.connect();
	}

	public boolean isGpsEnable() {
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	@Override
	public void onActivityResumed() {
		bus.register(this);
		if (state == State.NotReady) {
			state = State.Ready;
		}
	}

	@Override
	public void onActivityPaused() {
		bus.unregister(this);
		state = State.NotReady;
		if (googleApiClient != null) {
			Log.d(TAG, "Google api client disconnected.");
			googleApiClient.disconnect();
		}
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

	private final OnConnectionFailedListener connectionFailedEventProducer = new OnConnectionFailedListener() {
		@Override
		public void onConnectionFailed(ConnectionResult result) {
			Log.w(TAG, "Google api with LocationServices connection failed - " + result.toString());
			state = State.Ready;
			bus.post(new CurrentLocationEvent(new ServiceException(result.toString())));
		}
	};

	private final AbstractConnectionCallbacks currentLocationEventProducer = new AbstractConnectionCallbacks() {
		@Override
		public void onConnected(Bundle bundle) {
			Log.d(TAG, "Google api connection connected.");
			LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, createLocationRequest(),
					new LocationListener() {
						@Override
						public void onLocationChanged(Location location) {
							state = State.Ready;
							bus.post(new CurrentLocationEvent(location));
						}
					});
		}
	};

	private ConnectionCallbacks lastLocationEventProducer = new AbstractConnectionCallbacks() {
		@Override
		public void onConnected(Bundle bundle) {
			Log.d(TAG, "Google api connection connected.");

			// If a location is not available, it can bu null,
			//
			// https://developers.google.com/android/reference/com/google/android/gms/location/
			// FusedLocationProviderApi#getLastLocation(com.google.android.gms.common.api.GoogleApiClient)
			Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

			state = State.Ready;
			if (lastLocation == null) {
				bus.post(new LastLocationEvent(new ServiceException("Getting last location is not available.")));
			} else {
				bus.post(new LastLocationEvent(lastLocation));
			}
		}
	};

	private OnConnectionFailedListener lastLocationFailedEventProducer = new OnConnectionFailedListener() {
		@Override
		public void onConnectionFailed(ConnectionResult result) {
			Log.w(TAG, "Google api with LocationServices connection failed - " + result.toString());

			state = State.Ready;
			bus.post(new LastLocationEvent(new ServiceException(result.toString())));
		}
	};

	/**
	 * Service state. Google api can be used specific life cycle, between start and stop.
	 * State is {@link .State.Ready} or {@link .State.Connecting}, during start and stop.
	 * Others it is {@link .State.NotReady}. It may be {@link .State.Connecting} during google api request something.
	 */
	enum State {
		NotReady,
		Ready,
		Connecting
	}

	/**
	 * Event bus data for current request location.
	 */
	public static class CurrentLocationEvent extends GachaEvent<Location> {
		CurrentLocationEvent(Throwable t) {
			super(t);
		}

		CurrentLocationEvent(Location location) {
			super(location);
		}
	}

	/**
	 * Event bus data for last request location.
	 */
	public static class LastLocationEvent extends GachaEvent<Location> {
		LastLocationEvent(Throwable t) {
			super(t);
		}
		LastLocationEvent(Location location) {
			super(location);
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
