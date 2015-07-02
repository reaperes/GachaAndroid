package today.gacha.android.services;

import android.content.Context;
import com.android.volley.*;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.Getter;
import today.gacha.android.core.service.GachaService;
import today.gacha.android.utils.LogUtils;

/**
 * @author Namhoon
 */
public class RestApiService implements GachaService {
	private static final String TAG = LogUtils.makeTag(RestApiService.class);

	private static final String BASE_URL = "http://192.168.0.10:8080";

	private static volatile RestApiService singleton = null;

	private final RequestQueue requestQueue;

	private RestApiService(Context context) {
		requestQueue = Volley.newRequestQueue(context);
	}

	public static RestApiService getService(Context context) {
		if (singleton == null) {
			synchronized (RestApiService.class) {
				if (singleton == null) {
					singleton = new RestApiService(context.getApplicationContext());
				}
			}
		}
		return singleton;
	}

	public <T> void request(Method method, String url, Class<T> clazz, Listener<T> listener,
			ErrorListener errorListener) {
		requestQueue.add(new GsonRequest<>(method.getValue(), BASE_URL + url, clazz, listener, errorListener));
	}

	/**
	 * Adapter for volley
	 * @see com.android.volley.Request.Method
	 */
	public enum Method {
		GET(0),
		POST(1);

		@Getter
		int value;

		Method(int value) {
			this.value = value;
		}
	}

	private class GsonRequest<T> extends Request<T> {
		private final Gson gson = new Gson();
		private final Class<T> clazz;
		private final Listener<T> listener;

		public GsonRequest(int method, String url, Class<T> clazz, Listener<T> listener, ErrorListener errorListener) {
			super(method, url, errorListener);
			this.clazz = clazz;
			this.listener = listener;
		}

		@Override
		protected void deliverResponse(T response) {
			listener.onResponse(response);
		}

		@Override
		protected Response<T> parseNetworkResponse(NetworkResponse response) {
			try {
				String json = new String(response.data);
				return Response.success(gson.fromJson(json, clazz), HttpHeaderParser.parseCacheHeaders(response));
			} catch (JsonSyntaxException e) {
				return Response.error(new ParseError(e));
			}
		}
	}
}
