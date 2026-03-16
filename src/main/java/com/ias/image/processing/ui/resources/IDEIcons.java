package com.ias.image.processing.ui.resources;

import java.awt.Image;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class IDEIcons {

	public static final ImageIcon DeleteIcon = getIcon(IDEIcons.class.getResource("delete.png"));
	public static final Icon OtherIcon = getIcon(IDEIcons.class.getResource("delete.png"));

	public static ImageIcon getIcon(URL url) {
		ImageIcon icon = null;
		try {
			icon = new ImageIcon(url);
			icon = resizeImageIcon(icon);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return icon;
	}

	public static ImageIcon getIcon(URL url, int size) {
		ImageIcon icon = null;
		try {
			icon = resizeImageIcon(url, size, true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return icon;
	}

	public static ImageIcon resizeImageIcon(URL url, int size) {
		return resizeImageIcon(url, size, size);
	}

	public static ImageIcon resizeImageIcon(URL url, int width, int height) {

		double XFactor = 1.0;

		width = (int) (width * XFactor);
		height = (int) (height * XFactor);

		ImageIcon old = new ImageIcon(url);

		Image newImage = old.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return new ImageIcon(newImage);
	}

	public static ImageIcon resizeImageIcon(URL url, int size, boolean resolution) {
		int nIconSize = size;
		if (resolution) {
			double XFactor = 1.0;
			nIconSize = (int) (size * XFactor);
		}
		ImageIcon old = new ImageIcon(url);
		Image newImage = old.getImage().getScaledInstance(nIconSize, nIconSize, Image.SCALE_SMOOTH);
		return new ImageIcon(newImage);
	}

	public static ImageIcon resizeImageIcon(ImageIcon imageIcon) {
		double XFactor = 1.0;
		int width = (int) (imageIcon.getIconWidth() * XFactor);
		int height = (int) (imageIcon.getIconHeight() * XFactor);

		Image newImage = imageIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
		return new ImageIcon(newImage);
	}
}