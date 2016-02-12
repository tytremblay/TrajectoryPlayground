package com.team319.web.srx;

import java.net.URI;
import java.util.concurrent.Future;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SrxClient {

	public static void start() throws Exception {

        URI destUri = new URI("ws://localhost:5801/trajectory");

        WebSocketClient client = new WebSocketClient();
        try
        {

            client.start();
            // The socket that receives events
            SrxSocket socket = new SrxSocket();
            // Attempt Connect
            Future<Session> fut = client.connect(socket,destUri);
            // Wait for Connect
            Session session = fut.get();
            // Send a message

            JSONObject message = new JSONObject();
            JSONArray waypoints = new JSONArray();
            JSONObject waypoint1 = new JSONObject();
            waypoint1.put("x", 0.0);
            waypoint1.put("y", 0.0);
            waypoint1.put("z", 0.0);
            JSONObject waypoint2 = new JSONObject();
            waypoint2.put("x", 5.0);
            waypoint2.put("y", 0.0);
            waypoint2.put("z", 0.0);
            JSONObject waypoint3 = new JSONObject();
            waypoint3.put("x", 10.0);
            waypoint3.put("y", 0.0);
            waypoint3.put("z", Math.PI/18);
            waypoints.add(waypoint1);
            waypoints.add(waypoint2);
            waypoints.add(waypoint3);
            message.put("waypoints", waypoints);
            session.getRemote().sendString(message.toJSONString());

        }
        catch (Throwable t)
        {
            t.printStackTrace(System.err);
        }

    }

}
