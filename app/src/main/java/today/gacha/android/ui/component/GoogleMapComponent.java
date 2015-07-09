package today.gacha.android.ui.component;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import today.gacha.android.R;
import today.gacha.android.domain.Restaurant;
import today.gacha.android.ui.MapsActivity;
import today.gacha.android.utils.LogUtils;

/**
 * @author Namhoon
 */
public class GoogleMapComponent extends MapsActivity {
	private static final String TAG = LogUtils.makeTag(GoogleMapComponent.class);

	// default position
	private static final LatLng NHN_NEXT = new LatLng(37.40208147037274d, 127.10891090333462);
	private static final float DEFAULT_ZOOM_LEVEL = 14f;

	private GoogleMap map;
    private Context context;

	public GoogleMapComponent(GoogleMap map, Context context) {
		this.map = map;
        this.context = context;

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
        Marker marker = map.addMarker(new MarkerOptions().position(currentLatLng).title("NOW").icon(BitmapDescriptorFactory.fromResource(R.mipmap.red_pins)));


        // location test Data
        LatLng test1 = new LatLng(location.getLatitude() - 0.005, location.getLongitude() - 0.005);
        Marker marker1 = map.addMarker(new MarkerOptions().position(test1).title("A").icon(BitmapDescriptorFactory.fromResource(R.mipmap.blue_pins)));

        LatLng test2 = new LatLng(location.getLatitude() + 0.004, location.getLongitude() + 0.004);
        Marker marker2 = map.addMarker(new MarkerOptions().position(test2).title("B").icon(BitmapDescriptorFactory.fromResource(R.mipmap.blue_pins)));

        LatLng test3 = new LatLng(location.getLatitude() - 0.02, location.getLongitude() + 0.03);
        Marker marker3 = map.addMarker(new MarkerOptions().position(test3).title("C").icon(BitmapDescriptorFactory.fromResource(R.mipmap.blue_pins)));

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
