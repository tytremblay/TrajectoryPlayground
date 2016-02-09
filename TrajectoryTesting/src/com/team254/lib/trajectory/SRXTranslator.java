package com.team254.lib.trajectory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.team254.lib.trajectory.io.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class SRXTranslator {

	//Generic Motion Profile Class
	public class SRXMotionProfile {

		public SRXMotionProfile(int numPoints, double[][] points) {
			this.numPoints = numPoints;
			this.points = points;
		}

		public SRXMotionProfile() {}

		public int numPoints;
		// Position (rotations) Velocity (RPM) Duration (ms)
		public double[][] points;
		
		public String toJSON()
		{
			StringBuilder sb = new StringBuilder();
			JSONObject obj = new JSONObject(); 
			obj.put("numPoints",numPoints); 
			//obj.put("points", points); 
			
			
			JSONArray list = new JSONArray(); 
			for (int i = 0; i < points.length; i++) {
				JSONObject point = new JSONObject();
				point.put("pos", points[i][0]);
				point.put("vel", points[i][1]);
				point.put("dt", points[i][2]);
				
				list.add(point);
			}
			
			obj.put("points",list);
			//System.out.print(obj); 
			return obj.toString(); 
		}

	}

	//Combines left and right motion profiles in one object
	public class CombinedSRXMotionProfile {

		public CombinedSRXMotionProfile(SRXMotionProfile left, SRXMotionProfile right) {
			this.leftProfile = left;
			this.rightProfile = right;
		}

		public SRXMotionProfile leftProfile;
		public SRXMotionProfile rightProfile;
	}

	//Reads a text file generated from 254's trajectory planning software and 
	//creates a CombinedSRXMotionProfile from it
	public CombinedSRXMotionProfile getSRXProfileFromChezyPath(String chezyTrajectoryLoc, double wheelDiameterInches, double gearReduction) {

		TextFileDeserializer ds = new TextFileDeserializer();  //254's deserializer
		String serializedChezyTrajectory; // string that is read in from the file
		Path chezyPath; //254 path to deserialize into
		
		try {
			//read the file
			serializedChezyTrajectory = readFile(chezyTrajectoryLoc, StandardCharsets.UTF_8);
			//deserialize from the string to a path object
			chezyPath = ds.deserialize(serializedChezyTrajectory);
			
			//create an array of points for the SRX
			double[][]leftPoints = extractSRXPointsFromChezyTrajectory(chezyPath.pair.left, wheelDiameterInches, gearReduction);
			
			//do it again for the right side
			double[][]rightPoints = extractSRXPointsFromChezyTrajectory(chezyPath.pair.right, wheelDiameterInches, gearReduction);
			
			//create the motion profile objects
			SRXMotionProfile left = new SRXMotionProfile(leftPoints.length,leftPoints);
			SRXMotionProfile right = new SRXMotionProfile(rightPoints.length, rightPoints);
			
			//Combine
			return new CombinedSRXMotionProfile(left, right);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;

	}
	
	public double[][] extractSRXPointsFromChezyTrajectory(Trajectory traj, double wheelDiameterInches, double gearReduction)
	{		
		//create an array of points for the SRX
		double[][]points = new double[traj.segments_.length][3];
		
		//Fill that array
		for (int i = 0; i < traj.segments_.length; i++) {
			
			//translate from feet to rotations
			points[i][0] = convertFeetToEncoderRotations(traj.segments_[i].pos, wheelDiameterInches, gearReduction);
			
			//translate from fps to rpm				
			points[i][1] = convertFpsToEncoderRpm(traj.segments_[i].vel, wheelDiameterInches, gearReduction);
			
			//translate from seconds to milliseconds 
			points[i][2] = traj.segments_[i].dt*1000;
		}
		
		return points;
	}
	
	public double convertFpsToEncoderRpm(double fps, double wheelDiameterInches, double gearReduction)
	{
		//feet per minute
		double fpm = fps * 60;
		//wheel rpm
		double rpm = fpm * 12 / (wheelDiameterInches * Math.PI);
		//encoder rpm
		double encoderRpm = rpm * gearReduction;
		
		return encoderRpm;
	}
	
	//convert 254's distance units of feet to SRX's distance units of encoder rotations
	public double convertFeetToEncoderRotations(double feet, double wheelDiameterInches, double gearReduction)
	{
		//convert feet to wheel rotations using the circumference of the wheel
		double wheelRotations = feet*12/(wheelDiameterInches * Math.PI);
		
		//convert wheel rotations to encoder rotations using the recuction between the two
		double encoderRotations = wheelRotations * gearReduction;
		return encoderRotations;
	}

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
