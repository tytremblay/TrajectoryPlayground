package com.team319.lib;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.io.TextFileSerializer;

public class PathWriter {

	private static final String DIRECTORY = "paths";

	private static final String PATH_NAME = "TyPath";



	public static boolean writePaths(Path path, SRXTranslator.CombinedSRXMotionProfile combined){
		String fullPath = joinPath(DIRECTORY, PATH_NAME + ".txt");
		String leftPath = joinPath(DIRECTORY, PATH_NAME + "_SRX_Left.json");
		String rightPath = joinPath(DIRECTORY, PATH_NAME + "_SRX_Right.json");

		TextFileSerializer js = new TextFileSerializer();
		String fullJson = js.serialize(path);
		String leftJson = combined.leftProfile.toJSON();
		String rightJson = combined.rightProfile.toJSON();

		if (!writeFile(fullPath, fullJson)) {
			System.err.println(leftPath + " could not be written!!!!1");
			return false;
		}else if (!writeFile(leftPath, leftJson)) {
			System.err.println(leftPath + " could not be written!!!!1");
			return false;
		} else if (!writeFile(rightPath, rightJson)) {
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
