package today.gacha.android.utils;

/**
 * @author Namhoon
 */
public class LogUtils {
	public static final String PRE_FIX = "Gacha_";

	public static String makeTag(Class clazz) {
		return PRE_FIX + clazz.getSimpleName();
	}
}
