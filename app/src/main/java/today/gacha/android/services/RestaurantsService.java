package today.gacha.android.services;

import today.gacha.android.core.GachaEvent;
import today.gacha.android.domain.Restaurant;
import today.gacha.android.utils.LogUtils;

import java.util.Collection;

/**
 * @author Namhoon
 */
public class RestaurantsService {
	private static final String TAG = LogUtils.makeTag(RestaurantsService.class);

	public static class RestaurantsDataEvent extends GachaEvent<Collection<Restaurant>> {
		RestaurantsDataEvent(Throwable t) {
			super(t);
		}

		RestaurantsDataEvent(Collection<Restaurant> restaurants) {
			super(restaurants);
		}
	}
}
