package com.team319.web.trajectory;

import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.PathGenerator;
import com.team254.lib.trajectory.TrajectoryGenerator;
import com.team254.lib.trajectory.WaypointSequence;
import com.team254.lib.trajectory.WaypointSequence.Waypoint;
import com.team319.lib.PathWriter;
import com.team319.lib.SRXTranslator;
import com.team319.lib.SRXTranslator.CombinedSRXMotionProfile;
import com.team319.lib.SRXTranslator.SRXMotionProfile;
import com.team319.web.UpdatableWebSocketAdapter;
import com.team319.web.WebServer;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketAdapter;
import org.eclipse.jetty.websocket.api.WebSocketException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class TrajectorySocket extends UpdatableWebSocketAdapter {

	JSONObject latestResult = null;

	final double kWheelbaseWidth = 23.25 / 12;
	TrajectoryGenerator.Config config;

    public void onWebSocketClose(int statusCode, String reason) {
        super.onWebSocketClose(statusCode, reason);
        WebServer.unregisterSocket(this);
    }

    public void onWebSocketConnect(Session session) {
        super.onWebSocketConnect(session);
        WebServer.registerSocket(this);

        config = new TrajectoryGenerator.Config();
    	config.dt = .01;
    	config.max_acc = 10.0;
    	config.max_jerk = 60.0;
    	config.max_vel = 15.0;
    }

    public void onWebSocketError(Throwable cause) {
        System.err.println("WebSocket Error" + cause);
        WebServer.unregisterSocket(this);
    }

    public void onWebSocketText(String message) {
        JSONParser parser = new JSONParser();
        JSONObject obj;
        try {
            obj = (JSONObject) parser.parse(message);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }

		JSONArray points = (JSONArray) obj.get("waypoints");


		if (points != null) {
			WaypointSequence waypoints = new WaypointSequence(points.size());
			for (int i = 0; i < points.size(); i++) {
				JSONObject singlePoint = (JSONObject) points.get(i);
				Waypoint waypoint = new Waypoint((double)singlePoint.get("x"), (double)singlePoint.get("y"), (double)singlePoint.get("z"));
				waypoints.addWaypoint(waypoint);
			}

			Path path = PathGenerator.makePath(waypoints, config, kWheelbaseWidth, PathWriter.PATH_NAME);

			SRXTranslator srxt = new SRXTranslator();
			CombinedSRXMotionProfile combined = srxt.getSRXProfileFromChezyPath(path, 5.875, 2.778);

			JSONObject result = new JSONObject();
			result.put("left", combined.leftProfile.toJson());
			result.put("right", combined.rightProfile.toJson());

			latestResult = result;

			update();
		}



    }

    @Override
    public void onWebSocketBinary(byte[] imageData, int offset, int len) {
       //not supported
    }

    @Override
    public boolean update() {
        if (!isConnected()) {
            return false;
        }else if (!canBeUpdated()) {
            return true;
        }



        getRemote().sendStringByFuture(latestResult.toJSONString());
        return true;
    }

}
