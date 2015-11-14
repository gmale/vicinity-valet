package com.kevingorham.android.vicinityvalet.app;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * @since 11/7/15.
 */
public class VicinityValet extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		registerActivityLifecycleCallbacks(new SimpleLifecycleCallbacks(){
			@Override
			public void onActivityDestroyed(Activity activity) {
				VicinityValet.this.onActivityDestroyed(activity);
			}

			@Override
			public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
				VicinityValet.this.onActivityCreated(activity);
			}
		});
	}

	private void onActivityCreated(Activity activity) {
		VicinityValetModule.get().create();
	}

	private void onActivityDestroyed(Activity activity) {
		VicinityValetModule.get().destroy();
	}
}
