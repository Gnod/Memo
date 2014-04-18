package com.gnod.memo.views;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.gnod.activity.R;
import com.gnod.memo.activity.App;
import com.gnod.memo.activity.PasswordSetActivity;
import com.gnod.memo.provider.MemoConstants;
import com.gnod.memo.tool.PrefConstants;
import com.gnod.memo.tool.PreferenceHelper;
import com.gnod.memo.ui.PathDialog;
import com.gnod.memo.ui.PopupList;
import com.gnod.memo.ui.SingleChoiceDialog;
import com.gnod.memo.widgets.PopupItemAction;
import com.gnod.memo.widgets.PopupWidget;
import com.gnod.memo.widgets.SideBarItemAction;
import com.gnod.memo.widgets.SingleChoiceItemAction;
import com.umeng.fb.FeedbackAgent;

import java.io.File;

public class ListItemsView extends SideBarView{

//	private static final String TAG = ListItemsView.class.getSimpleName();\
	public static final int SIDEBAR_MENU_THEME = 0;
	public static final int SIDEBAR_MENU_BG = 1;
	public static final int SIDEBAR_MENU_PASSWORD = 2;
	public static final int SIDEBAR_MENU_OUTPUT_PATH = 3;
	public static final int SIDEBAR_MENU_OUTPUT = 4;
	public static final int SIDEBAR_MENU_REPORT = 5;
	public static final int SIDEBAR_MENU_ABOUT = 6;
	public static final int SIDEBAR_MENU_VERSION = 7;
	
	public final int[] THEME_ID = {
			R.drawable.sl_bg_list_title_green, 
			R.drawable.sl_bg_list_title_light_blue, 
			R.drawable.sl_bg_list_title_light_green, 
			R.drawable.sl_bg_list_title_blue,
			R.drawable.sl_bg_list_title_black,
			R.drawable.sl_bg_list_title_grey
		};
		
	public final String[] THEME_NAME = {
		"果壳绿",
		"梦幻蓝",
		"豆瓣青",
		"淡雅蓝",
		"高端黑",
		"文艺灰"
	};

	
	AlphaAnimation alphaAnim = new AlphaAnimation(0, 1);
	{
		alphaAnim.setDuration(400);
		alphaAnim.setStartOffset(200);
	}
//	private AppModel model = null;
	private ViewListener viewListener = null;
	private ImageView addBtn = null;
	private TextView titleView = null;
	public ListView listView = null;
	
	private View listBarBg;
	private SideBarItemAction themeAction;
	private SideBarItemAction pathAction;
	private SideBarItemAction bgAction;
	private SideBarItemAction passwordAction;
	private SideBarItemAction reportAction;
	private SideBarItemAction aboutAction;
    private ImageView menuBtn;


    public static interface ViewListener{
		public void onAddItem(View v);
		public void onPopupMenu(View v);
	}
	
	public ListItemsView(Context context, AttributeSet attrs) {
		super(context, attrs);
//		model = AppModel.getInstance();
	}
	
	public void onActivityResume() {
		if(addBtn != null) {
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					addBtn.startAnimation(alphaAnim);
				}
			}, 200);
		}
	}
	
	public void setViewListener(ViewListener listener){
		this.viewListener = listener;
	}
	
	@Override
	public void initView() {
		sideBar = findViewById(R.id.list_layout_sidebar);
		mainView = findViewById(R.id.list_layout_mainview);
		sideBarList = (ListView)findViewById(R.id.list_layout_sidebar_itemlist);
		listBarBg = findViewById(R.id.list_bar_bg);
        menuBtn = (ImageView) findViewById(R.id.btn_menu);
        addBtn = (ImageView)findViewById(R.id.list_bar_add);
		titleView = (TextView)findViewById(R.id.app_title);
		listView = (ListView)findViewById(R.id.item_list);

        addBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(viewListener != null)
					viewListener.onAddItem(v);
			}
		});

        menuBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                toggleSideBar();
            }
        });
		titleView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				viewListener.onPopupMenu(v);
			}
			
		});
		
//		int listBarBgId = PreferenceHelper.getInt(PrefConstants.THEME_ID, R.drawable.sl_bg_list_title_green);
//		listBarBg.setBackgroundResource(listBarBgId);
		int bgId = PreferenceHelper.getInt(PrefConstants.BG_ID, 0);
		mainView.setBackgroundResource(PrefConstants.BG_IDS[bgId]);
	}

	@Override
	public void initSideBarActions() {
		String themeName = PreferenceHelper.getString(PrefConstants.THEME_NAME, "果壳绿");
		String path = Environment.getExternalStorageDirectory().getPath() + File.separator + 
						PreferenceHelper.getString(PrefConstants.OUTPUT_PATH, "Memo");
		themeAction = new SideBarItemAction(getResources().getString(R.string.menu_theme), themeName, SIDEBAR_MENU_THEME);
		bgAction = new SideBarItemAction(getResources().getString(R.string.menu_bg), SIDEBAR_MENU_BG);
		passwordAction = new SideBarItemAction(getResources().getString(R.string.menu_password), SIDEBAR_MENU_PASSWORD);
		pathAction = new SideBarItemAction("导出路径", path, SIDEBAR_MENU_OUTPUT_PATH);
		reportAction = new SideBarItemAction("意见反馈", SIDEBAR_MENU_REPORT);
		aboutAction = new SideBarItemAction("关于", SIDEBAR_MENU_ABOUT);
		
//		sideBarActions.add(themeAction);
//		sideBarActions.add(bgAction);
//		sideBarActions.add(passwordAction);
		sideBarActions.add(pathAction);
		sideBarActions.add(reportAction);
//		sideBarActions.add(aboutAction);
		PackageInfo info = null;
		try {
			info = App.getContext().getPackageManager().getPackageInfo(App.getContext().getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if(info != null)
			sideBarActions.add(new SideBarItemAction("v" + info.versionName, SIDEBAR_MENU_VERSION));
	

	}

	
	public void setListAdapter(ListAdapter adapter){
		listView.setAdapter(adapter);
	}
	
	public ListAdapter getListAdapter(){
		return listView.getAdapter();
	}

	public TextView getTitleView() {
		return titleView;
	}
	
	@Override
	public OnItemClickListener getSideBarClickListener() {
		return sideBarItemClickListener;
	}

	private OnItemClickListener sideBarItemClickListener = new OnItemClickListener() {
		
		public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
			int type = (Integer)view.getTag();
			switch (type) {
			case SIDEBAR_MENU_THEME:
				int themeId = PreferenceHelper.getInt(PrefConstants.THEME_ID, THEME_ID[0]);
				
				SingleChoiceDialog dialog = new SingleChoiceDialog(getContext());
				dialog.setSelectedListener(themeItemClickListener);
				
				for(int i = 0; i < THEME_ID.length; i ++) {
					dialog.addAction(new SingleChoiceItemAction(
							THEME_NAME[i], 
							getResources().getDrawable(THEME_ID[i]), 
							THEME_ID[i], 
							themeId == THEME_ID[i]));
				}
				dialog.show();
				break;
				
			case SIDEBAR_MENU_BG:
				String[] array = {
					"The Lord", "Gandalf", "Aragorn", "Mordor"	
				};
				PopupList menu = new PopupList(App.getContext());
				menu.setWidth(LayoutParams.MATCH_PARENT);
				for(String s:array){
					menu.addMenuItemAction(new PopupItemAction(null, s));
				}
				menu.setOnItemClickListener(bgListener);
				menu.show(view);
				break;
			case SIDEBAR_MENU_PASSWORD:
				Intent intent = new Intent();
				intent.setClass(getContext(), PasswordSetActivity.class);					
				getContext().startActivity(intent);
				break;
			case SIDEBAR_MENU_OUTPUT_PATH:
				PathDialog path = new PathDialog(getContext());
				path.setPathListener(pathListener);
				path.show();
				break;
			case SIDEBAR_MENU_REPORT:
                FeedbackAgent agent = new FeedbackAgent(getContext());
                agent.startFeedbackActivity();
				break;
//			case SIDEBAR_MENU_ABOUT:
//				Intent about = new Intent();
//				about.setClass(getContext(), AboutActivity.class);
//				getContext().startActivity(about);
//				break;
//			case SIDEBAR_MENU_VERSION:
//				Intent versionIntent = new Intent();
//				versionIntent.setClass(getContext(), GuideActivity.class);
//				getContext().startActivity(versionIntent);
//				break;
			default:
				break;
			}
        }
    };
    
    PopupWidget.ItemClickListener bgListener = new PopupWidget.ItemClickListener() {
		
    	@Override
		public void onClicked(PopupWidget widget, View v, int pos) {
			mainView.setBackgroundResource(PrefConstants.BG_IDS[pos]);
			SharedPreferences pref = getContext().getSharedPreferences(
					MemoConstants.PREFERENCES_NAME, Context.MODE_APPEND);
			final Editor editor = pref.edit();
			editor.putInt(PrefConstants.BG_ID, pos);
			editor.commit();
		}
    };
    
    private SingleChoiceDialog.OnSelectedListener themeItemClickListener = new SingleChoiceDialog.OnSelectedListener() {
		@Override
		public void onItemClick(View v, int position, CharSequence value) {
			int id = (Integer)v.getTag();
			listBarBg.setBackgroundResource(id);
			
			SharedPreferences pref = getContext().getSharedPreferences(
					MemoConstants.PREFERENCES_NAME, Context.MODE_APPEND);
			final Editor editor = pref.edit();
			editor.putInt(PrefConstants.THEME_ID, id);
			editor.putString(PrefConstants.THEME_NAME, value.toString());
			editor.commit();
			
			themeAction.setSubTitle(value);
			sideBarAdapter.notifyDataSetChanged();
		}
	};
	
	private PathDialog.PathListener pathListener = new PathDialog.PathListener() {
		@Override
		public void onOkClicked(View v, String path) {
			pathAction.setSubTitle(path);
			sideBarAdapter.notifyDataSetChanged();
		}
	};

}
