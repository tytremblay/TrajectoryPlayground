package com.team319.trajectory;

import org.json.simple.JSONObject;

//Combines left and right motion profiles in one object
public class CombinedSrxMotionProfile {
	private SrxMotionProfile leftProfile;
	private SrxMotionProfile rightProfile;

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
