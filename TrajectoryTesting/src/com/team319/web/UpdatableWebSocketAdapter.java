package com.team319.web;

import org.eclipse.jetty.websocket.api.WebSocketAdapter;

public abstract class UpdatableWebSocketAdapter extends WebSocketAdapter {
	private boolean running = true;
	
	public boolean canBeUpdated() {
        return running;
    }
	
	public abstract boolean update();
}
