package dk.nodes.widgets.dialogs;

import android.app.Dialog;
import android.content.Context;

import dk.nodes.controllers.dialogqueuing.NDialogQueueInterface;
import dk.nodes.controllers.dialogqueuing.NDialogQueueListener;
import dk.nodes.utils.NLog;

/**
 * This dialog is preventing most stupid crashes and will auto dismiss if view looses focus.
 * @author Casper Rasmussen - 2012
 *
 */
public abstract class NDialog extends Dialog implements NDialogQueueInterface{
	public boolean AUTO_DISMISS_ON_FOCUS_LOST = true;
	private NDialogQueueListener mNDialogQueueListener;
	
	/**
	 * Remember to check if context is null
	 * @param context
	 * @param theme
	 */
	public NDialog(Context context, int theme) {
		super(context, theme);
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if(AUTO_DISMISS_ON_FOCUS_LOST && !hasFocus){
			dismiss();	
		}
		super.onWindowFocusChanged(hasFocus);
	}
	
	@Override
	public void cancel() {
		try{
			super.cancel();
		}
		catch(Exception e){
			NLog.e("NDialog cancel",e);
		}
	}

	@Override
	public void dismiss() {
		try{
			super.dismiss();
			if(mNDialogQueueListener != null)
				mNDialogQueueListener.onGone();
		}
		catch(Exception e){
			NLog.e("NDialog dismiss",e);
		}
	}

	@Override
	public void show() {
		try{
			super.show();
		}
		catch(Exception e){
			NLog.e("NDialog show",e);
		}
	}

	@Override
	public void onDetachedFromWindow() {
		try{
			super.onDetachedFromWindow();
		}
		catch(Exception e){
			NLog.e("NDialog onDetachedFromWindow",e);
		}
	}

	@Override
	public void setQueueListener(NDialogQueueListener listener) {
		this.mNDialogQueueListener = listener;
	}

	@Override
	public String getQueueTag() {
		return null;
	}
}
