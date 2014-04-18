package com.gnod.memo.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.gnod.activity.R;
import com.gnod.memo.tool.StringHelper;
import com.gnod.memo.tool.ToastHelper;

public class FeedBackDialog extends Dialog {

	private TextView titleView;

	private int preSelectedPos = -1;
	private View preItemView;
	private boolean mIsDirty = false;
	private View contentView;
	private EditText editor;

	private Button btnSend;

	private Button btnCancel;
	
	public FeedBackDialog(Context context, int theme) {
		super(context, theme);
	}
	
	public FeedBackDialog(Context context) {
		this(context, R.style.theme_wrap_contents);
	}
	
	public FeedBackDialog(Context context, View view) {
		this(context, R.style.theme_wrap_contents);
		this.contentView = view;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_feedback);
		
		bindView();
	}

	private void bindView() {
		titleView = (TextView)findViewById(R.id.dialog_feedback_title);
		editor = (EditText)findViewById(R.id.dialog_feedback_contents);
		btnSend = (Button)findViewById(R.id.dialog_feedback_send);
		btnCancel = (Button)findViewById(R.id.dialog_feedback_cancel);
		
		btnSend.setOnClickListener(sendClickListener);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}

	private View.OnClickListener sendClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			String content = editor.getText().toString();
			
			if(StringHelper.isNullOrEmpty(content)) {
				ToastHelper.show("反馈信息不能为空");
				return;
			}
			
			Intent i = new Intent(Intent.ACTION_SEND);  
			i.setType("message/rfc822") ;
			i.putExtra(Intent.EXTRA_EMAIL, new String[]{"gnodsy@gmail.com"});  
			i.putExtra(Intent.EXTRA_SUBJECT, "Memo用户反馈-Android客户端");  
			i.putExtra(Intent.EXTRA_TEXT,content);
			getContext().startActivity(Intent.createChooser(i, "Sending mail"));
			dismiss();
		}
	};
}
