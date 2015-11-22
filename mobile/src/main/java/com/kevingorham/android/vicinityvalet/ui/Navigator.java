package com.kevingorham.android.vicinityvalet.ui;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;

import com.kevingorham.android.vicinityvalet.R;
import com.kevingorham.android.vicinityvalet.fragment.MainContentFragment;

import butterknife.ButterKnife;

/**
 * Facilitates navigation for activities that contain an navigation drawer.
 *
 * @since 11/7/15.
 */
public class Navigator implements NavigationView.OnNavigationItemSelectedListener,
		DrawerLayout.DrawerListener, View.OnClickListener {

	public static final int DIRTY_MENU_ITEM = 0x00000001;

	// Activity fields
	// (valid only during the lifetime of mActivity)
	private AppCompatActivity mActivity;
	private Fragment mActiveFragment;
	private DrawerLayout mDrawerLayout;
	private NavigationView mNavigationView;
	private Toolbar mToolbar;

	SparseArray<Integer> navigationSectionMap;
	private int mFlags;

	/**
	 * ID of the currently selected section
	 */
	private int mCurrentSectionId;
	/**
	 * indicates whether this navigator has been configured
	 */
	private boolean mConfigured;

	/**
	 * Initialize navigation for the given activity.
	 *
	 * @param activity the activity containing a navigation drawer
	 */
	public void init(AppCompatActivity activity) {
		init(activity, R.id.drawer_layout);
	}

	/**
	 * Initialize navigation for the given activity.
	 *
	 * @param activity the activity containing the given drawer
	 * @param drawerLayoutResourceId the resource ID to use as the drawer layout
	 *
	 * @throws IllegalArgumentException if the given activity does not contain a drawerLayout with
	 *                                  the given resourceId
	 */
	public void init(AppCompatActivity activity, int drawerLayoutResourceId) {
		dispose();

		initSections();
		mActivity = activity;
		setDrawer(findDrawer(activity, drawerLayoutResourceId));
	}

	private void initSections() {
		navigationSectionMap = new SparseArray<>();
		navigationSectionMap.put(R.id.navigation_home, R.layout.fragment_nav_home);
		navigationSectionMap.put(R.id.navigation_beacons, R.layout.fragment_nav_beacons);
		navigationSectionMap.put(R.id.navigation_geofences, R.layout.fragment_nav_geofences);
		navigationSectionMap.put(R.id.navigation_nfc, R.layout.fragment_nav_nfc);
		navigationSectionMap.put(R.id.navigation_wifi, R.layout.fragment_nav_wifi);
	}


	protected void setDrawer(final DrawerLayout drawer) {
		setDrawer(drawer, R.id.navigation_view);
	}

	protected void setDrawer(final DrawerLayout drawer, int navigationViewResourceId) {
		mDrawerLayout = drawer;
		mNavigationView = findNavigationView(drawer, navigationViewResourceId);
		configureDrawer();
	}

	public void setToolbar(Toolbar toolbar) {
		mToolbar = toolbar;
		configureToolbar();
	}

	public void setActiveFragment(Fragment fragment) {
		mActiveFragment = fragment;
	}

	public void resume() {
		if (mCurrentSectionId == 0) {
			setSelection(R.id.navigation_home);
		}
	}

	public void pause() {

	}

	private void configureDrawer() {
		if (mDrawerLayout == null) {
			return;
		}

		// setup drawer
		mDrawerLayout.setStatusBarBackgroundColor(
				mDrawerLayout.getResources().getColor(R.color.theme_primary_dark)
		);
		mDrawerLayout.setDrawerListener(this);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mNavigationView.setNavigationItemSelectedListener(this);
	}

	private void configureToolbar() {
		if (mToolbar == null) {
			return;
		}

		// setup actionbar
		mActivity.setSupportActionBar(mToolbar);
		mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mActivity.getSupportActionBar().setHomeButtonEnabled(true);


		// setup toolbar
		mToolbar.setNavigationContentDescription(
				mActivity.getString(R.string.navigation_accessibility_description)
		);
		mToolbar.setTitle(R.string.app_name);
		mToolbar.setNavigationIcon(R.drawable.ic_menu);
		mToolbar.setNavigationOnClickListener(this);
	}


	@NonNull
	private DrawerLayout findDrawer(Activity activity, int drawerLayoutResourceId) {
		DrawerLayout drawerLayout = ButterKnife.findById(activity, drawerLayoutResourceId);
		if (drawerLayout == null) {
			String message = String.format("The %s activity did not contain a DrawerLayout with " +
							"the expected ID of %s.", activity.getClass().getSimpleName(),
					activity.getResources().getResourceEntryName(drawerLayoutResourceId));
			throw new IllegalArgumentException(message);
		}
		return drawerLayout;
	}

	//TODO: consolidate these into one parameterized method
	@NonNull
	private NavigationView findNavigationView(View view, int navigationViewResourceId) {
		NavigationView navigationView = ButterKnife.findById(view, navigationViewResourceId);
		if (navigationView == null) {
			String message = String.format("The %s view did not contain a NavigationView with " +
							"the expected ID of %s.", view.getClass().getSimpleName(),
					view.getResources().getResourceEntryName(navigationViewResourceId));
			throw new IllegalArgumentException(message);
		}
		return navigationView;
	}


	public void dispose() {
		if (mActivity == null) {
			return;
		}

		//TODO: cleanup activity stuff

		dispose(mDrawerLayout);
		dispose(mNavigationView);
	}

	private void dispose(DrawerLayout drawerLayout) {

	}

	private void dispose(NavigationView navigationView) {

	}


	//
	// Fragment Selection
	//

	/**
	 * Selects the fragment that corresponds with the given menu item ID
	 *
	 * @param drawerMenuItemId an ID declared in the drawer.xml file
	 */
	public void setSelection(int drawerMenuItemId) {
		if (drawerMenuItemId != mCurrentSectionId) {
			mCurrentSectionId = drawerMenuItemId;
			invalidateSelection();
		}

		if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			mDrawerLayout.closeDrawer(GravityCompat.START);
		} else {
			applySelection();
		}
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem menuItem) {
		setSelection(menuItem.getItemId());
		return true;
	}

	public void setSelectedFragment(Fragment fragment) {
		validateTag(fragment);
		setSelection(Integer.parseInt(fragment.getTag()));
	}

	private void applySelection() {
		// if the section has already been applied, then there's nothing left to do
		if (hasValidSelection())
			return;

		/* now, since the selection is dirty, we need to update the selected fragment */

		String tag = String.valueOf(mCurrentSectionId);
		FragmentManager fragmentManager = mActivity.getSupportFragmentManager();
		Fragment fragment = fragmentManager.findFragmentByTag(tag);

		if (fragment != null) {
			// if the fragment exists, then no need to create it, just pop back to it so
			// that repeatedly toggling between fragments doesn't create a giant basckstack
			fragmentManager.popBackStackImmediate(tag, 0);
			setActiveFragment(fragment);
		} else {
			// at this point, popping back to that fragment didn't happen
			// So create a new one and then show it
			fragment = createFragmentForSection(mCurrentSectionId);

			FragmentTransaction transaction = fragmentManager.beginTransaction()
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.replace(R.id.main_content, fragment, tag);

			// don't add the first fragment to the backstack
			// otherwise, pressing back on that fragment will result in a blank screen
			if (fragmentManager.getFragments() != null) {
				transaction.addToBackStack(tag);
			}

			transaction.commit();
			setActiveFragment(fragment);
		}

		// at this point, we have successfully applied the selection so remove the dirty flag
		markSelectionValid();
	}

	private void invalidateSelection() {
		mFlags |= DIRTY_MENU_ITEM;
	}

	private boolean hasValidSelection() {
		return (mFlags & DIRTY_MENU_ITEM) == 0;
	}

	private void markSelectionValid() {
		//unset the dirty flag
		mFlags &= ~DIRTY_MENU_ITEM;
	}

	/**
	 * Returns the fragment matching this menu item.
	 *
	 * @param drawerMenuItemId the ID from drawer.xml whose fragment we'd like to create
	 *
	 * @return  a new instance of the fragment that corresponds to the given ID
	 */
	public Fragment createFragmentForSection(int drawerMenuItemId) {
		int layoutId = navigationSectionMap.get(drawerMenuItemId);
		return MainContentFragment.newInstance(layoutId);
	}


	//
	// Validation
	//

	/**
	 * Validate the tag for the given fragment. Presently, the tag should always be a non-null
	 * String containing the resourceId of the navigation menu item to which this fragment
	 * corresponds.
	 *
	 * @param fragment The fragment whose tag to check
	 *
	 * @throws IllegalArgumentException if the fragment is null or the tag is invalid.
	 */
	public void validateTag(Fragment fragment) {
		if (fragment == null) {
			throw new IllegalArgumentException("Cannot get a tag from a null fragment");
		}

		String tag = fragment.getTag();
		if (tag == null || !TextUtils.isDigitsOnly(tag)) {
			throw new IllegalArgumentException(String.format("Invalid fragment tag. All fragments" +
							" should have a tag set by the Navigator. Fragment: %s  Tag: %s",
					fragment.getClass().getSimpleName(),
					fragment.getTag()));
		}
		//TODO: consider validating that the fragment ID
		//      explicitly matches one of the nav menu items
	}


	/* DrawerLayout.DrawerListener implementation */

	@Override
	public void onDrawerSlide(View drawerView, float slideOffset) {

	}

	@Override
	public void onDrawerOpened(View drawerView) {

	}

	@Override
	public void onDrawerClosed(View drawerView) {
		applySelection();
	}

	@Override
	public void onDrawerStateChanged(int newState) {

	}

	/* View.OnClickListener implementation */

	/**
	 * Listener for click events on the hamburger icon
	 */
	@Override
	public void onClick(View v) {
		mDrawerLayout.openDrawer(GravityCompat.START);
	}
}
