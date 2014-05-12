package ru.vermilion.vcn.app;

import org.eclipse.swt.graphics.Color;

public class VCNConfiguration {
  
	public static boolean isTreeLines = true;
	
	private static Color gradientSelectionColor;

	
	public static Color getGradientSelectionColor() {
		return gradientSelectionColor;
	}

	public static void setGradientSelectionColor(Color gradientSelectionColor) {
		VCNConfiguration.gradientSelectionColor = gradientSelectionColor;
	}

}
