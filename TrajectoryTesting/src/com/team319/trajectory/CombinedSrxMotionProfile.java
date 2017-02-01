package com.team319.trajectory;

import org.json.simple.JSONObject;

import com.team254.lib.trajectory.TrajectoryGenerator;

//Combines left and right motion profiles in one object
public class CombinedSrxMotionProfile {
	public SrxMotionProfile leftProfile;
	public SrxMotionProfile rightProfile;
	
	public static class Config extends TrajectoryGenerator.Config{
	    public double wheelbase_width;
	    public double wheel_dia;
	    public double scale_factor;  //used for reductions between encoder and wheel
	    public int direction = 1;  //1 = forward, -1 = backward
	}

	public CombinedSrxMotionProfile(SrxMotionProfile left, SrxMotionProfile right) {
		this.leftProfile = left;
		this.rightProfile = right;
	}

	public CombinedSrxMotionProfile(JSONObject combinedProfile){
		leftProfile = new SrxMotionProfile((JSONObject) combinedProfile.get("left"));
		rightProfile = new SrxMotionProfile((JSONObject) combinedProfile.get("right"));
	}

	public JSONObject toJson(){
		JSONObject combinedProfile = new JSONObject();
		combinedProfile.put("left", leftProfile.toJson());
		combinedProfile.put("right",rightProfile.toJson());
		return combinedProfile;
	}

}
