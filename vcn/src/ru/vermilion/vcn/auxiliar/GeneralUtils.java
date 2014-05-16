package ru.vermilion.vcn.auxiliar;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class GeneralUtils {

	private GeneralUtils() {}
	
	public static void clearTree(Tree tree) {
		TreeItem[] items = tree.getItems();
		
		for (TreeItem ti : items) {
			ti.dispose();
		}
	}
	
	public static String getStackTrace(Exception ex) {
		if (ex == null) {
			return "";
		}
		
		Object [] traces = ex.getStackTrace();
		if (traces == null) {
			return "";
		}
		
		String s = "";
		for (Object trace : traces) {
			s += "    " + trace.toString() + "\r\n";
		}
		
		return s + "\r\n";
	}
}
