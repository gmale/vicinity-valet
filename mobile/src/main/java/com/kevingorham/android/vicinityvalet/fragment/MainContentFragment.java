package com.kevingorham.android.vicinityvalet.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kevingorham.android.vicinityvalet.R;
import com.kevingorham.android.vicinityvalet.app.VicinityValetModule;
import com.kevingorham.android.vicinityvalet.ui.Navigator;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @since 11/8/15.
 */
public class MainContentFragment extends Fragment {

	/**
	 * defines the content layout for this fragment
	 */
	private static final String ARGUMENT_LAYOUT_ID = "argument_layout_id";

	@Nullable
	@Bind(R.id.toolbar)
	Toolbar mToolbar;

	public static MainContentFragment newInstance(int layoutResourceId) {
		if (layoutResourceId <= 0) {
			throw new IllegalArgumentException("MainContentFragment requires a layout ID that is" +
					" " +
					"greater than zero. ID provided: " + layoutResourceId);
		}

		Bundle args = new Bundle();
		args.putInt(ARGUMENT_LAYOUT_ID, layoutResourceId);

		MainContentFragment fragment = new MainContentFragment();
		fragment.setArguments(args);
		return fragment;
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
			savedInstanceState) {
		int layoutId = getArguments().getInt(ARGUMENT_LAYOUT_ID);
		View view = inflater.inflate(layoutId, container, false);
		ButterKnife.bind(this, view);

		Navigator navigator = VicinityValetModule.get().provideNavigator();
		navigator.setToolbar(mToolbar);
		return view;
	}

}
