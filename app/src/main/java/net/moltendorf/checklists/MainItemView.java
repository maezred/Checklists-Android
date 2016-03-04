package net.moltendorf.checklists;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;

/**
 * Created by moltendorf on 16/3/2.
 */
public class MainItemView extends LinearLayout {
	public static final String TAG = "MainItemView";

	private WeakReference<MainActivity> mActivity;
	private DataModel.Checklist         mChecklist;

	private TextView mChecklistTitleTextView;

	public MainItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mChecklistTitleTextView = (TextView) findViewById(R.id.item_main_checklist_title);

		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainActivity activity = mActivity.get();

				if (activity != null) {
					activity.openChecklist(mChecklist.getId());
				}
			}
		});
	}

	public void bindTo(WeakReference<MainActivity> activity, DataModel.Checklist checklist) {
		mActivity = activity;
		mChecklist = checklist;

		mChecklistTitleTextView.setText(mChecklist.getTitle());
	}
}
