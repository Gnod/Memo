package com.gnod.memo.activity;

import java.net.URLEncoder;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;

import com.gnod.activity.R;
import com.gnod.memo.adapter.MemoCursorAdapter;
import com.gnod.memo.command.Command;
import com.gnod.memo.command.DeleteCommand;
import com.gnod.memo.command.StartActivityCommand;
import com.gnod.memo.handler.MemoHandler;
import com.gnod.memo.models.AppModel;
import com.gnod.memo.provider.MemoConstants;
import com.gnod.memo.tool.ToastHelper;
import com.gnod.memo.ui.PopupList;
import com.gnod.memo.ui.PopupOptMenu;
import com.gnod.memo.views.ListItemsView;
import com.gnod.memo.widgets.Item.GestureListener;
import com.gnod.memo.widgets.PopupItemAction;
import com.gnod.memo.widgets.PopupWidget;

public class ListViewActivity extends Activity {
	
	private static final String TAG = ListViewActivity.class.getSimpleName();
	private static final int MENU_UNDO = 0;
	private static final int MENU_REDO = 1;
	
	
	private AppModel model = null;
	private ListItemsView view = null;
	private MemoHandler handler ;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		App.setContext(this);
		model = AppModel.getInstance();
		view = (ListItemsView)View.inflate(this, R.layout.activity_list_items, null);
		view.setViewListener(viewListener);
		setContentView(view);
		handler = MemoHandler.getInstance();
		handler.registerStackListener();
		initList();
	}

	@Override
	protected void onResume() {
		super.onResume();
		App.setContext(this);
		view.onActivityResume();
	}

	private void initList(){
		Intent intent = getIntent();
		model.setUri(MemoConstants.CONTENT_URI);
		if(intent.getData() == null){
			intent.setData(MemoConstants.CONTENT_URI);
		}
		Cursor cursor = model.getCursor();
		
		String[] from = new String[]{
				MemoConstants.COLUMN_NAME_NOTE_TITLE, 
				MemoConstants.COLUMN_NAME_NOTE_TIME};
		int[] to = new int[]{R.id.list_item_title, 
				R.id.list_item_time};
		
		MemoCursorAdapter adapter = new MemoCursorAdapter(App.getContext(), 
				R.layout.list_item, cursor, from, to);
		adapter.setGestureListener(gestureListener);
		view.setListAdapter(adapter);
		
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.dispose();
		App.getCommandStack().dispose();
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_MENU){
			view.toggleSideBar();
			return true;
		}else {
			return super.onKeyUp(keyCode, event);
		}
	}
	
	@Override
	public void onBackPressed() {
		if(view.isSideBarOpen()) {
			view.closeSideBar();
		}else {
			super.onBackPressed();
		}
	}

	public void onPopMenu() {
		if(!handler.canRedo && !handler.canUndo){
			return;
		}
		PopupOptMenu menu = new PopupOptMenu(getApplicationContext());
		if(handler.canUndo){
			menu.addMenuItemAction(new PopupItemAction(null, getResources().getString(R.string.menu_undo), MENU_UNDO));
		}
		if(handler.canRedo){
			menu.addMenuItemAction(new PopupItemAction(null, getResources().getString(R.string.menu_redo), MENU_REDO));
		}
		menu.setOnItemClickListener(menuListener);
		menu.show(view.getTitleView(), true);
	}
	
	private PopupWidget.ItemClickListener menuListener = new PopupWidget.ItemClickListener() {
		@Override
		public void onClicked(PopupWidget widget, View v, int pos) {
			int type = (Integer)v.getTag();
			switch (type) {
			case MENU_UNDO:
				handler.undo();
				break;
			case MENU_REDO:
				handler.redo();
				break;
			default:
				break;
			}
		}
	};

	/**
	 * Update current URI. 
	 */
	private void toggleUri(){
		Uri noteUri = ContentUris.withAppendedId(
				getIntent().getData(),
				view.getListAdapter().getItemId(App.getPosition()));
		model.setUri(noteUri);
	}
	
	public void editItem() {
		toggleUri();
		boolean moved = model.getCursor().moveToPosition(App.getPosition());
		if(!moved){
			Log.e(TAG, "invalid cursor position " + App.getPosition());
			return;
		}
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_EDIT);
		intent.setData(getIntent().getData());
		intent.setClass(ListViewActivity.this, EditorActivity.class);
		Command command = new StartActivityCommand(intent);
		handler.setCurrentCommand(command);
		handler.invoke();
		overridePendingTransition(R.anim.common_open_enter, R.anim.common_open_exit);
	}
	
	public void deleteItem() {
		ToastHelper.show(getResources().getString(R.string.hint_delete));
		toggleUri();
		Command command = new DeleteCommand(getContentResolver(),
				model.getUri());
		handler.setCurrentCommand(command);
		handler.invoke();
	}
	
	private ListItemsView.ViewListener viewListener = new ListItemsView.ViewListener() {
		
		@Override
		public void onAddItem(View v) {
			model.add();
			model.setUri(getIntent().getData());
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_INSERT);
			intent.setData(getIntent().getData());
			intent.setClass(ListViewActivity.this, EditorActivity.class);
			Command command = new StartActivityCommand(intent);
			handler.setCurrentCommand(command);
			handler.invoke();
			overridePendingTransition(R.anim.common_open_enter, R.anim.common_open_exit);
		}

		@Override
		public void onPopupMenu(View v) {
			onPopMenu();
		}
	};
	
	private GestureListener gestureListener  = new GestureListener() {
		@Override
		public boolean onSingleTapUp(View v) {
			editItem();
			return false;
		}

		@Override
		public void onScrollLeftUp(View v) {
			deleteItem();
		}

		@Override
		public void onScrollRightUp(View v) {
			ToastHelper.show(view.getResources().getString(R.string.share));
			toggleUri();
			boolean moved = model.getCursor().moveToPosition(App.getPosition());
			if(!moved){
				Log.e(TAG, "invalid cursor position " + App.getPosition());
				return;
			}
			String[] array = view.getResources().getStringArray(R.array.share_type);
			PopupList menu = new PopupList(App.getContext());
			menu.setWidth(LayoutParams.MATCH_PARENT);
			for(String s:array){
				menu.addMenuItemAction(new PopupItemAction(null, s));
			}
			menu.setOnItemClickListener(shareListener);
			menu.show(v);
		}
	};
	
	PopupWidget.ItemClickListener shareListener = new PopupWidget.ItemClickListener() {
		private final int SHARE_VIA_SMS = 0;
		private final int SHARE_VIA_EMAIL = 1;
		private final int SHARE_VIA_WEIBO = 2;
		@Override
		public void onClicked(PopupWidget widget, View v, int pos) {
			String contents = model.getContents();
			if (contents.trim().length() == 0) 
				ToastHelper.show(view.getResources().getString(R.string.hint_empty_contents), true);
			switch (pos) {
			case SHARE_VIA_SMS:
				openSendSMS(contents);
				break;
			case SHARE_VIA_EMAIL:
				openSendEmail(contents);
				break;
			case SHARE_VIA_WEIBO:
				openSendWeibo(contents);
				break;
			default:
				break;
			}
		}
		
		public void openSendSMS(String content) {
			Uri uri = Uri.parse("smsto:");
			Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
			intent.putExtra("sms_body", content);
			App.getContext().startActivity(intent);
		}

		public void openSendEmail(String content) {
			Uri uri = Uri.parse("mailto:");
			Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
			intent.putExtra(Intent.EXTRA_TEXT, content);
			try {
				App.getContext().startActivity(intent);
			} catch (Exception e) {
				ToastHelper.show(view.getResources().getString(R.string.hint_email));
			}
		}

		public void openSendWeibo(String content) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setData(Uri.parse("sinaweibo://sendweibo?content="
					+ URLEncoder.encode(content)));
			try {
				App.getContext().startActivity(intent);
			} catch (Exception e) {
				ToastHelper.show(view.getResources().getString(R.string.hint_weibo));
			}
		}
	};
	
}
