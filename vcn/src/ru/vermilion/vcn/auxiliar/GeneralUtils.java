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
}
