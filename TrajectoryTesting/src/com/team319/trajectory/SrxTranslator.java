package com.team319.trajectory;

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

public class SrxTranslator {

	// Reads a text file generated from 254's trajectory planning software and
	// creates a CombinedSrxMotionProfile from it
	public SrxTrajectory getSRXProfileFromChezyPath(Path chezyPath, SrxTrajectory.Config config) {

		// create an array of points for the SRX
		double[][] leftPoints = extractSRXPointsFromChezyTrajectory(chezyPath.getPair().left, config.wheel_dia,
				config.scale_factor, config.direction);

		// do it again for the right side
		double[][] rightPoints = extractSRXPointsFromChezyTrajectory(chezyPath.getPair().right, config.wheel_dia,
				config.scale_factor, config.direction);

		// create the motion profile objects
		SrxMotionProfile left = new SrxMotionProfile(leftPoints.length, leftPoints);
		SrxMotionProfile right = new SrxMotionProfile(rightPoints.length, rightPoints);

		// Combine
		return new SrxTrajectory(left, right);

	}

	public double[][] extractSRXPointsFromChezyTrajectory(Trajectory traj, double wheelDiameterInches,
			double scaleFactor, int direction) {
		// create an array of points for the SRX
		double[][] points = new double[traj.getSegments().length][3];

		// Fill that array
		for (int i = 0; i < traj.getSegments().length; i++) {

			// translate from feet to rotations
			points[i][0] = direction * convertFeetToEncoderRotations(traj.getSegments()[i].pos, wheelDiameterInches, scaleFactor);

			// translate from fps to rpm
			points[i][1] = direction * convertFpsToEncoderRpm(traj.getSegments()[i].vel, wheelDiameterInches, scaleFactor);

			// translate from seconds to milliseconds
			points[i][2] = traj.getSegments()[i].dt * 1000;
		}

		return points;
	}

	public double convertFpsToEncoderRpm(double fps, double wheelDiameterInches, double scaleFactor) {
		// feet per minute
		double fpm = fps * 60;
		// wheel rpm
		double rpm = fpm * 12 / (wheelDiameterInches * Math.PI);
		// encoder rpm
		double encoderRpm = rpm * scaleFactor;

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

	public SrxMotionProfile importSrxJson(JSONObject srxJson) {
		int numPoints = ((Long) srxJson.get("numPoints")).intValue();
		JSONArray points = (JSONArray) srxJson.get("points");

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
		SrxMotionProfile prof = new SrxMotionProfile(numPoints, pointsArray);
		return prof;

	}

	public SrxMotionProfile importSrxJsonFile(String filePath) throws ParseException {
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
		JSONObject srxJson = (JSONObject) obj;

		return importSrxJson(srxJson);

	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
