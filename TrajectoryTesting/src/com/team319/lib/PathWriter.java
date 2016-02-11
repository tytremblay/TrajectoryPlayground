package com.team319.lib;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.SRXTranslator.CombinedSRXMotionProfile;
import com.team254.lib.trajectory.io.TextFileSerializer;

public class PathWriter {

	public static final String DIRECTORY = "Paths";
	public static final String PATH_NAME = "TyPath";

	public static boolean writePath(Path path, CombinedSRXMotionProfile combined){
		// Outputs to the directory supplied as the first argument.
		TextFileSerializer js = new TextFileSerializer();
		String serialized = js.serialize(path);
		// System.out.print(serialized);
		String fullpath = joinPath(DIRECTORY, PATH_NAME + ".txt");
		String leftPath = joinPath(DIRECTORY, PATH_NAME + "_SRX_Left.json");
		String rightPath = joinPath(DIRECTORY, PATH_NAME + "_SRX_Right.json");

		String leftJSON = combined.leftProfile.toJSON();
		String rightJSON = combined.rightProfile.toJSON();

		if (!writeFile(fullpath, serialized)) {
			System.err.println(fullpath + " could not be written!!!!1");
			return false;
		}else if (!writeFile(leftPath, leftJSON)) {
			System.err.println(leftPath + " could not be written!!!!1");
			return false;
		} else if (!writeFile(rightPath, rightJSON)) {
			System.err.println(rightPath + " could not be written!!!!1");
			return false;
		}
		return true;
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

	public static String joinPath(String path1, String path2) {
		File file1 = new File(path1);
		File file2 = new File(file1, path2);
		return file2.getPath();
	}

}
