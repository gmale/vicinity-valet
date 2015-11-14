package com.kevingorham.android.vicinityvalet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.kevingorham.android.vicinityvalet.app.VicinityValetModule;
import com.kevingorham.android.vicinityvalet.ui.Navigator;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

	Navigator navigator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		navigator = VicinityValetModule.get().provideNavigator();
		navigator.init(this);
	}

	@Override
	protected void onResumeFragments() {
		super.onResumeFragments();
		navigator.resume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		navigator.pause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		navigator.dispose();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.home) {
			Toast.makeText(this, "options: home", Toast.LENGTH_SHORT).show();
//			mDrawerLayout.openDrawer(GravityCompat.START);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
