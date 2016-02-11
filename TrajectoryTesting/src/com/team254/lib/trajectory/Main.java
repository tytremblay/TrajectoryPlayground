package com.team254.lib.trajectory;

import com.team254.lib.trajectory.SRXTranslator.CombinedSRXMotionProfile;
import com.team254.lib.trajectory.SRXTranslator.SRXMotionProfile;
import com.team254.lib.trajectory.io.Plotter;
import com.team254.lib.trajectory.io.TextFileSerializer;
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
	public static String joinPath(String path1, String path2) {
		File file1 = new File(path1);
		File file2 = new File(file1, path2);
		return file2.getPath();
	}

	private static boolean writeFile(String path, String data) {
		try {
			File file = new File(path);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(data);
			bw.close();
		} catch (IOException e) {
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
		String directory = "Paths";
		if (args.length >= 1) {
			directory = args[0];
		}

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

			// Outputs to the directory supplied as the first argument.
			TextFileSerializer js = new TextFileSerializer();
			String serialized = js.serialize(path);
			// System.out.print(serialized);
			String fullpath = joinPath(directory, path_name + ".txt");
			String leftPath = joinPath(directory, path_name + "_SRX_Left.json");
			String rightPath = joinPath(directory, path_name + "_SRX_Right.json");

			if (!writeFile(fullpath, serialized)) {
				System.err.println(fullpath + " could not be written!!!!1");
				System.exit(1);
			} else {
				System.out.println("Wrote " + fullpath);

				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						new JFXPanel(); // this will prepare JavaFX toolkit and
										// environment
						Platform.runLater(new Runnable() {
							@Override
							public void run() {
								SRXTranslator srxt = new SRXTranslator();
								CombinedSRXMotionProfile combined = srxt.getSRXProfileFromChezyPath(fullpath, 4, 2.54);
								String leftJSON = combined.leftProfile.toJSON();
								String rightJSON = combined.rightProfile.toJSON();

								if (!writeFile(leftPath, leftJSON)) {
									System.err.println(leftPath + " could not be written!!!!1");
									System.exit(1);
								} else if (!writeFile(rightPath, rightJSON)) {
									System.err.println(rightPath + " could not be written!!!!1");
									System.exit(1);
								} else {
									SRXMotionProfile leftImportedProfile;
									SRXMotionProfile rightImportedProfile;
									try {
										leftImportedProfile = srxt.importSRXJSON(leftPath);
										rightImportedProfile = srxt.importSRXJSON(rightPath);
									} catch (ParseException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									Plotter pl = new Plotter();
									pl.plotChezyTrajectory(path);
								}
							}
						});
					}
				});

			}
		}
	}
}
