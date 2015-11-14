package com.kevingorham.android.vicinityvalet.app;

import com.kevingorham.android.vicinityvalet.ui.Navigator;

/**
 * Poor man's dependency injection. This module is Intended to make it easy to transition to dagger
 * if this app grows. Until then, I just want to get this proof of concept on its feet. For now
 * all dependencies are created and destroyed along with the activity lifecycle.
 *
 * @since 11/7/15.
 */
public enum VicinityValetModule {
	INSTANCE;

	private Navigator mNavigator;

	public static VicinityValetModule get() {
		return INSTANCE;
	}

	public Navigator provideNavigator() {
		return mNavigator;
	}

	public void create() {
		mNavigator = new Navigator();
	}

	public void destroy() {
		mNavigator = null;
	}

}
