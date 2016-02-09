package com.team254.lib.trajectory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import com.team254.lib.trajectory.io.*;

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
	public CombinedSRXMotionProfile getSRXProfileFromChezyTrajectory(String chezyTrajectoryLoc, double wheelDiameter, double gearReduction) {

		TextFileDeserializer ds = new TextFileDeserializer();  //254's deserializer
		String serializedChezyTrajectory; // string that is read in from the file
		Path chezyTrajectory; //254 path to deserialize into
		
		try {
			//read the file
			serializedChezyTrajectory = readFile(chezyTrajectoryLoc, StandardCharsets.UTF_8);
			//deserialize from the string to a path object
			chezyTrajectory = ds.deserialize(serializedChezyTrajectory);
			
			//create an array of points for the SRX
			double[][]leftPoints = new double[chezyTrajectory.go_left_pair_.left.segments_.length][3];
			
			//Fill that array
			for (int i = 0; i < chezyTrajectory.go_left_pair_.left.segments_.length; i++) {
				//TODO: translate from feet to rotations
				leftPoints[i][0] = chezyTrajectory.go_left_pair_.left.segments_[i].pos;
				//TODO: translate from fps to rpm
				leftPoints[i][1] = chezyTrajectory.go_left_pair_.left.segments_[i].vel;
				//translate from seconds to milliseconds 
				leftPoints[i][2] = chezyTrajectory.go_left_pair_.left.segments_[i].dt*1000;
			}
			
			//do it again for the right side
			double[][]rightPoints = new double[chezyTrajectory.go_left_pair_.right.segments_.length][3];
			for (int i = 0; i < chezyTrajectory.go_left_pair_.right.segments_.length; i++) {
				//TODO: translate from feet to rotations
				rightPoints[i][0] = chezyTrajectory.go_left_pair_.right.segments_[i].pos;
				//TODO: translate from fps to rpm
				rightPoints[i][1] = chezyTrajectory.go_left_pair_.right.segments_[i].vel;
				//translate from seconds to milliseconds 
				rightPoints[i][2] = chezyTrajectory.go_left_pair_.right.segments_[i].dt*1000;
			}
			
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

	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}
}
