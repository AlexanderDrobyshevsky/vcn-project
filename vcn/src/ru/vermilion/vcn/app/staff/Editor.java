package ru.vermilion.vcn.app.staff;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

public class Editor extends Text {
	
	private VCNTreeItem treeItem;
	
	public Editor(Composite parent, int style) {
		super(parent, style);
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

	
}
