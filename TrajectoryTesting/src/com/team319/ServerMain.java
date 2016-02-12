package com.team319;

import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.PathGenerator;
import com.team254.lib.trajectory.TrajectoryGenerator;
import com.team254.lib.trajectory.WaypointSequence;
import com.team254.lib.trajectory.io.TextFileSerializer;
import com.team319.lib.PathWriter;
import com.team319.lib.SRXTranslator;
import com.team319.lib.SRXTranslator.CombinedSRXMotionProfile;
import com.team319.lib.SRXTranslator.SRXMotionProfile;
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
public class ServerMain {

	public static void main(String[] args) {

		WebServer.startServer();

	}
}
