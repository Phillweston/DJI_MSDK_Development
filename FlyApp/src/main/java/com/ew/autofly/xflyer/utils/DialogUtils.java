package com.ew.autofly.xflyer.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

public class DialogUtils {
	public static final int NO_ICON = -1;

	public static Dialog createNormalDialog(Context ctx, int iconId,
			String title, String message, String btnName,
			DialogInterface.OnClickListener listener) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

		if (iconId != -1) {
			builder.setIcon(iconId);
		}

		builder.setTitle(title);

		builder.setMessage(message);

		builder.setPositiveButton(btnName, listener);

		dialog = builder.create();
		return dialog;
	}

	public static Dialog createConfirmDialog(Context ctx, int iconId,
			String title, String message, String positiveBtnName,
			String negativeBtnName,
			DialogInterface.OnClickListener positiveListener,
			DialogInterface.OnClickListener negativeListener) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

		if (iconId != -1) {
			builder.setIcon(iconId);
		}

		builder.setTitle(title);

		builder.setMessage(message);

		builder.setPositiveButton(positiveBtnName, positiveListener);
		builder.setNegativeButton(negativeBtnName, negativeListener);

		dialog = builder.create();
		return dialog;
	}

	public static Dialog createListDialog(Context ctx, int iconId,
			String title, int itemsId, DialogInterface.OnClickListener listener) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

		if (iconId != -1) {
			builder.setIcon(iconId);
		}

		builder.setTitle(title);

		builder.setItems(itemsId, listener);

		dialog = builder.create();
		return dialog;
	}

	public static Dialog createRadioDialog(Context ctx, int iconId,
			String title, int itemsId,
			DialogInterface.OnClickListener listener, String btnName,
			DialogInterface.OnClickListener listener2) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

		if (iconId != -1) {
			builder.setIcon(iconId);
		}

		builder.setTitle(title);

		builder.setSingleChoiceItems(itemsId, 0, listener);

		builder.setPositiveButton(btnName, listener2);

		dialog = builder.create();
		return dialog;
	}

	public static Dialog createCheckBoxDialog(Context ctx, int iconId,
			String title, int itemsId, boolean[] flags,
			DialogInterface.OnMultiChoiceClickListener listener,
			String btnName, DialogInterface.OnClickListener listener2) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

		if (iconId != -1) {
			builder.setIcon(iconId);
		}

		builder.setTitle(title);
		builder.setMultiChoiceItems(itemsId, flags, listener);

		builder.setPositiveButton(btnName, listener2);

		dialog = builder.create();
		return dialog;
	}

	public static Dialog createCustomDialog(Context ctx, int iconId,
			String title, View view, String positiveBtnName,
			String negativeBtnName,
			DialogInterface.OnClickListener positiveListener,
			DialogInterface.OnClickListener negativeListener) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);

		if (iconId != -1) {
			builder.setIcon(iconId);
		}

		builder.setTitle(title);

		builder.setView(view);

		builder.setPositiveButton(positiveBtnName, positiveListener);
		builder.setNegativeButton(negativeBtnName, negativeListener);

		dialog = builder.create();
		return dialog;
	}
}