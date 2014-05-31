package com.gnod.memo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

import com.gnod.activity.R;
import com.gnod.memo.adapter.MemoCursorAdapter;
import com.gnod.memo.command.Command;
import com.gnod.memo.command.DeleteCommand;
import com.gnod.memo.command.StartActivityCommand;
import com.gnod.memo.handler.MemoHandler;
import com.gnod.memo.models.AppModel;
import com.gnod.memo.provider.MemoConstants;
import com.gnod.memo.tool.PrefConstants;
import com.gnod.memo.tool.PreferenceHelper;
import com.gnod.memo.tool.ToastHelper;
import com.gnod.memo.ui.PathDialog;
import com.gnod.memo.ui.PopupList;
import com.gnod.memo.ui.PopupOptMenu;
import com.gnod.memo.views.swipelistview.BaseSwipeListViewListener;
import com.gnod.memo.views.swipelistview.SwipeListView;
import com.gnod.memo.widgets.Item.GestureListener;
import com.gnod.memo.widgets.PopupItemAction;
import com.gnod.memo.widgets.PopupWidget;
import com.umeng.fb.FeedbackAgent;

import java.net.URLEncoder;

public class ListActivity extends Activity {

    private static final String TAG = ListActivity.class.getSimpleName();
    private static final int MENU_UNDO = 0;
    private static final int MENU_REDO = 1;

    public static final int SIDEBAR_MENU_BG = 0;
    public static final int SIDEBAR_MENU_OUTPUT_PATH = 1;
    public static final int SIDEBAR_MENU_REPORT = 2;

    private AppModel model = null;
    private MemoHandler handler;
    private SwipeListView swipeListView;
    private boolean deleteMark = false;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private View mDrawerListLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.setContext(this);
        model = AppModel.getInstance();

        setContentView(R.layout.activity_list);
        String[] mDrawerListTitles = getResources().getStringArray(R.array.drawer_list);
        PackageInfo info = null;
        try {
            info = App.getContext().getPackageManager().getPackageInfo(App.getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (info != null) {
            mDrawerListTitles[mDrawerListTitles.length - 1] = "v" + info.versionName;
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.icon_menu,
                R.string.drawer_open, R.string.drawer_close) {

            public void onDrawerClosed(View view) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);


        mDrawerListLayout = findViewById(R.id.left_drawer);
        mDrawerList = (ListView) findViewById(R.id.left_drawer_list);
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mDrawerListTitles));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onDrawerSelected(position);
            }
        });

        swipeListView = (SwipeListView) findViewById(R.id.item_list);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        handler = MemoHandler.getInstance();
        handler.registerStackListener();
        initList();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isDrawerOpen = mDrawerLayout.isDrawerOpen(mDrawerListLayout);
        menu.findItem(R.id.action_new).setVisible(!isDrawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_new:
                onAddItem();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        App.setContext(this);
//		view.onActivityResume();
    }

    private void onDrawerSelected(int position) {

        switch (position) {
            case SIDEBAR_MENU_BG:
                String[] array = {
                        "Lord", "Gandalf", "Aragorn", "Mordor"
                };
                PopupList menu = new PopupList(App.getContext());
                menu.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                for (String s : array) {
                    menu.addMenuItemAction(new PopupItemAction(null, s));
                }
                menu.setOnItemClickListener(bgListener);
                menu.show(mDrawerLayout);
                break;
            case SIDEBAR_MENU_OUTPUT_PATH:
                PathDialog path = new PathDialog(this);
                path.setPathListener(pathListener);
                path.show();
                break;
            case SIDEBAR_MENU_REPORT:
                FeedbackAgent agent = new FeedbackAgent(this);
                agent.startFeedbackActivity();
                break;
            default:
                break;
        }

        mDrawerList.setItemChecked(position, false);
    }

    private void initList() {
        Intent intent = getIntent();
        model.setUri(MemoConstants.CONTENT_URI);
        if (intent.getData() == null) {
            intent.setData(MemoConstants.CONTENT_URI);
        }
        Cursor cursor = model.getCursor();

        String[] from = new String[]{
                MemoConstants.COLUMN_NAME_NOTE_TITLE,
                MemoConstants.COLUMN_NAME_NOTE_TIME};
        int[] to = new int[]{
                R.id.list_item_title,
                R.id.list_item_time};

        swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onClickFrontView(int position) {
                super.onClickFrontView(position);
                App.setPosition(position);
                if (swipeListView.isOpen(position)) {
                    swipeListView.closeAnimate(position);
                } else {
                    editItem(position);
                }
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
                super.onClosed(position, fromRight);
                if (deleteMark) {
                    deleteItem(App.getPosition());
                    deleteMark = false;
                }
            }
        });
        MemoCursorAdapter adapter = new MemoCursorAdapter(App.getContext(),
                R.layout.layout_listitem, cursor, from, to);
        adapter.setItemMenuListener(new MemoCursorAdapter.MemoItemMenuListener() {
            @Override
            public void onDelete() {
//                AlertDialog.Builder builder = new AlertDialog.Builder(ListViewActivity.this);
//                builder.setMessage("确定删除？");
//                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    }
//                });
//                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        deleteItem();
//                    }
//                });
//                builder.show();
                if (!deleteMark) {
                    deleteMark = true;
                    swipeListView.closeAnimate(App.getPosition());
                }

            }

            @Override
            public void onShare(View view) {
                share(view);
            }
        });
        swipeListView.setAdapter(adapter);


        int bgId = PreferenceHelper.getInt(PrefConstants.BG_ID, 1);
        mDrawerLayout.setBackgroundResource(PrefConstants.BG_IDS[bgId]);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.dispose();
        App.getCommandStack().dispose();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {

            boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerListLayout);
            if (drawerOpen) {
                mDrawerLayout.closeDrawer(mDrawerListLayout);
            } else {
                mDrawerLayout.openDrawer(mDrawerListLayout);
            }
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    public void onBackPressed() {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerListLayout);
        if (drawerOpen) {
            mDrawerLayout.closeDrawer(mDrawerListLayout);
        } else {
            super.onBackPressed();
        }
    }

    public void onPopMenu() {
        if (!handler.canRedo && !handler.canUndo) {
            return;
        }
        PopupOptMenu menu = new PopupOptMenu(getApplicationContext());
        if (handler.canUndo) {
            menu.addMenuItemAction(new PopupItemAction(null, getResources().getString(R.string.menu_undo), MENU_UNDO));
        }
        if (handler.canRedo) {
            menu.addMenuItemAction(new PopupItemAction(null, getResources().getString(R.string.menu_redo), MENU_REDO));
        }
        menu.setOnItemClickListener(menuListener);
//        menu.show(view.getTitleView(), true);
    }

    private PopupWidget.ItemClickListener menuListener = new PopupWidget.ItemClickListener() {
        @Override
        public void onClicked(PopupWidget widget, View v, int pos) {
            int type = (Integer) v.getTag();
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
    private void toggleUri(long id) {
        Uri noteUri = ContentUris.withAppendedId(
                getIntent().getData(),
                id);
        model.setUri(noteUri);
    }

    public void editItem(int position) {
        toggleUri(swipeListView.getAdapter().getItemId(position));
        boolean moved = model.getCursor().moveToPosition(App.getPosition());
        if (!moved) {
            Log.e(TAG, "invalid cursor position " + App.getPosition());
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_EDIT);
        intent.setData(getIntent().getData());
        intent.setClass(ListActivity.this, EditorActivity.class);
        Command command = new StartActivityCommand(intent);
        handler.setCurrentCommand(command);
        handler.invoke();
        overridePendingTransition(R.anim.common_open_enter, R.anim.common_open_exit);
    }

    public void deleteItem(int position) {
        toggleUri(swipeListView.getAdapter().getItemId(position));
        Command command = new DeleteCommand(getContentResolver(),
                model.getUri());
        handler.setCurrentCommand(command);
        handler.invoke();
    }

    public void onAddItem() {
        model.add();
        model.setUri(getIntent().getData());
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_INSERT);
        intent.setData(getIntent().getData());
        intent.setClass(ListActivity.this, EditorActivity.class);
        Command command = new StartActivityCommand(intent);
        handler.setCurrentCommand(command);
        handler.invoke();
        overridePendingTransition(R.anim.common_open_enter, R.anim.common_open_exit);
    }

    private GestureListener gestureListener = new GestureListener() {
        @Override
        public boolean onSingleTapUp(View v) {
            editItem(App.getPosition());
            return false;
        }

        @Override
        public void onScrollLeftUp(View v) {

            AlertDialog.Builder builder = new AlertDialog.Builder(ListActivity.this);
            builder.setMessage("确定删除？");
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteItem(App.getPosition());
                }
            });
            builder.show();
        }

        @Override
        public void onScrollRightUp(View v) {
            share(v);
        }
    };

    private void share(View v) {
//        ToastHelper.show(view.getResources().getString(R.string.share));
        toggleUri(swipeListView.getAdapter().getItemId(App.getPosition()));
        boolean moved = model.getCursor().moveToPosition(App.getPosition());
        if (!moved) {
            Log.e(TAG, "invalid cursor position " + App.getPosition());
            return;
        }
        String[] array = getResources().getStringArray(R.array.share_type);
        PopupList menu = new PopupList(App.getContext());
        menu.setWidth(LayoutParams.MATCH_PARENT);
        for (String s : array) {
            menu.addMenuItemAction(new PopupItemAction(null, s));
        }
        menu.setOnItemClickListener(shareListener);
        menu.show(v);
    }

    PopupWidget.ItemClickListener shareListener = new PopupWidget.ItemClickListener() {
        private final int SHARE_VIA_SMS = 0;
        private final int SHARE_VIA_EMAIL = 1;
        private final int SHARE_VIA_WEIBO = 2;

        @Override
        public void onClicked(PopupWidget widget, View v, int pos) {
            String contents = model.getContents();
            if (contents.trim().length() == 0)
                ToastHelper.show(getResources().getString(R.string.hint_empty_contents), true);
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
                ToastHelper.show(getResources().getString(R.string.hint_email));
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
                ToastHelper.show(getResources().getString(R.string.hint_weibo));
            }
        }
    };

    PopupWidget.ItemClickListener bgListener = new PopupWidget.ItemClickListener() {

        @Override
        public void onClicked(PopupWidget widget, View v, int pos) {
            mDrawerLayout.setBackgroundResource(PrefConstants.BG_IDS[pos]);
            SharedPreferences pref = getSharedPreferences(
                    MemoConstants.PREFERENCES_NAME, Context.MODE_APPEND);
            final SharedPreferences.Editor editor = pref.edit();
            editor.putInt(PrefConstants.BG_ID, pos);
            editor.commit();
        }
    };

    private PathDialog.PathListener pathListener = new PathDialog.PathListener() {
        @Override
        public void onOkClicked(View v, String path) {
            // Todo
        }
    };
}
