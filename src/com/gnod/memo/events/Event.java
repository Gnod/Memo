package com.gnod.memo.events;

public interface Event {

	public String getType();
	public Object getSource();
	public void setSource(Object o);
}
