package net.moltendorf.checklists;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by moltendorf on 16/3/2.
 */
public class CustomEditText extends EditText {
	public static final String TAG = "CustomEditText";

	private Runnable mOnBackListener  = null;
	private Runnable mOnPauseListener = null;

	public CustomEditText(Context context) {
		super(context);
	}

	public CustomEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public void setOnBackListener(Runnable r) {
		mOnBackListener = r;
	}

	public void onBackListener() {
		if (mOnBackListener != null) {
			mOnBackListener.run();
		}
	}

	public void setOnPauseListener(Runnable r) {
		mOnPauseListener = r;
	}

	public void onPauseListener() {
		if (mOnPauseListener != null) {
			mOnPauseListener.run();
		}
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && mOnBackListener != null) {
			onBackListener();
		}

		return super.onKeyPreIme(keyCode, event);
	}
}
