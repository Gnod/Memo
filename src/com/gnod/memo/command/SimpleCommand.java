package com.gnod.memo.command;

import android.net.Uri;

public abstract class SimpleCommand extends Command {

	private Uri mUri = null;

	
	public SimpleCommand(String label, Uri uri){
		super(label);
		this.mUri = uri;
	}
	
	public void execute(){
		try{
			preExecute();
			doExecute();
			postExecute();
		}catch(Exception e){
			handleException(e);
		}
		
	}
	
	/**
	 * child class can override this method for any pre action.
	 */
	protected void preExecute() {
	}
	
	/**
	 * child class should override this method for the real action.
	 */
	protected abstract void doExecute();
	
	/**
	 * child class can override this method for any post action. NOTE: if
	 * preExecute() or doExecute() throw exception, then this method will not be
	 * called.
	 */
	protected void postExecute() {
	}
	
	/**
	 * child class can override it to handle execute exception.
	 * 
	 * @param e
	 */
	protected void handleException(Exception e) {
		e.printStackTrace();
	}
	
	public Uri getUri() {
		return mUri;
	}

	public void setUri(Uri uri) {
		this.mUri = uri;
	}
	
	
}
