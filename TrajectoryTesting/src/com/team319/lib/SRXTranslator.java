package com.team319.lib;

import java.io.IOException;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;

import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.Trajectory;
import com.team254.lib.trajectory.io.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;

public class SRXTranslator {

	// Generic Motion Profile Class
	public class SRXMotionProfile {

		public SRXMotionProfile(int numPoints, double[][] points) {
			this.numPoints = numPoints;
			this.points = points;
		}

		public SRXMotionProfile() {
		}

		public int numPoints;
		// Position (rotations) Velocity (RPM) Duration (ms)
		public double[][] points;

		public String toJSON() {
			StringBuilder sb = new StringBuilder();
			JSONObject obj = new JSONObject();
			obj.put("numPoints", numPoints);
			// obj.put("points", points);

			JSONArray list = new JSONArray();
			DecimalFormat df = new DecimalFormat("0.000");
			df.setRoundingMode(RoundingMode.HALF_UP);

			// format the values to 3 decimal places and add to the JSON object
			for (int i = 0; i < points.length; i++) {
				JSONObject point = new JSONObject();
				point.put("pos", Double.parseDouble(df.format(points[i][0])));
				point.put("vel", Double.parseDouble(df.format(points[i][1])));
				point.put("dt", points[i][2]);

				list.add(point);
			}

			obj.put("points", list);
			// System.out.print(obj);
			return obj.toString();
		}

	}

	// Combines left and right motion profiles in one object
	public class CombinedSRXMotionProfile {

		public CombinedSRXMotionProfile(SRXMotionProfile left, SRXMotionProfile right) {
			this.leftProfile = left;
			this.rightProfile = right;
		}

		public SRXMotionProfile leftProfile;
		public SRXMotionProfile rightProfile;
	}

	// Reads a text file generated from 254's trajectory planning software and
	// creates a CombinedSRXMotionProfile from it
	public CombinedSRXMotionProfile getSRXProfileFromChezyPath(Path chezyPath, double wheelDiameterInches,
			double gearReduction) {

		// create an array of points for the SRX
		double[][] leftPoints = extractSRXPointsFromChezyTrajectory(chezyPath.getPair().left, wheelDiameterInches,
				gearReduction);

		// do it again for the right side
		double[][] rightPoints = extractSRXPointsFromChezyTrajectory(chezyPath.getPair().right, wheelDiameterInches,
				gearReduction);

		// create the motion profile objects
		SRXMotionProfile left = new SRXMotionProfile(leftPoints.length, leftPoints);
		SRXMotionProfile right = new SRXMotionProfile(rightPoints.length, rightPoints);

		// Combine
		return new CombinedSRXMotionProfile(left, right);

	}

	public double[][] extractSRXPointsFromChezyTrajectory(Trajectory traj, double wheelDiameterInches,
			double gearReduction) {
		// create an array of points for the SRX
		double[][] points = new double[traj.getSegments().length][3];

		// Fill that array
		for (int i = 0; i < traj.getSegments().length; i++) {

			// translate from feet to rotations
			points[i][0] = convertFeetToEncoderRotations(traj.getSegments()[i].pos, wheelDiameterInches, gearReduction);

			// translate from fps to rpm
			points[i][1] = convertFpsToEncoderRpm(traj.getSegments()[i].vel, wheelDiameterInches, gearReduction);

			// translate from seconds to milliseconds
			points[i][2] = traj.getSegments()[i].dt * 1000;
		}

		return points;
	}

	public double convertFpsToEncoderRpm(double fps, double wheelDiameterInches, double gearReduction) {
		// feet per minute
		double fpm = fps * 60;
		// wheel rpm
		double rpm = fpm * 12 / (wheelDiameterInches * Math.PI);
		// encoder rpm
		double encoderRpm = rpm * gearReduction;

		return encoderRpm;
	}

	// convert 254's distance units of feet to SRX's distance units of encoder
	// rotations
	public double convertFeetToEncoderRotations(double feet, double wheelDiameterInches, double gearReduction) {
		// convert feet to wheel rotations using the circumference of the wheel
		double wheelRotations = feet * 12 / (wheelDiameterInches * Math.PI);

		// convert wheel rotations to encoder rotations using the recuction
		// between the two
		double encoderRotations = wheelRotations * gearReduction;
		return encoderRotations;
	}

	public SRXMotionProfile importSRXJSON(String filePath) throws ParseException {
		String json;
		try {
			json = readFile(filePath, StandardCharsets.UTF_8);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}

		JSONParser parser = new JSONParser();
		Object obj;

		obj = parser.parse(json);
		JSONObject cmd = (JSONObject) obj;
		int numPoints = ((Long) cmd.get("numPoints")).intValue();
		JSONArray points = (JSONArray) cmd.get("points");

		double[][] pointsArray = new double[points.size()][3];
		if (points != null) {
			int len = points.size();
			for (int i = 0; i < len; i++) {
				JSONObject singlePoint = (JSONObject) points.get(i);
			    pointsArray[i][0] = (double) singlePoint.get("dt");
			    pointsArray[i][1] = (double) singlePoint.get("pos");
			    pointsArray[i][2] = (double) singlePoint.get("vel");
				//System.out.println(pointsArray[i][0] + "," + pointsArray[i][1] + "," + pointsArray[i][2]);
			}
		}
		SRXMotionProfile prof = new SRXMotionProfile(numPoints, pointsArray);
		return prof;

	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
