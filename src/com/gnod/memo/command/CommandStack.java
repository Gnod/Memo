package com.gnod.memo.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.Stack;

public class CommandStack {

	private Stack redoable = new Stack();
	private Stack undoable = new Stack();
	protected List listeners = new ArrayList();
	private int undoLimit = 0;

	public boolean canRedo() {
		return !redoable.isEmpty();
	}

	public boolean canUndo() {
		if (undoable.size() == 0) {
			return false;
		}
		return ((Command) undoable.lastElement()).canUndo();
	}

	public void execute(Command command) {
		if (command == null || !command.canExecute())
			return;
		flushRedo();
		try {
			command.execute();
			if (getUndoLimit() > 0) {
				while (undoable.size() >= getUndoLimit()) {
					((Command) undoable.remove(0)).dispose();
				}
			}
			if (command.canUndo()) {
				undoable.push(command);
			}
		} finally {
			notifyListeners(command);
		}
	}

	private void flushRedo() {
		while (!redoable.isEmpty())
			((Command) redoable.pop()).dispose();
	}

	public void redo() {
		if (!canRedo())
			return;
		synchronized (CommandStack.class) {
			Command command = (Command) redoable.pop();
			try {
				command.redo();
				undoable.push(command);
			} finally {
				notifyListeners(command);
			}
		}
	}

	public void undo() {
		if (!canUndo())
			return;
		synchronized (CommandStack.class) {
			Command command = (Command) undoable.pop();
			try {
				command.undo();
				redoable.push(command);
			} finally {
				notifyListeners(command);
			}
		}
	}

	public void flush() {
		flushRedo();
		flushUndo();
		notifyListeners(null);
	}

	private void flushUndo() {
		while (!undoable.isEmpty())
			((Command) undoable.pop()).dispose();
	}

	public void addListener(StackListener listener) {
		listeners.add(listener);
	}

	public void removeListener(StackListener listener) {
		listeners.remove(listener);
	}

	protected void notifyListeners(Command command) {
		EventObject event = new EventObject(this);
		for (int i = 0; i < listeners.size(); i++)
			((StackListener) listeners.get(i)).commandStackChanged(
					event, command);
	}

	public void dispose() {
		flushRedo();
		flushUndo();
	}

	private int getUndoLimit() {
		return undoLimit;
	}

	public void setUndoLimit(int undoLimit) {
		this.undoLimit = undoLimit;
	}
}
