package today.gacha.gachaframework;

/**
 * @author Namhoon
 */
public interface ActivityService {
	void onPreCreating();
	void onPostCreating();

	void onPreStarting();
	void onPostStarting();

	void onPreRestarting();
	void onPostRestarting();

	void onPreResuming();
	void onPostResuming();

	void onPrePausing();
	void onPostPausing();

	void onPreStopping();
	void onPostStopping();

	void onPreDestroying();
	void onPostDestroying();
}
