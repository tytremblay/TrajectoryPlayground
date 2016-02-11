package com.team319.web;

import com.team254.lib.util.TaskQueue;
import com.team319.web.trajectory.TrajectoryServlet;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.servlets.CrossOriginFilter;

import java.util.ArrayList;
import java.util.EnumSet;

import javax.servlet.DispatcherType;

public class WebServer {
    private static Server server;
    private static ArrayList<UpdatableWebSocketAdapter> updateStreams = new ArrayList<UpdatableWebSocketAdapter>();
    private static TaskQueue streamUpdate = new TaskQueue(200);

    public static void startServer() {
        if (server != null) {
            return;
        }

        server = new Server(5801);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        FilterHolder cors = context.addFilter(CrossOriginFilter.class,"/*",EnumSet.of(DispatcherType.REQUEST));
        cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, "*");
        cors.setInitParameter(CrossOriginFilter.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, "*");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "GET,POST,HEAD");
        cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin");

        ServletHolder commandHolder = new ServletHolder("trajectory", new TrajectoryServlet());
        context.addServlet(commandHolder, "/trajectory");

        Thread serverThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server.start();
                    server.join();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        serverThread.setPriority(Thread.MIN_PRIORITY);
        serverThread.start();
        streamUpdate.start();
    }

    public static void registerSocket(UpdatableWebSocketAdapter s) {
    	updateStreams.add(s);
    }

    public static void unregisterSocket(UpdatableWebSocketAdapter s) {
    	updateStreams.remove(s);
    }


    public static Runnable updateRunner = new Runnable() {
        public void run() {
            for (int i = 0; i < updateStreams.size(); ++i) {
                UpdatableWebSocketAdapter s = updateStreams.get(i);
                if (s != null && s.isConnected() && !s.canBeUpdated()) {
                } else if ((s == null || !s.isConnected() || !s.update()) && i < updateStreams.size()) {
                    updateStreams.remove(i);
                }
            }
        }
    };

    public static void updateAll() {
        boolean runUpdate = false;
        for (UpdatableWebSocketAdapter s : updateStreams) {
            runUpdate = (s != null && s.canBeUpdated());
            if (runUpdate) {
                break;
            }
        }
        if (runUpdate) {
            streamUpdate.addTask(updateRunner);
        }
    }
}
