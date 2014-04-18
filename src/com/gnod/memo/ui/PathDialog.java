package com.gnod.memo.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.gnod.activity.R;
import com.gnod.memo.provider.MemoConstants;
import com.gnod.memo.tool.PrefConstants;
import com.gnod.memo.tool.PreferenceHelper;
import com.gnod.memo.tool.StringHelper;
import com.gnod.memo.tool.ToastHelper;

public class PathDialog extends Dialog {


	private EditText editor;
	private Button btnSend;
	private Button btnCancel;
	private PathListener pathListener;
	
	public static interface PathListener {
		public void onOkClicked(View v, String path);
	}
	public void setPathListener(PathListener listener) {
		pathListener = listener;
	}
	
	public PathDialog(Context context, int theme) {
		super(context, theme);
	}
	
	public PathDialog(Context context) {
		this(context, R.style.theme_wrap_contents);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_path);
		
		bindView();
	}

	private void bindView() {
		editor = (EditText)findViewById(R.id.dialog_path_contents);
		btnSend = (Button)findViewById(R.id.dialog_path_ok);
		btnCancel = (Button)findViewById(R.id.dialog_path_cancel);
		
		btnSend.setOnClickListener(okClickListener);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		
		String content = PreferenceHelper.getString(PrefConstants.OUTPUT_PATH, "Memo");
		editor.setText(content);
	}

	private View.OnClickListener okClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String content = editor.getText().toString();
			
			if(StringHelper.isNullOrEmpty(content)) {
				ToastHelper.show("路径不能为空");
				return;
			}
			SharedPreferences pref = getContext().getSharedPreferences(
					MemoConstants.PREFERENCES_NAME, Context.MODE_APPEND);
			final Editor editor = pref.edit();
			editor.putString(PrefConstants.OUTPUT_PATH, content);
			editor.commit();
			dismiss();
			pathListener.onOkClicked(v, content);
		}
	};

}
