package today.gacha.android.service;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import today.gacha.gachaframework.AbstractActivityService;

/**
 * @author Namhoon
 */
public class MapActivityService extends AbstractActivityService {
	private Activity activity;
	private int mapFragmentId;

	private GoogleMap map;

	public static MapActivityServiceBuilder with(Activity activity) {
		return new MapActivityServiceBuilder(activity);
	}

	private MapActivityService(Activity activity, int mapFragmentId) {
		this.activity = activity;
		this.mapFragmentId = mapFragmentId;
	}

	public void animateCamera(double latitude, double longitude, float zoom, int durationMs) {
		map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom), durationMs, new GoogleMap.CancelableCallback() {
			@Override
			public void onFinish() {
			}

			@Override
			public void onCancel() {
			}
		});
	}

	// ------------------------------------------------------------------------
	// Abstract activity service
	// ------------------------------------------------------------------------

	@Override
	public void onPostCreating() {
		setUpMapIfNeeded();
	}

	private void setUpMapIfNeeded() {
		if (map == null) {
			FragmentActivity fragmentActivity = (FragmentActivity) activity;
			SupportMapFragment fragment = (SupportMapFragment) fragmentActivity.getSupportFragmentManager().findFragmentById(mapFragmentId);
			map = fragment.getMap();
		}
	}

	public static class MapActivityServiceBuilder {
		private Activity activity;
		private int mapFragmentId;

		private MapActivityServiceBuilder(Activity activity) {
			this.activity = activity;
		}

		public MapActivityServiceBuilder mapFragmentId(int id) {
			mapFragmentId = id;
			return this;
		}

		public MapActivityService getService() {
			if (mapFragmentId == 0) {
				throw new IllegalStateException("MapFragmentId must be set.");
			}
			return new MapActivityService(activity, mapFragmentId);
		}
	}
}
