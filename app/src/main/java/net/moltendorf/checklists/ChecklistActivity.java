package net.moltendorf.checklists;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.io.File;

public class ChecklistActivity extends AppCompatActivity {
	public static final String TAG = "ChecklistActivity";

	private DataModel           mDataModel;
	private DataModel.Checklist mChecklist;
	private ChecklistAdapter    mAdapter;

	private FloatingActionButton fab;
	private CustomEditText       mTitleEditText;
	private RecyclerView         mListView;

	protected CustomEditText mKeyboardFocus = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checklist);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		mDataModel = DataModel.getInstance();

		if (!mDataModel.isLoaded()) {
			mDataModel.setFile(new File(getFilesDir(), "data.json"));
		}

		prepareViewReferences();
		prepareEventHandlers();
		prepareInitialTasks();

		addChecklistsAdapter();
	}

	@Override
	public void onResume() {
		super.onResume();

		if (mKeyboardFocus != null) {
			showKeyboard(mKeyboardFocus);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if (mKeyboardFocus != null) {
			mKeyboardFocus.onPauseListener();
		}
	}

	private void prepareViewReferences() {
		fab = (FloatingActionButton) findViewById(R.id.fab);
		mTitleEditText = (CustomEditText) findViewById(R.id.checklist_title_edit_text);
		mListView = (RecyclerView) findViewById(R.id.checklist_list_view);
	}

	private void prepareEventHandlers() {
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				int position = mChecklist.size();

				DataModel.Checklist.Item item = mChecklist.getNewItem();
				item.setText(getResources().getString(R.string.checklist_default_item));

				mAdapter.notifyDataSetChanged();

				mListView.scrollToPosition(position);
			}
		});

		final Runnable finishEditTitle = new Runnable() {
			@Override
			public void run() {
				String newTitle = mTitleEditText.getText().toString().trim().replaceAll("\\n", "").replaceAll("\\s{2,}", " ");

				if (!newTitle.isEmpty()) {
					mChecklist.setTitle(newTitle);
					getSupportActionBar().setTitle(newTitle);
				}

				mTitleEditText.setVisibility(View.GONE);
				fab.show();
			}
		};

		mTitleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					finishEditTitle.run();
				}
			}
		});

		mTitleEditText.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					finishEditTitle.run();

					hideKeyboard();
				}

				return false;
			}
		});

		mTitleEditText.setOnBackListener(new Runnable() {
			@Override
			public void run() {
				finishEditTitle.run();

				mKeyboardFocus = null;
			}

		});

		mTitleEditText.setOnPauseListener(new Runnable() {
			@Override
			public void run() {
				String newTitle = mTitleEditText.getText().toString().trim().replaceAll("\\n", "").replaceAll("\\s{2,}", " ");

				if (!newTitle.isEmpty()) {
					mChecklist.setTitle(newTitle);
					getSupportActionBar().setTitle(newTitle);
				}
			}
		});

//		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				// Edit an item.
//			}
//		});
	}

	private void prepareInitialTasks() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		int checklistId = getIntent().getIntExtra("checklistId", -1);

		if (checklistId >= 0) {
			mChecklist = mDataModel.getChecklist(checklistId);

			mTitleEditText.setVisibility(View.GONE);
		} else {
			mChecklist = mDataModel.getNewChecklist();
			mChecklist.setTitle(getResources().getString(R.string.checklist_default_name));

			renameList();
		}

		getSupportActionBar().setTitle(mChecklist.getTitle());
	}

	private void addChecklistsAdapter() {
		mAdapter = new ChecklistAdapter(this, mChecklist);

		mListView.setLayoutManager(new LinearLayoutManager(this));
		mListView.setAdapter(mAdapter);
	}

	public void refreshList() {
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();

		setResult(RESULT_OK);
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_checklist, menu);
		return true;
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
				if (mKeyboardFocus != null) {
					mKeyboardFocus.onBackListener();

					setResult(RESULT_OK);
					finish();
				}

				break;

			case R.id.action_rename:
				renameList();
				break;

			case R.id.action_delete:
				showDeleteDialog();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void showDeleteDialog() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setMessage(R.string.action_delete_checklist_confirm);

		alert.setPositiveButton(R.string.action_delete_confirm_positive, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();

				mDataModel.deleteChecklist(mChecklist.getId());

				setResult(RESULT_OK);
				finish();
			}
		});

		alert.setNegativeButton(R.string.action_delete_confirm_negative, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		alert.show();
	}

	private void renameList() {
		fab.hide();

		mTitleEditText.setText(mChecklist.getTitle());
		mTitleEditText.setVisibility(View.VISIBLE);
		mTitleEditText.requestFocus();
		mTitleEditText.selectAll();

		showKeyboard(mTitleEditText);
	}

	protected void showKeyboard(final CustomEditText view) {
		mKeyboardFocus = view;

		// A delay works? What a hack!
		view.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);

				imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
			}
		}, 200);
	}

	protected void hideKeyboard() {
		InputMethodManager imm  = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
		View               view = getCurrentFocus();

		if (view == null) {
			view = new View(this);
		}

		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

		mKeyboardFocus = null;
	}
}
