package com.team319.lib;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.io.TextFileSerializer;
import com.team319.lib.SRXTranslator.CombinedSRXMotionProfile;

public class PathWriter {

	public static final String DIRECTORY = "Paths";
	public static final String PATH_NAME = "TyPath";

	public static boolean writePath(Path path, CombinedSRXMotionProfile combined){
		// Outputs to the directory supplied as the first argument.
		TextFileSerializer js = new TextFileSerializer();
		String serialized = js.serialize(path);
		// System.out.print(serialized);
		String fullpath = joinFilePaths(DIRECTORY, PATH_NAME + ".txt");
		String leftPath = joinFilePaths(DIRECTORY, PATH_NAME + "_SRX_Left.json");
		String rightPath = joinFilePaths(DIRECTORY, PATH_NAME + "_SRX_Right.json");

		String leftJSON = combined.leftProfile.toJsonString();
		String rightJSON = combined.rightProfile.toJsonString();

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

	private static boolean writeFile(String filePath, String data) {
		try {
			File file = new File(filePath);

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

	public static String joinFilePaths(String path1, String path2) {
		File file1 = new File(path1);
		File file2 = new File(file1, path2);
		return file2.getPath();
	}

}
