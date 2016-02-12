package com.team319.web;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;

import com.team254.lib.trajectory.TrajectoryGenerator;

public abstract class UpdatableWebSocketAdapter extends WebSocketAdapter {
	private boolean running = true;

	public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        WebServer.unregisterSocket(this);
    }

    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);
        WebServer.registerSocket(this);

        this.initialize();
    }

    public void onWebSocketError(Throwable cause) {
        System.err.println("WebSocket Error" + cause);
        WebServer.unregisterSocket(this);
    }

	public boolean canBeUpdated() {
        return running;
    }

	public abstract boolean update();

	public abstract void initialize();

}
