package com.gnod.memo.events;

public interface Dispatcher {

	public void addEventListener(String type, EventListener listener);
	public void removeEventListener(String type, EventListener listener);
	public boolean hasEventListener(String type, EventListener listener);
	public void dispatchEvent(Event event);
	
}
