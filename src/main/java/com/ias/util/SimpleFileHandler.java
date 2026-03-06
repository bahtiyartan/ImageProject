package com.ias.util;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

public class SimpleFileHandler {

	public static ArrayList<String> list(String folderPath) {

		ArrayList<String> fileList = new ArrayList<String>();

		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(folderPath))) {

			for (Path path : stream) {
				System.out.println(path.getFileName());
				fileList.add(folderPath + path.getFileName());
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return fileList;
	}

	public static String getDataFolder() {
		return System.getProperty("user.dir") + "\\data\\";
	}

}
