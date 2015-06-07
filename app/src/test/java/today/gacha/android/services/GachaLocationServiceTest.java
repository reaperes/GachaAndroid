package today.gacha.android.services;

import android.location.Location;
import android.location.LocationManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.otto.ThreadEnforcer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import today.gacha.android.services.GachaLocationService.CurrentLocationEvent;
import today.gacha.android.services.GachaLocationService.LastLocationEvent;
import today.gacha.android.services.GachaLocationService.State;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static today.gacha.android.services.GachaLocationService.State.NotReady;
import static today.gacha.android.services.GachaLocationService.State.Ready;

/**
 * @author Namhoon
 */
@RunWith(MockitoJUnitRunner.class)
public class GachaLocationServiceTest {
	private CountDownLatch latch;
	private Bus bus;

	private LocationManager locationManager;
	private GachaLocationService service;
	private GoogleApiClient googleApiClient;

	@Before
	public void before() throws InterruptedException {
		latch = new CountDownLatch(1);

		bus = new Bus(ThreadEnforcer.ANY);

		locationManager = mock(LocationManager.class);
		googleApiClient = mock(GoogleApiClient.class);
		service = new GachaLocationService(bus, locationManager, googleApiClient);

		bus.register(service);
	}

	@Test
	public void state__is_Ready_only__when_activity_life_cycle_is_Resume() {
		service.onActivityResumed();
		assertState(Ready);

		service.onActivityPaused();
		assertState(NotReady);
	}

	@Test
	public void requestCurrentLocation__should_success__with_conditions() {
		setState(Ready);
		setGpsEnabled(true);
		doAnswer(produceDummyCurrentLocation).when(googleApiClient).connect();

		bus.register(new Object() {
			@Subscribe
			public void subscribe(CurrentLocationEvent event) {
				assertTrue(event.isSuccess());
				assertNotNull(event.getData());
				latch.countDown();
			}
		});

		service.requestCurrentLocation();
		assertLatchDownToZero();
	}

	@Test
	public void requestCurrentLocation__should_fail__when_state_is_not_Ready() {
		setState(NotReady);

		bus.register(new Object() {
			@Subscribe
			public void subscribe(CurrentLocationEvent event) {
				assertFalse(event.isSuccess());
				assertNull(event.getData());
				latch.countDown();
			}
		});
		service.requestCurrentLocation();

		assertLatchDownToZero();
	}

	@Test
	public void requestCurrentLocation__should_fail__if_gps_is_not_enabled() {
		setGpsEnabled(false);
		setState(Ready);

		bus.register(new Object() {
			@Subscribe
			public void subscribe(CurrentLocationEvent event) {
				assertFalse(event.isSuccess());
				assertNull(event.getData());
				latch.countDown();
			}
		});
		service.requestCurrentLocation();

		assertLatchDownToZero();
	}

	@Test
	public void requestLastLocation__should_success__with_conditions() {
		setState(Ready);
		doAnswer(produceDummyLastLocation).when(googleApiClient).connect();

		bus.register(new Object() {
			@Subscribe
			public void subscribe(LastLocationEvent event) {
				assertTrue(event.isSuccess());
				assertNotNull(event.getData());
				latch.countDown();
			}
		});

		service.requestLastLocation();
		assertLatchDownToZero();
	}

	@Test
	public void requestLastLocation__should_fail__when_state_is_not_ready() {
		setState(NotReady);

		bus.register(new Object() {
			@Subscribe
			public void subscribe(LastLocationEvent event) {
				assertFalse(event.isSuccess());
				assertNull(event.getData());
				latch.countDown();
			}
		});

		service.requestLastLocation();
		assertLatchDownToZero();
	}

	private Answer<Void> produceDummyCurrentLocation = new Answer<Void>() {
		@Override
		public Void answer(InvocationOnMock invocation) throws Throwable {
			Location dummyLocation = mock(Location.class);
			bus.post(new CurrentLocationEvent(dummyLocation));
			return null;
		}
	};

	private Answer<Void> produceDummyLastLocation = new Answer<Void>() {
		@Override
		public Void answer(InvocationOnMock invocation) throws Throwable {
			Location dummyLocation = mock(Location.class);
			bus.post(new LastLocationEvent(dummyLocation));
			return null;
		}
	};

	private void setGpsEnabled(boolean enable) {
		doReturn(enable).when(locationManager).isProviderEnabled(any(String.class));
	}

	private void setState(State state) {
		try {
			Field fieldState = GachaLocationService.class.getDeclaredField("state");
			fieldState.setAccessible(true);
			fieldState.set(service, state);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void assertState(State expect) {
		assertThat(service.getState(), is(expect));
	}

	private void assertLatchDownToZero() {
		try {
			assertThat(latch.await(200, TimeUnit.MILLISECONDS), is(true));
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
}
