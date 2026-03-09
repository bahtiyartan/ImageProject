package com.ias.image.processing.logic;

import com.ias.image.processing.logic.operations.ImageOperation;
import com.ias.image.processing.logic.operations.ProjectData;
import java.awt.image.BufferedImage;
import java.util.List;
import java.io.*;
import javax.imageio.ImageIO;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.*;
import com.ias.image.processing.logic.operations.*;

public class ImageController {

	private ImageModel model;
	private Runnable updateViewCallback;

	public ImageController(ImageModel model) {
		this.model = model;
	}

	private byte[] imageToBytes(BufferedImage img) throws IOException {
		if (img == null) return null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(img, "png", baos);
		return baos.toByteArray();
	}

	private BufferedImage bytesToImage(byte[] data) throws IOException {
		if (data == null) return null;
		return ImageIO.read(new ByteArrayInputStream(data));
	}

	private String convertToJson(ProjectData data) {
		return new GsonBuilder().setPrettyPrinting().create().toJson(data);
	}
	private ProjectData parseJson(File file) throws Exception {
		Gson gson = new Gson();
		try (Reader reader = new FileReader(file)) {
			return gson.fromJson(reader, ProjectData.class);
		}
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
		if (model.getOriginalImage() == null)
			return;

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
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(model.getOriginalImage(), "png", baos);
		ProjectData data = new ProjectData(baos.toByteArray(), model.getOperations());

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		try (Writer writer = new FileWriter(file)) {
			writer.write(gson.toJson(data));
		}
	}

	public void loadProject(File file) throws Exception {
		try (Reader reader = new FileReader(file)) {
			JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

			byte[] imageData = new Gson().fromJson(jsonObject.get("imageData"), byte[].class);
			model.setOriginalImage(bytesToImage(imageData));

			JsonArray opsArray = jsonObject.getAsJsonArray("operations");
			model.getOperations().clear();

			Gson gson = new Gson();
			if (opsArray != null) {
				for (JsonElement element : opsArray) {
					JsonObject opObj = element.getAsJsonObject();

					String typeStr = opObj.get("getOperationType") != null ?
							opObj.get("getOperationType").getAsString() :
							opObj.get("type") != null ? opObj.get("type").getAsString() : null;

					ImageOperation op = null;

					if (opObj.has("angle")) {
						op = gson.fromJson(element, RotateOp.class);
					} else if (opObj.has("x") && opObj.has("width")) {
						op = gson.fromJson(element, CropOp.class);
					} else if (opObj.has("kernelSize")) { // GaussianBlur
						op = gson.fromJson(element, GaussianBlurOp.class);
					} else if (opObj.has("countX")) { // TileOp
						op = gson.fromJson(element, TileOp.class);
					}

					if (op != null) model.addOperation(op);
				}
			}
			processImage();
		}
	}

	public ImageModel getModel() {
		return model;
	}
}