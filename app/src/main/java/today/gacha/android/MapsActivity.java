package today.gacha.android;

import android.location.Location;
import android.os.Bundle;
import today.gacha.android.service.LocationActivityService;
import today.gacha.android.service.MapActivityService;
import today.gacha.gachaframework.GachaFragmentActivity;

public class MapsActivity extends GachaFragmentActivity implements LocationActivityService.LocationChangedListener {
	private LocationActivityService locationActivityService;
	private MapActivityService mapActivityService;

	{
		locationActivityService = LocationActivityService.with(this).getService();
		mapActivityService = MapActivityService.with(this).mapFragmentId(R.id.map).getService();

		registerActivityService(locationActivityService);
		registerActivityService(mapActivityService);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_maps);
	}

	@Override
	protected void onStart() {
		super.onStart();
		locationActivityService.setLocationChangedListener(this);
	}

	@Override
	public void onLocationChanged(final Location location) {
		mapActivityService.animateCamera(location.getLatitude(), location.getLongitude(), 14f, 200);
	}
}
