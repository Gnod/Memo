package com.gnod.memo.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.gnod.activity.R;
import com.gnod.memo.command.Command;
import com.gnod.memo.command.DeleteCommand;
import com.gnod.memo.command.ForwardCommand;
import com.gnod.memo.command.InsertCommand;
import com.gnod.memo.command.PreviousCommand;
import com.gnod.memo.command.UpdateCommand;
import com.gnod.memo.handler.MemoHandler;
import com.gnod.memo.models.AppModel;
import com.gnod.memo.provider.MemoConstants;
import com.gnod.memo.views.EditorView;

public class EditorActivity extends Activity {
//	private static final String TAG = EditorActivity.class.getSimpleName();
	
	AppModel model = null;
	EditorView view = null;
	MemoHandler handler = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		model = AppModel.getInstance();
		view = (EditorView)View.inflate(this, R.layout.activity_editor, null);
		view.setViewListener(viewListener);
		setContentView(view);
		handler = MemoHandler.getInstance();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		App.setContext(this);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			view.setEditorState();
			if(view.isSideBarOpen()){
				view.closeSideBar();
				return true;
			}
		}else if(keyCode == KeyEvent.KEYCODE_MENU){
			view.setEditorState();
			view.toggleSideBar();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		view.onDestroy();
	}

	private EditorView.EditorViewListener viewListener = new EditorView.EditorViewListener() {
		@Override
		public void onSave() {
//			ToastHelper.show("save");
			String curText = view.getContents();
			ContentValues values = new ContentValues();
			long now = System.currentTimeMillis();
			String title = (curText.length() > 60)? curText.substring(0, 61):curText;
			values.put(MemoConstants.COLUMN_NAME_DATE_MODIFIED, now);
			values.put(MemoConstants.COLUMN_NAME_NOTE_TITLE, title);
			values.put(MemoConstants.COLUMN_NAME_NOTE_CONTENTS, curText);
			values.put(MemoConstants.COLUMN_NAME_NOTE_BG_ID, view.getViewBgId());
			
			String action = getIntent().getAction();
			Command command = null;
			if (Intent.ACTION_INSERT.equals(action)) {
				command = new InsertCommand(model.getUri(), values);
			} else if (Intent.ACTION_EDIT.equals(action)) {
				command = new UpdateCommand(model.getUri(), values);
			}
			handler.setCurrentCommand(command);
			handler.invoke();
			finish();
		}
		
		@Override
		public void onPrevious() {
			Command command = new PreviousCommand(App.getContext(), model);
			handler.setCurrentCommand(command);
			handler.invoke();
		}
		
		@Override
		public void onForward() {
			Command command = new ForwardCommand(App.getContext(), model);
			handler.setCurrentCommand(command);
			handler.invoke();
		}
		
		@Override
		public void onDel() {
//			ToastHelper.show("delete");
			if(getIntent().getAction().equals(Intent.ACTION_EDIT)){
				Command command = new DeleteCommand(
						getContentResolver(), model.getUri());
				handler.setCurrentCommand(command);
				handler.invoke();
			}
			finish();
		}

		@Override
		public void onChangeBg(View v) {
			view.getColorGrid().show(v);
		}
		
	};
}
