package com.ias.image.processing.logic;

import com.ias.image.processing.logic.operations.*;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import javax.imageio.ImageIO;

public class ImageController {

	private final ImageModel model;
	private Runnable updateViewCallback;
	private String originalImagePath;

	public ImageController(ImageModel model) {
		this.model = model;
	}

	public void setUpdateViewCallback(Runnable callback) {
		this.updateViewCallback = callback;
	}

	public void loadImage(File file) throws IOException {
		this.originalImagePath = file.getAbsolutePath();
		BufferedImage img = ImageIO.read(file);
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
		if (model.getOriginalImage() == null)
			return;

		OperationResult currentRes = new OperationResult(model.getOriginalImage(), "Original Loaded", null, null);

		try {
			for (ImageOperation op : model.getOperations()) {
				if (op.getInputType() == DataType.IMAGE && !currentRes.hasImage()) {
					System.err.println("Error: " + op.getOperationName() + " It's waiting for the process image, but the previous step was generating the image.");
					break;
				}

				currentRes = op.apply(currentRes);
			}
		} catch (Exception e) {
			System.err.println("An error occurred while processing the image: " + e.getMessage());
			e.printStackTrace();
		}

		model.setCurrentResult(currentRes);

		if (currentRes.hasImage()) {
			model.setCurrentImage(currentRes.getImageResult());
		}

		if (updateViewCallback != null)
			updateViewCallback.run();
	}

	public void saveProject(File file) throws IOException {
		File processedImageFile = new File(file.getParent(), file.getName() + "_image.png");
		if (model.getCurrentImage() != null) {
			ImageIO.write(model.getCurrentImage(), "png", processedImageFile);
		}

		String safeImagePath = (originalImagePath != null) ? originalImagePath.replace("\\", "\\\\") : "";
		StringBuilder json = new StringBuilder();
		json.append("{\n");
		json.append("\"imagePath\": \"").append(safeImagePath).append("\",\n");
		json.append("\"operations\": [\n");

		List<ImageOperation> ops = model.getOperations();
		for (int i = 0; i < ops.size(); i++) {
			json.append(ops.get(i).toJson());
			if (i < ops.size() - 1)
				json.append(",");
			json.append("\n");
		}
		json.append("]\n");
		json.append("}");

		try (FileWriter writer = new FileWriter(file)) {
			writer.write(json.toString());
		}
	}

	public void loadProject(File file) throws Exception {
		String content = new String(Files.readAllBytes(file.toPath()));
		model.getOperations().clear();

		String imagePath = extractField(content, "imagePath");
		if (imagePath != null && !imagePath.isEmpty()) {
			imagePath = imagePath.replace("\\\\", "\\");
			this.originalImagePath = imagePath;
			BufferedImage img = ImageIO.read(new File(imagePath));
			model.setOriginalImage(img);
		}

		int bracketStart = content.indexOf("[");
		int bracketEnd = content.lastIndexOf("]");

		if (bracketStart != -1 && bracketEnd != -1) {
			String opsStr = content.substring(bracketStart + 1, bracketEnd);
			if (!opsStr.trim().isEmpty()) {
				String[] opJsons = opsStr.split("\\},\\s*\\{");

				for (String raw : opJsons) {
					String opJson = raw.trim();
					if (!opJson.startsWith("{"))
						opJson = "{" + opJson;
					if (!opJson.endsWith("}"))
						opJson = opJson + "}";

					ImageOperation op = null;
					String idStr = extractField(opJson, "operationId");
					if (idStr != null) {
						int id = Integer.parseInt(idStr);
						switch (id) {
						case 1:
							op = CropOp.fromJson(opJson);
							break;
						case 2:
							op = RotateOp.fromJson(opJson);
							break;
						case 3:
							op = TileOp.fromJson(opJson);
							break;
						case 4:
							op = GaussianBlurOp.fromJson(opJson);
							break;
						case 5:
							op = ColorHistogramOp.fromJson(opJson);
							break;
						}
					}
					if (op != null)
						model.addOperation(op);
				}
			}
		}
		processImage();
	}

	private String extractField(String json, String field) {
		int idx = json.indexOf("\"" + field + "\"");
		if (idx == -1)
			return null;
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