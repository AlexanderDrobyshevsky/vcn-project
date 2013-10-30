package ru.vermilion.vcn.app.staff;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class VCNTreeItem extends TreeItem {

	private String content;
	
	private boolean isWrap = true;
	
	
	
	
	public VCNTreeItem(Tree parent, int style) {
		super(parent, style);
	}
	
	public VCNTreeItem(TreeItem parentItem, int style) {
		super(parentItem, style);
	}
	
	public VCNTreeItem(Tree parent, int style, int index) {
		super(parent, style, index);
	}
	
	public VCNTreeItem(TreeItem parentItem, int style, int index) {
		super(parentItem, style, index);
	}
	
	protected void checkSubclass () {
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isWrap() {
		return isWrap;
	}

	public void setWrap(boolean isWrap) {
		this.isWrap = isWrap;
	}
	

}
