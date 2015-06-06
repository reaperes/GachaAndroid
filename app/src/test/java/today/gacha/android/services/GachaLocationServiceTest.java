package today.gacha.android.services;

import android.location.Location;
import android.location.LocationManager;
import com.google.android.gms.common.api.GoogleApiClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import today.gacha.android.services.GachaLocationService.State;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static today.gacha.android.services.GachaLocationService.State.NotReady;
import static today.gacha.android.services.GachaLocationService.State.Ready;

/**
 * @author Namhoon
 */
@RunWith(MockitoJUnitRunner.class)
public class GachaLocationServiceTest {
	@Mock private LocationManager locationManager;
	@Mock private GoogleApiClient googleApiClient;
	@InjectMocks private GachaLocationService service;

	private CountDownLatch latch;

	@Test
	public void state__is_Ready_only__when_activity_life_cycle_is_Resume() {
		service.onActivityCreated();
		assertState(NotReady);

		service.onActivityStarted();
		assertState(NotReady);

		service.onActivityResumed();
		assertState(Ready);

		service.onActivityPaused();
		assertState(NotReady);

		service.onActivityStopped();
		assertState(NotReady);

		service.onActivityDestroyed();
		assertState(NotReady);
	}

	@Test
	public void requestCurrentLocation__should_fail__when_state_is_not_Ready()
			throws NoSuchFieldException, IllegalAccessException, InterruptedException {
		setState(NotReady);

		latch = new CountDownLatch(1);
		service.requestCurrentLocation(new GachaLocationService.LocationCallback() {
			@Override
			public void onCompleted(Location location, GachaLocationService.FailReason reason) {
				assertNull(location);
				assertNotNull(reason);
				latch.countDown();
			}
		});

		assertTrue(latch.await(500, TimeUnit.MILLISECONDS));
	}

	@Test
	public void requestCurrentLocation__should_fail__if_gps_is_not_enabled()
			throws NoSuchFieldException, IllegalAccessException, InterruptedException {
		Mockito.doReturn(false).when(locationManager).isProviderEnabled(any(String.class));
		setState(Ready);

		latch = new CountDownLatch(1);
		service.requestCurrentLocation(new GachaLocationService.LocationCallback() {
			@Override
			public void onCompleted(Location location, GachaLocationService.FailReason reason) {
				assertNull(location);
				assertNotNull(reason);
				latch.countDown();
			}
		});

		assertTrue(latch.await(500, TimeUnit.MILLISECONDS));
	}

	private void setState(State state) throws NoSuchFieldException, IllegalAccessException {
		Field fieldState = GachaLocationService.class.getDeclaredField("state");
		fieldState.setAccessible(true);
		fieldState.set(service, state);
	}

	private void assertState(State expect) {
		assertThat(service.getState(), is(expect));
	}
}
