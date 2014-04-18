package com.gnod.memo.command;

import java.util.EventObject;

public interface StackListener {

	void commandStackChanged(EventObject event, Command command);
}
