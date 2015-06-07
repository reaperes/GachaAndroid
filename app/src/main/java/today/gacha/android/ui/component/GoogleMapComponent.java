package today.gacha.android.ui.component;

import android.location.Location;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import today.gacha.android.utils.LogUtils;

/**
 * @author Namhoon
 */
public class GoogleMapComponent {
	private static final String TAG = LogUtils.makeTag(GoogleMapComponent.class);

	// default position
	private static final LatLng NHN_NEXT = new LatLng(37.40208147037274d, 127.10891090333462);
	private static final float DEFAULT_ZOOM_LEVEL = 14f;

	private GoogleMap googleMap;

	public GoogleMapComponent(GoogleMap googleMap) {
		this.googleMap = googleMap;

		setCameraToNHNNEXT();
		setLoggerOnClickEvent();
	}

	public void animateToCamera(Location location) {
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(location.getLatitude(), location.getLongitude()))
				.zoom(DEFAULT_ZOOM_LEVEL)
				.build();

		Log.d(TAG, "Move google map camera to location - " + location.toString());
		googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	private void setCameraToNHNNEXT() {
		googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(NHN_NEXT, DEFAULT_ZOOM_LEVEL));
	}

	private void setLoggerOnClickEvent() {
		googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				Log.e(TAG, "lat: " + String.valueOf(latLng.latitude));
				Log.e(TAG, "lng: " + String.valueOf(latLng.longitude));
			}
		});
	}
}
