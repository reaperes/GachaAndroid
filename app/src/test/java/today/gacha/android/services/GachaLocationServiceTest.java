package today.gacha.android.services;

import android.location.LocationManager;
import com.google.android.gms.common.api.GoogleApiClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * @author Namhoon
 */
@RunWith(MockitoJUnitRunner.class)
public class GachaLocationServiceTest {
	@Mock private LocationManager locationManager;
	@Mock private GoogleApiClient googleApiClient;

	private GachaLocationService service;

	@Before
	public void before() {
		service = new GachaLocationService(locationManager, googleApiClient);
	}

	@Test
	public void state__is_Ready_only__when_activity_life_cycle_is_Resume() {
		Assert.assertNotNull(service);
	}
}
