package net.moltendorf.checklists;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class ChecklistItemInfoActivity extends AppCompatActivity {
	private static final String TAG = "ChecklistItemInfoActivity";

	private DataModel.Checklist.Item mItem;

	private CustomEditText mInfoEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checklist_item_info);

		mInfoEditText = (CustomEditText) findViewById(R.id.checklist_item_edit_info);

		Intent intent = getIntent();

		int checklistId  = intent.getIntExtra("checklistId", -1);
		int itemPosition = intent.getIntExtra("itemPosition", -1);

		mItem = DataModel.getInstance().getChecklist(checklistId).getItem(itemPosition);
		mInfoEditText.setText(mItem.getInfo());

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(mItem.getText());

		prepareEventHandlers();

		mInfoEditText.requestFocus();

		// A delay works? What a hack!
		mInfoEditText.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) mInfoEditText.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);

				imm.showSoftInput(mInfoEditText, InputMethodManager.SHOW_IMPLICIT);
			}
		}, 200);
	}

	@Override
	protected void onPause() {
		super.onPause();

		mInfoEditText.onPauseListener();
	}

	private void prepareEventHandlers() {
		final Runnable saveInfo = new Runnable() {
			@Override
			public void run() {
				mItem.setInfo(mInfoEditText.getText().toString().trim().replaceAll("(^\\n+|\\n+$)", "").replaceAll("( |\\t){2,}", " "));
			}
		};

		mInfoEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					saveInfo.run();

					mInfoEditText.clearFocus();
					mInfoEditText.setText(mItem.getInfo());
				}
			}
		});

		mInfoEditText.setOnBackListener(new Runnable() {
			@Override
			public void run() {
				saveInfo.run();

				mInfoEditText.clearFocus();
				mInfoEditText.setText(mItem.getInfo());
			}
		});

		mInfoEditText.setOnPauseListener(saveInfo);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		switch (id) {
			case android.R.id.home:
				mInfoEditText.onBackListener();

				setResult(RESULT_OK);
				finish();

				break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		setResult(RESULT_OK);
		finish();
	}
}
