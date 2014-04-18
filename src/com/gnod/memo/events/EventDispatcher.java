package com.gnod.memo.events;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import android.util.Log;

public class EventDispatcher implements Dispatcher {

	private static final String TAG = EventDispatcher.class.getSimpleName();
	
	private Dispatcher target = null;
	private HashMap<String, CopyOnWriteArrayList<EventListener>> listenerMap = null;
	
	public EventDispatcher(){
		this(null);
	}
	
	public EventDispatcher(Dispatcher target){
		listenerMap = new HashMap<String, CopyOnWriteArrayList<EventListener>>();
		this.target = (target == null)? this : target;
	}
	
	@Override
	public void addEventListener(String type, EventListener listener) {
		synchronized(listenerMap){
			CopyOnWriteArrayList<EventListener> list = listenerMap.get(type);
			if(list == null){
				list = new CopyOnWriteArrayList<EventListener>();
				listenerMap.put(type, list);
			}
			list.add(listener);
		}
	}

	@Override
	public void removeEventListener(String type, EventListener listener) {
		synchronized(listenerMap){
			CopyOnWriteArrayList<EventListener> list = listenerMap.get(type);
			if(list == null)
				return;
			list.remove(listener);
			if(list.size() == 0)
				listenerMap.remove(list);
		}
	}

	@Override
	public boolean hasEventListener(String type, EventListener listener) {
		synchronized(listenerMap){
			CopyOnWriteArrayList<EventListener> list = listenerMap.get(type);
			if(list == null)
				return false;
			return list.contains(listener);
		}
	}

	@Override
	public void dispatchEvent(Event event) {
		if(event == null){
			Log.e(TAG, "Can't dispatch null events.");
			return;
		}
		String type = event.getType();
		event.setSource(target);
		CopyOnWriteArrayList<EventListener> list = null;
		synchronized(listenerMap){
			list = listenerMap.get(type);
		}
		if(list == null)
			return;
		for(EventListener l: list){
			l.onEvent(event);
		}
	}

}
