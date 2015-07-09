package today.gacha.android.ui.component;

import android.location.Location;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import today.gacha.android.domain.Restaurant;
import today.gacha.android.utils.LogUtils;

/**
 * @author Namhoon
 */
public class GoogleMapComponent {
	private static final String TAG = LogUtils.makeTag(GoogleMapComponent.class);

	// default position
	private static final LatLng NHN_NEXT = new LatLng(37.40208147037274d, 127.10891090333462);
	private static final float DEFAULT_ZOOM_LEVEL = 14f;

	private GoogleMap map;

	public GoogleMapComponent(GoogleMap map) {
		this.map = map;

		setCameraToNHNNEXT();
		setLoggerOnClickEvent();
	}

	public void animateToCamera(Location location) {
		CameraPosition cameraPosition = new CameraPosition.Builder()
				.target(new LatLng(location.getLatitude(), location.getLongitude()))
				.zoom(DEFAULT_ZOOM_LEVEL)
				.build();

		Log.d(TAG, "Move google map camera to location - " + location.toString());
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	}

	public void addMarkerWithTitle(LatLng latLng, String title) {
		Marker marker = map.addMarker(new MarkerOptions()
				.position(latLng)
				.title(title));

		marker.showInfoWindow();
	}

    public void addMarkerOfCurrentLocation(Location location){
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        map.addMarker(new MarkerOptions().position(currentLatLng).title("NOW"));
    }

    // addMarker is Google Map Class Name. 'addMarker' rename 'addRestaurantLocationMarker'
	public void addRestaurantLocationMarker(Restaurant restaurant) {
		map.addMarker(new MarkerOptions().position(new LatLng(restaurant.getLatitude(), restaurant.getLongitude()))
				.title(restaurant.getName())
				.snippet("Score: " + String.valueOf(restaurant.getScore())));
	}

	public LatLng getDefaultLatLng() {
		return NHN_NEXT;
	}

	private void setCameraToNHNNEXT() {
		map.moveCamera(CameraUpdateFactory.newLatLngZoom(NHN_NEXT, DEFAULT_ZOOM_LEVEL));
	}

	private void setLoggerOnClickEvent() {
		map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			@Override
			public void onMapClick(LatLng latLng) {
				Log.e(TAG, "lat: " + String.valueOf(latLng.latitude));
				Log.e(TAG, "lng: " + String.valueOf(latLng.longitude));
//				addMarkerWithTitle(map.getProjection().getVisibleRegion().latLngBounds.northeast, "north east");
//				addMarkerWithTitle(map.getProjection().getVisibleRegion().latLngBounds.southwest, "south west");
			}
		});
	}
}
