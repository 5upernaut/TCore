package dk.nodes.controllers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import dk.nodes.utils.NLog;
import dk.nodes.utils.NUtils;

/**
 * @author Thomas Nielsen - 2012
 */
public class NKeyboardController {

	private static String TAG = "NKeyboardController";
	static boolean isKeyboardShown = false;
	private static OnGlobalLayoutListener mOnGlobalLayoutListener;

	/**
	 * Returns true if keyboard is up, and false if keyboard is down
	 * 
	 * @param activity
	 * @return
	 */
	public static boolean isKeyboardShowing(final Activity activity) {
		Rect r = new Rect();
		try {
			activity.findViewById(android.R.id.content).getWindowVisibleDisplayFrame(r);
			int heightDiff = activity.findViewById(android.R.id.content).getRootView().getHeight() - (r.bottom - r.top);
			if (heightDiff > NScreenParameters.toPx(100)) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * @deprecated  Use public static void setCancelable(final Activity activity, final View contentView, boolean cancelable, final EditText...editText) instead;
	 * 
	 * @param activity
	 * @param contentView
	 * @param editText
	 * @param cancelable
	 * 
	 * This makes the user able to cancel (hide) the keyboard when touching the screen outside the EditText.
	 */
	public static void setCancelable(final Activity activity, final View contentView, final EditText editText, boolean cancelable){
		setCancelable(activity, contentView, cancelable, editText);
	}

	/**
	 * @param activity
	 * @param contentView
	 * @param cancelable
	 * @param editText(s) You can now add multiple EditTexts, which is nice for pages that 
	 * have more than one EditText that you should be able to hide the keyboard when clicking outside of it.
	 * Fx. when you need to fill out both phoneumber, name, address etc on a single page.
	 * 
	 * This makes the user able to cancel (hide) the keyboard when touching the screen outside the EditText.
	 */
	public static void setCancelable(final Activity activity, final View contentView, boolean cancelable, final EditText...editText){
		contentView.setClickable(cancelable);
		contentView.setFocusableInTouchMode(cancelable);

		for(final EditText et : editText){
			if(cancelable){
				et.setOnFocusChangeListener(new OnFocusChangeListener() {
					@Override
					public void onFocusChange(View v, boolean hasFocus) {
						NKeyboardController.showKeyboard(activity, et, hasFocus);
					}
				}); 
			} else {
				et.setOnFocusChangeListener(null);
			}
		}
	}


	/**
	 * @param activity
	 * @param editText This is needed.
	 * @param show true = show, false = hide.
	 * 
	 * This can be used with an OnFocusChangeListener on an EditText for the same functionality as NKeyboardController.setCancelable() for lots of win:
	 * 
	 * 		inputEt.setOnFocusChangeListener(new OnFocusChangeListener() {
	 *		@Override
	 *		public void onFocusChange(View v, boolean hasFocus) {
	 *			NKeyboardController.showKeyboard(SomeActivity.this, inputEt, hasFocus);
	 *		}
	 *	}); 
	 *
	 * and then just set the layouts top-parent view to focusableInTouchMode=true AND clickable=true in xml, so that it can take away the EditText's focus. 
	 * This makes the user able to click anywhere on the screen to hide the keyboard and remove focus from the EditText. Win.
	 */
	public static void showKeyboard(final Activity activity, final EditText editText, boolean show) {
		try {
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (show){
				imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
				editText.requestFocus();
			}
			else
				imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		} catch (Exception e) {
			NLog.e(TAG + " hideKeyboard", e);
		}
	}

	/**
	 * @param mActivity
	 * @param editText
	 *            This is needed.
	 */
	public static void showKeyboard(Activity mActivity, EditText editText) {
		InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
		editText.requestFocus();
	}

	public static void showKeyboardDelayed(final Activity mActivity, final EditText editText, int delayMs) {
		NUtils.performTaskDelayWithCallbackInMainThread(mActivity, delayMs, new NUtils.NCallbackListener() {

			@Override
			public void onCallback() {
				if(mActivity != null){
					InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
					editText.requestFocus();
				}
			}
		});
	}

	public static void showKeyboardTryMultipleTimes(Activity mActivity, EditText editText){
		showKeyboard(mActivity, editText);
		showKeyboardDelayed(mActivity, editText, 250);
		showKeyboardDelayed(mActivity, editText, 500);
	}

	/**
	 * @param activity
	 * @param editText
	 */
	public static void
    hideKeyboard(Activity activity, EditText editText) {
		if(activity == null || editText == null){
			NLog.d(TAG+" hideKeyboard","Activity or EditText was null");
			return;
		}

		try {
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		} catch (Exception e) {
			NLog.e(TAG + " hideKeyboard",e);
		}
	}

	/**
	 * @param activity
	 */
	public static void hideKeyboard(Activity activity) {
		if(activity == null){
			NLog.d(TAG+" hideKeyboard","Activity was null");
			return;
		}

		try {
			InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
		} catch (Exception e) {
			NLog.e(TAG + " hideKeyboard",e);
		}
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	public static void setOnKeyboardListener(final Activity activity, final NKeyboardShowingListener nKeyboardShowingListener) {

		isKeyboardShown = isKeyboardShowing(activity);
		final View activityRootView = activity.findViewById(android.R.id.content);

		if(nKeyboardShowingListener == null && mOnGlobalLayoutListener != null){
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
				activityRootView.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
			else
				activityRootView.getViewTreeObserver().removeGlobalOnLayoutListener(mOnGlobalLayoutListener);
		}
		else{	
			mOnGlobalLayoutListener = new OnGlobalLayoutListener() {
				@Override
				public void onGlobalLayout() {
					if (isKeyboardShowing(activity) != isKeyboardShown && nKeyboardShowingListener != null) {
						if (isKeyboardShowing(activity)) {
							nKeyboardShowingListener.onKeyboardShown();
							isKeyboardShown = true;
						} else {
							nKeyboardShowingListener.onKeyboardHidden();
							isKeyboardShown = false;
						}
					}
				}
			};

			activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
		}
	}

	public interface NKeyboardShowingListener {
		public void onKeyboardShown();
		public void onKeyboardHidden();
	}

}
