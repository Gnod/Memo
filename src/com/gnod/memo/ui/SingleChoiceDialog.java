package com.gnod.memo.ui;

import java.util.ArrayList;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.gnod.activity.R;
import com.gnod.memo.widgets.SingleChoiceItemAction;

public class SingleChoiceDialog extends Dialog {

	private ListView listView;

	private ArrayList<SingleChoiceItemAction> actions = new ArrayList<SingleChoiceItemAction>();
	private int preSelectedPos = -1;
	private boolean mIsDirty = false;
	private OnSelectedListener selectedListener;
	
	public static interface OnSelectedListener {
		public void onItemClick(View view, int position, CharSequence name);
	}
	public void setSelectedListener(OnSelectedListener listener) {
		selectedListener = listener;
	}
	public SingleChoiceDialog(Context context, int theme) {
		super(context, theme);
	}
	
	public SingleChoiceDialog(Context context) {
		this(context, R.style.theme_wrap_contents);
	}
	
	public void addAction(SingleChoiceItemAction action){
		if(action == null)
			return;
		actions.add(action);
		mIsDirty  = true;
	}
	
	public void clearAllSingleChoiceItemAction(){
		if(!actions.isEmpty()) {
			actions.clear();
			mIsDirty = true;
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_single_choice);
		
		bindView();
	}

	private void bindView() {
		listView = (ListView)findViewById(R.id.dialog_single_choice_list);
		
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(clickListener);
		
	}

	private BaseAdapter listAdapter = new BaseAdapter() {

		@Override
		public int getCount() {
			return actions.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View view, ViewGroup parent) {
			
			if(view == null) {
				view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_single_choice_item, listView, false);
			}
			ImageView icon = (ImageView) view.findViewById(R.id.dialog_single_choice_item_icon);
			TextView title = (TextView)view.findViewById(R.id.dialog_single_choice_item_text);
			RadioButton radioBtn = (RadioButton)view.findViewById(R.id.dialog_single_choice_item_radio_btn);
			
			SingleChoiceItemAction action = actions.get(position);
			icon.setBackgroundDrawable(action.getIcon());
			title.setText(action.getTitle());
			if(action.isSelected() && preSelectedPos < 0) {
				preSelectedPos = position;
			}
			radioBtn.setChecked(action.isSelected());
			view.setTag(action.getTag());
			return view;
		}
		
	};
	
	private OnItemClickListener clickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if(preSelectedPos >= 0){
				actions.get(preSelectedPos).setSelected(false);
			}
			actions.get(position).setSelected(true);
			preSelectedPos = position;
			CharSequence name = actions.get(position).getTitle();
			selectedListener.onItemClick(view, position, name);

			listAdapter.notifyDataSetChanged();
			
		}
	};
	
}
