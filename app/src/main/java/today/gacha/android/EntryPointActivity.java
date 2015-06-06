package today.gacha.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.urqa.clientinterface.URQAController;
import today.gacha.android.ui.MapsActivity;

/**
 * Initialize and register urqa service.
 *
 * @author Namhoon
 */
public class EntryPointActivity extends Activity {
	private static final String TAG = EntryPointActivity.class.getSimpleName();

	private static final String URQA_KEY = "C3AC5133";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "Register URQA crash report service.");
		URQAController.InitializeAndStartSession(getApplicationContext(), URQA_KEY);

		Log.v(TAG, "Start Maps activity.");
		Intent intent = new Intent(this, MapsActivity.class);
		startActivity(intent);
	}
}
