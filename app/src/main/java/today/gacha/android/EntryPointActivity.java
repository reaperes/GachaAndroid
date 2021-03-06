package today.gacha.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.urqa.clientinterface.URQAController;
import today.gacha.android.ui.MapsActivity;
import today.gacha.android.utils.ConnectionResultUtils;
import today.gacha.android.utils.LogUtils;

/**
 * Initialize and register urqa service.
 *
 * @author Namhoon
 */
public class EntryPointActivity extends Activity {
	private static final String TAG = LogUtils.makeTag(EntryPointActivity.class);

	private static final String URQA_KEY = "C3AC5133";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO handling if google play service is not available.
		if (!checkGooglePlayServiceIfAvailable()) {
			Toast.makeText(this, "Google play service is not available.", Toast.LENGTH_LONG).show();
			return ;
		}

		setUpURQA();

		Log.v(TAG, "Start Maps activity.");
		Intent intent = new Intent(this, MapsActivity.class);
		startActivity(intent);
	}

	private boolean checkGooglePlayServiceIfAvailable() {
		int statusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		Log.v(TAG, "Check if google play service available. status code is - " + statusCode);
		if (statusCode != ConnectionResult.SUCCESS) {
			Log.e(TAG, "Can not use Google play service. Cause is - " + ConnectionResultUtils.toString(statusCode));
			return false;
		}
		return true;
	}

	private void setUpURQA() {
		Log.d(TAG, "Register URQA crash report service.");
		URQAController.InitializeAndStartSession(getApplicationContext(), URQA_KEY);
	}
}
