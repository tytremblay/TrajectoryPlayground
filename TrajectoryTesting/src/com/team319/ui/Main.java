package com.team319.ui;

import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.PathGenerator;
import com.team254.lib.trajectory.TrajectoryGenerator;
import com.team254.lib.trajectory.WaypointSequence;
import com.team319.lib.PathWriter;
import com.team319.lib.SRXTranslator;

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
			final String path_name = "TyPath";

			// Description of this auto mode path.
			// Remember that this is for the GO LEFT CASE!
			WaypointSequence p = new WaypointSequence(10);
			p.addWaypoint(new WaypointSequence.Waypoint(0, 0, 0));
			p.addWaypoint(new WaypointSequence.Waypoint(5, 0, 0));
			p.addWaypoint(new WaypointSequence.Waypoint(8, 1, 0));
			p.addWaypoint(new WaypointSequence.Waypoint(10.5, 1, 0));

			Path path = PathGenerator.makePath(p, config, kWheelbaseWidth, path_name);

			SRXTranslator srxt = new SRXTranslator();
			SRXTranslator.CombinedSRXMotionProfile combined = srxt.getSRXProfileFromChezyPath(path, 4, 2.54);


			if(PathWriter.writePaths(path, combined)){
				PathViewer.view(path);
			}else{
				System.err.println("A path could not be written!!!!");
				System.exit(1);
			}
		}
	}
}
