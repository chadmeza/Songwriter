/**
 * @author Chad Meza
 */
package com.chadmeza.songwriter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

public class DetailActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		DetailFragment detailFrag = (DetailFragment) getFragmentManager().findFragmentById(R.id.detailFragment);
		detailFrag.mDatasource.updateSong(detailFrag.mSong);
	}
}
