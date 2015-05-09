package today.gacha.android.service;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import today.gacha.gachaframework.AbstractActivityService;

/**
 * @author Namhoon
 */
public class LocationActivityService extends AbstractActivityService implements LocationListener {
	private Activity activity;
	private long updateMinTime;
	private float updateMinDistance;

	private LocationChangedListener locationChangedListener;

	public static LocationActivityServiceBuilder with(Activity activity) {
		return new LocationActivityServiceBuilder(activity);
	}

	private LocationActivityService(Activity activity, long updateMinTime, float updateMinDistance) {
		this.activity = activity;
		this.updateMinTime = updateMinTime;
		this.updateMinDistance = updateMinDistance;
	}

	public void setLocationChangedListener(LocationChangedListener listener) {
		locationChangedListener = listener;
	}

	// ------------------------------------------------------------------------
	// Abstract activity service
	// ------------------------------------------------------------------------

	@Override
	public void onCreate(Bundle savedInstanceState) {
		LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, updateMinTime, updateMinDistance, this);
	}

	// ------------------------------------------------------------------------
	// Location listener
	// ------------------------------------------------------------------------

	@Override
	public void onLocationChanged(Location location) {
		if (locationChangedListener != null) {
			locationChangedListener.onLocationChanged(location);
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	public interface LocationChangedListener {
		void onLocationChanged(final Location location);
	}

	public static class LocationActivityServiceBuilder {
		private Activity activity;
		private long updateMinTime = 0;
		private float updateMinDistance = 0;

		private LocationActivityServiceBuilder(Activity activity) {
			this.activity = activity;
		}

		private LocationActivityServiceBuilder updateMinTime(long minTime) {
			updateMinTime = minTime;
			return this;
		}

		private LocationActivityServiceBuilder updateMinDistance(float minDistance) {
			updateMinDistance = minDistance;
			return this;
		}

		public LocationActivityService getService() {
			return new LocationActivityService(activity, updateMinTime, updateMinDistance);
		}
	}
}
