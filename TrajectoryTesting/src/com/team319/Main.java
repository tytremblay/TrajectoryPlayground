package com.team319;

import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.PathGenerator;
import com.team254.lib.trajectory.SRXTranslator;
import com.team254.lib.trajectory.SRXTranslator.CombinedSRXMotionProfile;
import com.team254.lib.trajectory.SRXTranslator.SRXMotionProfile;
import com.team254.lib.trajectory.TrajectoryGenerator;
import com.team254.lib.trajectory.WaypointSequence;
import com.team254.lib.trajectory.io.Plotter;
import com.team254.lib.trajectory.io.TextFileSerializer;
import com.team319.lib.PathWriter;
import com.team319.ui.PathViewer;

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

		TrajectoryGenerator.Config config = new TrajectoryGenerator.Config();
		config.dt = .01;
		config.max_acc = 10.0;
		config.max_jerk = 60.0;
		config.max_vel = 15.0;

		final double kWheelbaseWidth = 25.5 / 12;
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
			p.addWaypoint(new WaypointSequence.Waypoint(5, 0, 0));
			p.addWaypoint(new WaypointSequence.Waypoint(8, 1, 0));
			p.addWaypoint(new WaypointSequence.Waypoint(10.5, 1, 0));

			Path path = PathGenerator.makePath(p, config, kWheelbaseWidth, PathWriter.PATH_NAME);

			SRXTranslator srxt = new SRXTranslator();
			CombinedSRXMotionProfile combined = srxt.getSRXProfileFromChezyPath(path, 4, 2.54);


			if(!PathWriter.writePath(path, combined)){
				System.err.println("A path could not be written!!!!");
				System.exit(1);
			}else{
				PathViewer.showPath(path);
			}
		}
	}
}
