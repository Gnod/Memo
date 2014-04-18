package com.gnod.memo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.gnod.activity.R;
import com.gnod.memo.tool.PreferenceHelper;
import com.gnod.memo.tool.StringHelper;

public class PasswordActivity extends Activity {

	private EditText textShow;
	private Button[] buttons = {null, 
			null, null, null, null, null, null, null, null, null};
	private Button btnBack;
	private Button btnUnlock;
	
	private String realPassword = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_password);
		initView();
		realPassword = PreferenceHelper.getString("Password");
	}
	
	private void initView()
	{
		textShow = (EditText) findViewById(R.id.password_input_show);
		
		btnBack = (Button) findViewById(R.id.password_back);
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String input = textShow.getText().toString();
				if(StringHelper.isNullOrEmpty(input))
					return;
				input = input.substring(0, input.length() - 1);
				textShow.setText(input);
			}
		});
		
		btnUnlock = (Button) findViewById(R.id.password_unlock);
		btnUnlock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();			
			}
		});
		
		buttons[0] = (Button) findViewById(R.id.password_btn0);
		buttons[1] = (Button) findViewById(R.id.password_btn1);
		buttons[2] = (Button) findViewById(R.id.password_btn2);
		buttons[3] = (Button) findViewById(R.id.password_btn3);
		buttons[4] = (Button) findViewById(R.id.password_btn4);
		buttons[5] = (Button) findViewById(R.id.password_btn5);
		buttons[6] = (Button) findViewById(R.id.password_btn6);
		buttons[7] = (Button) findViewById(R.id.password_btn7);
		buttons[8] = (Button) findViewById(R.id.password_btn8);
		buttons[9] = (Button) findViewById(R.id.password_btn9);
		for(int i = 0; i < buttons.length; i++)
		{
			buttons[i].setOnClickListener(new NumberOnClickListner(i));
		}
	}
	
	class NumberOnClickListner implements OnClickListener
	{
		public int num;
		
		public NumberOnClickListner(int num) {
			super();
			this.num = num;
		}

		@Override
		public void onClick(View v) {
			String input = textShow.getText().toString();
			if(input.length() >= 8)
				return;
			
			input += String.valueOf(num);
			textShow.setText(input);
			if(input.equals(realPassword) || 
					StringHelper.isNullOrEmpty(realPassword)){
				Intent intent = new Intent();
				intent.setClass(PasswordActivity.this, ListViewActivity.class);					
				startActivity(intent);
			}
		}
	}

}
