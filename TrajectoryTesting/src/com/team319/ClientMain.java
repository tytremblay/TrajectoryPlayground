package com.team319;

import java.net.URI;
import java.util.concurrent.Future;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.team319.web.srx.SrxClient;

public class ClientMain {

	public static void main(String[] args) throws Exception {

        SrxClient.start();
    }



}
