package today.gacha.android.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

/**
 * @author Namhoon
 */
@Value
@Builder
@EqualsAndHashCode(of = { "latitude", "longitude" })
public class Restaurant {
	private double latitude;
	private double longitude;
	private String name;
	private int score;
}
