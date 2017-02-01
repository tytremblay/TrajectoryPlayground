package com.team319.web.srx;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.team319.trajectory.SrxTrajectory;

public class SrxSocket extends WebSocketAdapter {

	JSONObject latestResult = null;

    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        //WebServer.unregisterSocket(this);
    }

    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);
        //WebServer.registerSocket(this);
    }

    public void onWebSocketError(Throwable cause) {
        System.err.println("WebSocket Error" + cause);
        //WebServer.unregisterSocket(this);
    }

    public void onWebSocketText(String message) {

    	if(message.equalsIgnoreCase("pong")){

    		new Thread(new Runnable() {

				@Override
				public void run() {
		    		//TODO: MWT ADD DELAY HERE
					getRemote().sendStringByFuture("ping");

				}
			}).start();

    	}

        JSONParser parser = new JSONParser();
        JSONObject obj;
        try {
            obj = (JSONObject) parser.parse(message);
            SrxTrajectory profile = new SrxTrajectory(obj);
            profile.toString();
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

    }

    @Override
    public void onWebSocketBinary(byte[] imageData, int offset, int len) {
       //not supported
    }

}
