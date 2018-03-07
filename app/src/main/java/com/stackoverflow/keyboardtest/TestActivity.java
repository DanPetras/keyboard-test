package com.stackoverflow.keyboardtest;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;

public class TestActivity extends Activity {

	private EditText editText;
	private View redPanel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		redPanel = findViewById(R.id.redPanel);
		editText = findViewById(R.id.editText);
		findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean showRedPanel = !isRedPanelVisible();
				if (showRedPanel == KeyboardVisibilityEvent.isKeyboardVisible(TestActivity.this)) {
					showKeyboard(!showRedPanel, new KeyboardCallback() {
						@Override
						public void onKeyboardDone(boolean isVisible) {
							showRedPanel(!isVisible);
						}
					});
				} else {
					showRedPanel(showRedPanel);
				}
			}
		});
	}

	@Override
	public void onBackPressed() {
		if (isRedPanelVisible()) {
			showRedPanel(false);
			return;
		}
		super.onBackPressed();
	}

	private boolean isRedPanelVisible() {
		return redPanel.getVisibility() == View.VISIBLE;
	}

	private void showRedPanel(boolean show) {
		redPanel.setVisibility(show ? View.VISIBLE : View.GONE);
	}

	private void showKeyboard(boolean show, final KeyboardCallback callback) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		ResultReceiver receiver = new ResultReceiver(new Handler()) {
			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				callback.onKeyboardDone(resultCode == InputMethodManager.RESULT_SHOWN ||
						resultCode == InputMethodManager.RESULT_UNCHANGED_SHOWN);
			}
		};
		if (show) {
			imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT, receiver);
		} else {
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0, receiver);
		}
	}

}

interface KeyboardCallback {
	void onKeyboardDone(boolean isVisible);
}
