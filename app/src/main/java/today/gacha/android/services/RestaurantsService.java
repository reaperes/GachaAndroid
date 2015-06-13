package today.gacha.android.services;

import android.content.Context;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.squareup.otto.Bus;
import lombok.Getter;
import lombok.Setter;
import today.gacha.android.GachaApplication;
import today.gacha.android.core.GachaEvent;
import today.gacha.android.core.GachaService;
import today.gacha.android.core.ServiceException;
import today.gacha.android.domain.Restaurant;
import today.gacha.android.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static today.gacha.android.services.RestApiService.Method.GET;

/**
 * @author Namhoon
 */
public class RestaurantsService implements GachaService {
	private static final String TAG = LogUtils.makeTag(RestaurantsService.class);

	private static volatile RestaurantsService singleton = null;

	private Bus bus;
	private RestApiService restApiService;

	private RestaurantsService(Bus bus, RestApiService restApiService) {
		this.bus = bus;
		this.restApiService = restApiService;
	}

	public static RestaurantsService getService(Context context) {
		if (singleton == null) {
			synchronized (RestApiService.class) {
				if (singleton == null) {
					Bus bus = ((GachaApplication) context.getApplicationContext()).getEventBus();
					RestApiService restApiService = RestApiService.getService(context);
					singleton = new RestaurantsService(bus, restApiService);
				}
			}
		}
		return singleton;
	}

	public void requestRestaurants(double latitude, double longitude, double radius) {
		String url = String.format("/restaurants?latitude=%s&longitude=%s&radius=%s", latitude, longitude, radius);
		restApiService.request(GET, url, ResponseRestaurantsDto.class,
				new Response.Listener<ResponseRestaurantsDto>() {
					@Override
					public void onResponse(ResponseRestaurantsDto response) {
						Collection<Restaurant> restaurants = response.toRestaurantList();
						bus.post(new RestaurantsDataEvent(restaurants));
					}
				},
				new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						bus.post(new RestaurantsDataEvent(new ServiceException("Request restaurants failed.")));
					}
				});
	}

	private class ResponseRestaurantsDto {
		@Getter @Setter
		private List<RestaurantDto> data;

		@Getter
		@Setter
		private class RestaurantDto {
			private String name;
			private double latitude;
			private double longitude;
			private int score;

			Restaurant toRestaurant() {
				return Restaurant.builder()
						.name(name)
						.latitude(latitude)
						.longitude(longitude)
						.score(score)
						.build();
			}
		}

		List<Restaurant> toRestaurantList() {
			List<Restaurant> restaurants = new ArrayList<>();
			for (RestaurantDto dto : data) {
				restaurants.add(dto.toRestaurant());
			}
			return restaurants;
		}
	}

	public static class RestaurantsDataEvent extends GachaEvent<Collection<Restaurant>> {
		RestaurantsDataEvent(Throwable t) {
			super(t);
		}

		RestaurantsDataEvent(Collection<Restaurant> restaurants) {
			super(restaurants);
		}
	}
}
