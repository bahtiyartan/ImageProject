package com.ias.image.processing.logic;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.CropOp;
import com.ias.image.processing.logic.operations.RotateOp;
import com.ias.image.processing.logic.operations.GaussianBlurOp;
import com.ias.image.processing.logic.operations.TileOp;
import java.awt.image.BufferedImage;
import java.util.List;
import java.io.*;
import javax.imageio.ImageIO;

public class ImageController {

	private ImageModel model;
	private Runnable updateViewCallback;

	public ImageController(ImageModel model) {
		this.model = model;
	}

	private String buildJson() {
		StringBuilder json = new StringBuilder();
		json.append("{\n");
		json.append("\"Operations\": [\n");

		List<ImageOperation> ops = model.getOperations();
		for (int i = 0; i < ops.size(); i++) {
			json.append(ops.get(i).toJson());
			if (i < ops.size() - 1) json.append(",");
			json.append("\n");
		}
		json.append("]\n");
		json.append("}");
		return json.toString();
	}

	public void setUpdateViewCallback(Runnable callback) {
		this.updateViewCallback = callback;
	}

	public void loadImage(BufferedImage img) {
		model.getOperations().clear();
		model.setOriginalImage(img);
		processImage();
	}

	public void addOperation(ImageOperation op) {
		model.addOperation(op);
		processImage();
	}

	private boolean cropModeActive = false;

	public void setCropModeActive(boolean active) {
		this.cropModeActive = active;
		if (updateViewCallback != null)
			updateViewCallback.run();
	}

	public boolean isCropModeActive() {
		return cropModeActive;
	}

	public void undo() {
		List<ImageOperation> ops = model.getOperations();
		if (!ops.isEmpty()) {
			ops.remove(ops.size() - 1);
			processImage();

		} else {
			System.out.println("There are no more actions to undo.");
		}
	}

	public void updateOperation(int index, ImageOperation newOp) {
		model.getOperations().set(index, newOp);
		processImage();
	}

	public void removeOperation(int index) {
		model.getOperations().remove(index);
		processImage();
	}

	private void processImage() {
		if (model.getOriginalImage() == null) return;

		BufferedImage result = model.getOriginalImage();
		try {
			for (ImageOperation op : model.getOperations()) {
				result = op.apply(result);
			}
		} catch (Exception e) {
			System.err.println("An error occurred while processing the image: " + e.getMessage());
			e.printStackTrace();
		}
		model.setCurrentImage(result);

		if (updateViewCallback != null)
			updateViewCallback.run();
	}

	public void saveProject(File file) throws IOException {
		File imageFile = new File(file.getParent(), file.getName() + "_image.png");
		if (model.getOriginalImage() != null) {
			ImageIO.write(model.getOriginalImage(), "png", imageFile);
		}

		// Generate JSON
		StringBuilder json = new StringBuilder();
		json.append("{\n");
		json.append("\" ImagePath\": \"").append(imageFile.getAbsolutePath()).append("\",\n");
		json.append("\" operations\": [\n");

		List<ImageOperation> ops = model.getOperations();
		for (int i = 0; i < ops.size(); i++) {
			json.append(ops.get(i).toJson());
			if (i < ops.size() - 1) json.append(",");
			json.append("\n");
		}
		json.append("]\n");
		json.append("}");

		// Write JSON to project file
		try (FileWriter writer = new FileWriter(file)) {
			writer.write(json.toString());
		}
	}


	public void loadProject(File file) throws Exception {
		String content = new String(java.nio.file.Files.readAllBytes(file.toPath()));
		model.getOperations().clear();

		String imagePath = extractField(content, "imagePath");
		if (imagePath != null && !imagePath.isEmpty()) {
			BufferedImage img = ImageIO.read(new File(imagePath));
			model.setOriginalImage(img);
		}
		// Extracts operations from JSON
		String opsStr = content.substring(content.indexOf("[") + 1, content.lastIndexOf("]"));
		String[] opJsons = opsStr.split("\\},\\s*\\{");

		for (String raw : opJsons) {
			String opJson = raw.trim();
			if (!opJson.startsWith("{")) opJson = "{" + opJson;
			if (!opJson.endsWith("}")) opJson = opJson + "}";

			String type = extractField(opJson, "operationType");
			ImageOperation op = null;

			int id = Integer.parseInt(extractField(opJson, "operationId"));
			switch (id) {
				case 1: op = CropOp.fromJson(opJson); break;
				case 2: op = RotateOp.fromJson(opJson); break;
				case 3: op = TileOp.fromJson(opJson); break;
				case 4: op = GaussianBlurOp.fromJson(opJson); break;
			}

			if (op != null) model.addOperation(op);
		}
		processImage();
	}

	private String extractField(String json, String field) {
		int idx = json.indexOf("\"" + field + "\"");
		if (idx == -1) return null;
		int colon = json.indexOf(":", idx);
		int comma = json.indexOf(",", colon);
		int endBrace = json.indexOf("}", colon);
		int end = (comma == -1) ? endBrace : Math.min(comma, endBrace);
		String value = json.substring(colon + 1, end).trim();
		return value.replace("\"", "");
	}

	public ImageModel getModel() {
		return model;
	}
}
