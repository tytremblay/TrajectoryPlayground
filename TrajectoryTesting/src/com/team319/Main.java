package com.team319;

import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.PathGenerator;
import com.team254.lib.trajectory.TrajectoryGenerator;
import com.team254.lib.trajectory.WaypointSequence;
import com.team254.lib.trajectory.io.TextFileSerializer;
import com.team319.lib.PathWriter;
import com.team319.lib.SRXTranslator;
import com.team319.trajectory.CombinedSrxMotionProfile;
import com.team319.ui.PathViewer;
import com.team319.ui.Plotter;
import com.team319.web.WebServer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.SwingUtilities;

import org.json.simple.parser.ParseException;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

/**
 *
 * @author Jared341
 */
public class Main {

	public static void main(String[] args) {

		//TrajectoryGenerator.Config config = new TrajectoryGenerator.Config();
		CombinedSrxMotionProfile.Config config = new CombinedSrxMotionProfile.Config();
		config.dt = .01;
		config.max_acc = 10.0;
		config.max_jerk = 60.0;
		config.max_vel = 15.0;
		config.wheelbase_width = 23.25 / 12;
		config.wheel_dia = 5.875;
		config.scale_factor = 2.778;
		config.direction = -1;
		
		{
			// Path name must be a valid Java class name.
			config.dt = .01;
			config.max_acc = 7.0;
			config.max_jerk = 50.0;
			config.max_vel = 10.0;


			// Description of this auto mode path.
			// Remember that this is for the GO LEFT CASE!
			WaypointSequence p = new WaypointSequence(10);
			p.addWaypoint(new WaypointSequence.Waypoint(0, 0, 0));
			p.addWaypoint(new WaypointSequence.Waypoint(-3, -1, .8));

			Path path = PathGenerator.makePath(p, config, config.wheelbase_width, PathWriter.PATH_NAME);

			SRXTranslator srxt = new SRXTranslator();
			CombinedSrxMotionProfile combined = srxt.getSRXProfileFromChezyPath(path, config);


			combined.leftProfile.toJsonString();


			if(!PathWriter.writePath(path, combined)){
				System.err.println("A path could not be written!!!!");
				System.exit(1);
			}else{
				PathViewer.showPath(path);
			}
		}
	}
}
