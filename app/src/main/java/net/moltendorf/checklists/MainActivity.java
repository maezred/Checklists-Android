package net.moltendorf.checklists;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.io.File;

public class MainActivity extends AppCompatActivity {
	public static final int    REQUEST_CODE = 0;
	public static final String TAG          = MainActivity.class.getSimpleName();

	private DataModel   mDataModel;
	private MainAdapter mAdapter;

	private FloatingActionButton fab;
	private RecyclerView         mListView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		mDataModel = DataModel.getInstance();

		if (!mDataModel.isLoaded()) {
			mDataModel.setFile(new File(getFilesDir(), "data.json"));
		}

		prepareViewReferences();
		prepareEventHandlers();

		addChecklistsAdapter();
	}

	private void prepareViewReferences() {
		fab = (FloatingActionButton) findViewById(R.id.fab);
		mListView = (RecyclerView) findViewById(R.id.main_list_view);
	}

	private void prepareEventHandlers() {
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(MainActivity.this, ChecklistActivity.class);

				startActivityForResult(intent, 0);
			}
		});

//		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				Intent intent = new Intent(MainActivity.this, ChecklistActivity.class);
//
//				startActivityForResult(intent, 0);
//			}
//		});
	}

	private void addChecklistsAdapter() {
		mAdapter = new MainAdapter(this, mDataModel);

		mListView.setLayoutManager(new LinearLayoutManager(this));
		mListView.setAdapter(mAdapter);
	}

	protected void openChecklist(int id) {
		Intent intent = new Intent(MainActivity.this, ChecklistActivity.class);
		intent.putExtra("checklistId", id);

		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQUEST_CODE) {
			if (resultCode == RESULT_OK) {

			}
		}

//		Snackbar.make(findViewById(R.id.fab), "Welcome back!", Snackbar.LENGTH_LONG)
//			.setAction("Action", null).show();
	}
}
