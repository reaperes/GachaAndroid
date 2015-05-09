package today.gacha.gachaframework;

import android.os.Bundle;

/**
 * @author Namhoon
 */
public interface Service {
	void onCreate(Bundle savedInstanceState);

	void onStart();

	void onRestart();

	void onResume();

	void onPause();

	void onStop();

	void onDestroy();
}