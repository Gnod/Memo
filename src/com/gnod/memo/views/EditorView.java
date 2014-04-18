package com.gnod.memo.views;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gnod.activity.R;
import com.gnod.memo.activity.App;
import com.gnod.memo.events.Event;
import com.gnod.memo.events.EventListener;
import com.gnod.memo.models.AppModel;
import com.gnod.memo.provider.MemoConstants;
import com.gnod.memo.tool.PrefConstants;
import com.gnod.memo.tool.PreferenceHelper;
import com.gnod.memo.tool.StringHelper;
import com.gnod.memo.tool.ToastHelper;
import com.gnod.memo.widgets.PopupImageGrid;
import com.gnod.memo.widgets.PopupItemAction;
import com.gnod.memo.widgets.PopupWidget;
import com.gnod.memo.widgets.SideBarItemAction;
import com.gnod.memo.widgets.TextEditor;

import java.io.File;

public class EditorView extends SideBarView {

//	private static final String TAG = EditorView.class.getSimpleName();
	
	private AppModel model = null;
	
	private static final int SIDEBAR_ADD = 0;
	private static final int SIDEBAR_OUTPUT_TXT = 1;
	
	AlphaAnimation alphaAnim = new AlphaAnimation(0, 1);
	TranslateAnimation animRightIn = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
            );
	TranslateAnimation animLeftIn = new TranslateAnimation(
            Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f
            );
	{
		alphaAnim.setStartOffset(400);
		alphaAnim.setDuration(300);
		animLeftIn.setDuration(500);
		animRightIn.setDuration(500);
	}
	private ImageView btnBack;
	private ImageView btnDel;
	private ImageView btnSave;
	private ImageView btnForward;
	private ImageView btnBg;
	private ImageView[] btnHolder = new ImageView[]{
			btnBack,btnDel,btnSave,btnForward, btnBg};
	
	private EditorViewListener viewListener;

	private EditText editor;

	private TextView modTime;

	private RelativeLayout bgLayout;
	
	private PopupImageGrid colorGrid;

	private int bgId = 0;

	private String mOldContents;
	
	public static interface EditorViewListener{
		public void onSave();
		public void onDel();
		public void onForward();
		public void onPrevious();
		public void onChangeBg(View v);
	}
	
	public EditorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		model = AppModel.getInstance();
	}

	public void setViewListener(EditorViewListener listener){
		this.viewListener = listener;
	}
	
	@Override
	public OnItemClickListener getSideBarClickListener() {
		return sideBarItemClickListener;
	}

	@Override
	public void initView() {
		sideBar = findViewById(R.id.editor_layout_sidebar);
		mainView = findViewById(R.id.editor_layout_mainview);
		sideBarList = (ListView)findViewById(R.id.editor_layout_sidebar_itemlist);
		editor = (EditText) findViewById(R.id.note_editor);
		modTime = (TextView) findViewById(R.id.note_editor_time);
		btnHolder[0] = btnBack = (ImageView) findViewById(R.id.note_editor_back);
		btnHolder[1] = btnDel = (ImageView)findViewById(R.id.editor_del);
		btnHolder[2] = btnSave = (ImageView)findViewById(R.id.editor_save);
		btnHolder[3] = btnForward = (ImageView) findViewById(R.id.editor_forward);
		btnHolder[4] = btnBg = (ImageView)findViewById(R.id.note_editor_bg_change);
		bgLayout = (RelativeLayout) findViewById(R.id.note_editor_bg_layout);
		
		
		colorGrid = new PopupImageGrid(App.getContext());
		colorGrid.addMenuItemAction(new PopupItemAction(App.getContext(), R.drawable.flig_bg_green, ""));
		colorGrid.addMenuItemAction(new PopupItemAction(App.getContext(), R.drawable.flig_bg_red, ""));
		colorGrid.addMenuItemAction(new PopupItemAction(App.getContext(), R.drawable.flig_bg_blue, ""));
		colorGrid.addMenuItemAction(new PopupItemAction(App.getContext(), R.drawable.flig_bg_deepblue, ""));
		colorGrid.addMenuItemAction(new PopupItemAction(App.getContext(), R.drawable.flig_bg_yellow, ""));
		colorGrid.setOnItemClickListener(colorGridListener);
		
		for(ImageView b:btnHolder){
			b.setOnClickListener(clickListener);
		}
		
		editor.addTextChangedListener(textWatcher);		
		((TextEditor)editor).setOnGestureListener(gestureListener);
		((TextEditor)editor).enableScale(true);
		
		model.addEventListener(AppModel.ViewEvent.VIEW_PREVIOUS, modelListener);
		model.addEventListener(AppModel.ViewEvent.VIEW_NEXT, modelListener);
		model.addEventListener(AppModel.ViewEvent.VIEW_NEW, modelListener);
		bindView();
		setSideBarInLeft(false);
		
		int bgId = PreferenceHelper.getInt(PrefConstants.BG_ID, 2);
		mainView.setBackgroundResource(PrefConstants.BG_IDS[bgId]);
	}

	@Override
	public void initSideBarActions() {
		SideBarItemAction addAction = new SideBarItemAction(getResources().getString(R.string.menu_add), SIDEBAR_ADD);
		SideBarItemAction outputAction = new SideBarItemAction(getResources().getString(R.string.output_txt), SIDEBAR_OUTPUT_TXT);
		
		sideBarActions.add(addAction);
		sideBarActions.add(outputAction);
	}
	
	public void onDestroy(){
		model.removeEventListener(AppModel.ViewEvent.VIEW_PREVIOUS, modelListener);
		model.removeEventListener(AppModel.ViewEvent.VIEW_NEXT, modelListener);
		model.removeEventListener(AppModel.ViewEvent.VIEW_NEW, modelListener);
	}

	private void bindView(){
		btnBack.setEnabled(model.hasPrevious());
		btnForward.setEnabled(model.hasForward());
		editor.setText(model.getContents());
		modTime.setText(model.getSubTime());
		mOldContents = editor.getText().toString();
		setViewBg(model.getBackground());
	}
	
	public void setEditorState() {
		if(editor.isEnabled())
			editor.setEnabled(false);
	}
	
	public void setViewBg(int which){
		bgId = which;
		bgLayout.setBackgroundResource(
				MemoConstants.EDITOR_BG_IMAGE_IDS[which]);
	}

	public String getContents(){
		return editor.getText().toString();
	}
	
	public String getOldContents(){
		return mOldContents;
	}
	
	public int getViewBgId(){
		return bgId;
	}
	
	public float getTextSize(){
		return editor.getTextSize();
	}
	
	public void setTextSize(int size){
		editor.setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
	}
	
	public PopupImageGrid getColorGrid() {
		return colorGrid;
	}

	private EventListener modelListener = new EventListener() {
		@Override
		public void onEvent(Event event) {
			bindView();
		}
	};
	
	private PopupWidget.ItemClickListener colorGridListener = new PopupWidget.ItemClickListener() {
		@Override
		public void onClicked(PopupWidget widget, View v, int pos) {
			setViewBg(pos);
		}
	};
	
	private View.OnClickListener clickListener = new View.OnClickListener() {
		RotateAnimation anim = new RotateAnimation(8f, -8f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		{
			anim.setDuration(40);
			anim.setRepeatCount(3);
			anim.setRepeatMode(Animation.REVERSE);
		}
		@Override
		public void onClick(View v) {
			
			v.startAnimation(anim);
			if(v == btnBack){
				onPrevious();
			}else if(v == btnForward){
				onForward();
			}else if(v == btnDel){
				onDelete();
			}else if(v == btnSave){
				onSave();
			}else if(v == btnBg){
				onChangeBg(v);
			}
		}
	};
	
	protected void onChangeBg(View v) {
		viewListener.onChangeBg(v);
	}

	private void onForward(){
		viewListener.onForward();
		bgLayout.startAnimation(animRightIn);
		editor.startAnimation(alphaAnim);
		modTime.startAnimation(alphaAnim);
	}

	private void onDelete(){
		viewListener.onDel();
	}
	
	private void onSave() {
		viewListener.onSave();
	}
	
	public void onPrevious(){
		viewListener.onPrevious();
		bgLayout.startAnimation(animLeftIn);
		editor.startAnimation(alphaAnim);
		modTime.startAnimation(alphaAnim);
	}
	
	private TextWatcher textWatcher = new TextWatcher() {
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		public void afterTextChanged(Editable s) {
			btnSave.setEnabled(s.length()>0);
		}
	};

	private GestureDetector.OnGestureListener gestureListener = new GestureDetector.OnGestureListener() {
		@Override public boolean onDown(MotionEvent e) {return false;}
		@Override public void onShowPress(MotionEvent e) {}
		@Override public void onLongPress(MotionEvent e) {}
		
		@Override 
		public boolean onSingleTapUp(MotionEvent e) {
			editor.setEnabled(true);
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY) {
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if(e1.getX() - e2.getX() > 120){
				if(model.hasForward()){
					onForward();
				}
			}else if(e2.getX() - e1.getX() > 120){
				if(model.hasPrevious()){
					onPrevious();
				}
			}
			return false;
		}
	};
	
	private OnItemClickListener sideBarItemClickListener = new OnItemClickListener() {
		
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			int type = (Integer)view.getTag();
			switch (type) {
			case SIDEBAR_ADD:
				Intent intent = ((Activity)App.getContext()).getIntent();
				intent.setAction(Intent.ACTION_INSERT);
				model.setUri(intent.getData());
				model.add();
				toggleSideBar();
				break;
			case SIDEBAR_OUTPUT_TXT:
				backupToSDCard();
				break;
			default:
				break;
			}
        }
		
		private void backupToSDCard() {
			if (!Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				ToastHelper.show("SDCard didn't mounted.");
				return;
			}

			File file = Environment.getExternalStorageDirectory();
			final File dir = new File(file, File.separator + PreferenceHelper.getString(PrefConstants.OUTPUT_PATH, "Memo"));
			if (!dir.exists()) {
				dir.mkdirs();
			}

			int id = model.getId();
			if(id < 0) {
				ToastHelper.show("You must save before do this action.");
				return;
			}
			final String name = "notes_" + id + ".txt";
			final String text = model.getContents();

			if (StringHelper.isNullOrEmpty(text.trim())) {
				ToastHelper.show(getResources().getString(R.string.hint_empty_contents));
			}
			new Handler().post(new Runnable() {
				boolean isCreated = false;
				@Override
				public void run() {
					isCreated = PreferenceHelper.writeFile(dir, name, text);
					EditorView.this.post(new Runnable() {
						@Override
						public void run() {
							if (isCreated) {
								ToastHelper.show(getResources().getString(R.string.hint_backup_in) + dir.getPath()
										+ File.separator + name);
							} else {
								ToastHelper.show(getResources().getString(R.string.hint_backup_failed));
							}

						}
					});
				}
			});
		}
    };

}
