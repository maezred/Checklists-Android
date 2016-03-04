package net.moltendorf.checklists;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.ref.WeakReference;

/**
 * Created by moltendorf on 16/3/2.
 */
public class ChecklistItemView extends LinearLayout {
	public static final String TAG = "ChecklistItemView";

	private WeakReference<ChecklistActivity> mActivity;
	private DataModel.Checklist.Item         mItem;

	int mPosition = -1;

	private CustomEditText mItemEditText;
	private ImageView      mItemCheckButton;
	private ImageView      mItemDeleteButton;
	private ImageView      mItemInfoButton;

	public ChecklistItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		mItemEditText = (CustomEditText) findViewById(R.id.item_checklist_default_item);
		mItemCheckButton = (ImageView) findViewById(R.id.item_checklist_check_button);
		mItemDeleteButton = (ImageView) findViewById(R.id.item_checklist_delete_button);
		mItemInfoButton = (ImageView) findViewById(R.id.item_checklist_info_button);

		final Runnable finishEditItem = new Runnable() {
			@Override
			public void run() {
				String newItem = mItemEditText.getText().toString().trim().replaceAll("\\n", "").replaceAll("\\s{2,}", " ");

				if (!newItem.isEmpty()) {
					mItem.setText(newItem);
				}

				mItemEditText.clearFocus();
			}
		};

		mItemEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					finishEditItem.run();
				} else {
					ChecklistActivity activity = mActivity.get();

					if (activity != null) {
						activity.mKeyboardFocus = mItemEditText;
					}
				}
			}
		});

		mItemEditText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER) {
					finishEditItem.run();
					mItemEditText.setText(mItem.getText());

					ChecklistActivity activity = mActivity.get();

					if (activity != null) {
						activity.hideKeyboard();
					}

					return true;
				}

				return false;
			}
		});

		mItemEditText.setOnBackListener(new Runnable() {
			@Override
			public void run() {
				finishEditItem.run();

				ChecklistActivity activity = mActivity.get();

				if (activity != null) {
					activity.mKeyboardFocus = null;
				}
			}
		});

		mItemEditText.setOnPauseListener(new Runnable() {
			@Override
			public void run() {
				String newItem = mItemEditText.getText().toString().trim().replaceAll("\\n", "").replaceAll("\\s{2,}", " ");

				if (!newItem.isEmpty()) {
					mItem.setText(newItem);
				}
			}
		});

		mItemCheckButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String newItem = mItemEditText.getText().toString().trim().replaceAll("\\n", "").replaceAll("\\s{2,}", " ");

				if (!newItem.isEmpty()) {
					mItem.setText(newItem);
				}

				mItem.setChecked(!mItem.getChecked());

				ChecklistActivity activity = mActivity.get();

				if (activity != null) {
					activity.hideKeyboard();
					activity.refreshList();
				}
			}
		});

		mItemDeleteButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String newItem = mItemEditText.getText().toString().trim().replaceAll("\\n", "").replaceAll("\\s{2,}", " ");

				if (!newItem.isEmpty()) {
					mItem.setText(newItem);
				}

				mItemEditText.clearFocus();

				ChecklistActivity activity = mActivity.get();

				if (activity != null) {
					activity.hideKeyboard();
					activity.mKeyboardFocus = null;
				}

				showDeleteDialog();
			}
		});

		mItemInfoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ChecklistActivity activity = mActivity.get();

				if (activity != null) {
					Intent intent = new Intent(activity, ChecklistItemInfoActivity.class);
					intent.putExtra("checklistId", mItem.getChecklist().getId());
					intent.putExtra("itemPosition", mPosition);

					activity.startActivity(intent);
				}
			}
		});
	}

	private void showDeleteDialog() {
		ChecklistActivity activity = mActivity.get();

		if (activity != null) {
			AlertDialog.Builder alert = new AlertDialog.Builder(activity);

			alert.setMessage(String.format(getResources().getString(R.string.action_delete_item_confirm), mItem.getText()));

			alert.setPositiveButton(R.string.action_delete_confirm_positive, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();

					mItem.getChecklist().deleteItem(mPosition);

					ChecklistActivity activity = mActivity.get();

					if (activity != null) {
						activity.refreshList();
					}
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
	}

	public void bindTo(WeakReference<ChecklistActivity> activityReference, DataModel.Checklist.Item item, int position) {
		mActivity = activityReference;
		mItem = item;
		mPosition = position;

		mItemEditText.setText(mItem.getText());

		ChecklistActivity activity = mActivity.get();

		if (mItem.getAutoFocus()) {
			mItem.setAutoFocus(false);

			mItemEditText.requestFocus();
			mItemEditText.selectAll();

			if (activity != null) {
				activity.showKeyboard(mItemEditText);
			}
		}

		if (mItem.getChecked()) {
			mItemCheckButton.setImageDrawable(getResources().getDrawable(android.R.drawable.checkbox_on_background));
			mItemEditText.setPaintFlags(mItemEditText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
		} else {
			mItemCheckButton.setImageDrawable(getResources().getDrawable(android.R.drawable.checkbox_off_background));
			mItemEditText.setPaintFlags(mItemEditText.getPaintFlags() & (Integer.MAX_VALUE ^ Paint.STRIKE_THRU_TEXT_FLAG));
		}
	}
}
