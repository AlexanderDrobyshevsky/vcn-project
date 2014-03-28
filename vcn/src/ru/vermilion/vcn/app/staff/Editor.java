package ru.vermilion.vcn.app.staff;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class Editor extends Text {
	
	private VCNTreeItem treeItem;
	
    private static Integer fontSize = null;
	
	public Editor(Composite parent, int style) {
		super(parent, style);

		if (fontSize == null) {
			try {
				FontData[] fD = this.getFont().getFontData();

				fontSize = fD[0].getHeight();
				
				System.out.println("Editor default font size detected = " + fontSize);
			} catch (Exception ex) {
				ex.printStackTrace();
				fontSize = 10;
			}
		}
		
		FontData[] fontData = this.getFont().getFontData();
		fontData[0].setHeight(fontSize);
		this.setFont(new Font(parent.getDisplay(), fontData[0]));
	}

	protected void checkSubclass () {
		//if (!isValidSubclass ()) error (SWT.ERROR_INVALID_SUBCLASS);
	}
	
	public VCNTreeItem getTreeItem() {
		return treeItem;
	}

	public void setTreeItem(VCNTreeItem treeItem) {
		this.treeItem = treeItem;
	}

	// SWT.WRAP - is wrap! ))
	public boolean isWrapped() {
		return (getStyle() & SWT.WRAP) == 1;
	}

	public static int getFontSize() {
		return fontSize;
	}

	public static void setFontSize(int fontSize) {
		Editor.fontSize = Math.min(420, Math.max(2, fontSize));
	}
	
	public void applyFontSize() {
		FontData[] fontData = this.getFont().getFontData();
		fontData[0].setHeight(fontSize);
		this.setFont(new Font(this.getDisplay(), fontData[0]));
	}
	
}
