package ru.vermilion.vcn.app.staff;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class VCNTreeItem extends TreeItem {

	private static int idGenerator = 0;
	
	// null value means empty string
	private String content = "";
	
	private boolean isWrap = true;
	
	private int id = idGenerator++;
	
	private Color foregroundColor;
	
	private boolean isBold = false; 
	
	private static Color defaultForeground;
	
	private static Font boldFont;
	
	private static Font newBoldFont;
	
	private static Integer fontSize = null;
	{
		if (fontSize == null) {
			try {
				FontData[] fD = this.getFont().getFontData();

				fontSize = fD[0].getHeight();
				
				System.out.println("Tree default font size detected = " + fontSize);
			} catch (Exception ex) {
				ex.printStackTrace();
				fontSize = 10;
			}
		}
		
		FontData[] fD = this.getFont().getFontData();
		fD[0].setStyle(SWT.BOLD);
		boldFont = new Font(this.getDisplay(), fD);
		
		addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
        		if (foregroundColor != null && !foregroundColor.isDisposed()) {
        			foregroundColor.dispose();
        		}
			}
		});
		
		if (defaultForeground == null) {
			defaultForeground = super.getForeground();
		}
	}

	
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
	
	public void buildItemAgainstSource(VCNTreeItem sourceItem) {
		this.setText(sourceItem.getText());
		
		this.content = sourceItem.getContent();
		this.isWrap = sourceItem.isWrap;
		
		if (sourceItem.foregroundColor != null) {
			setForeground(new Color(this.getDisplay(), sourceItem.foregroundColor.getRGB()));
		}
		
		setBold(sourceItem.isBold);
	}
	
	public void makeBold() {
		if (this.isDisposed()) {
			return;
		}
		
		isBold = true;
        setFont(boldFont);
	}
	
	public void makePlain() {
		if (this.isDisposed()) { 
			return;
		}
		
        isBold = false;		
		setFont(null);
	}
	
	protected void checkSubclass () {
	}

	// null value means empty string
	public String getContent() {
		if (content == null) {
			content = "";
		}
		
		return content;
	}

	public void setContent(String content) {
		assert content != null;
		
		this.content = content;
	}

	public boolean isWrap() {
		return isWrap;
	}

	public void setWrap(boolean isWrap) {
		this.isWrap = isWrap;
	}
	
	public String getPath() {
		String path = this.getText();
		if (path.contains("/") || path.contains("\\")) {
			path = "'" + path + "'";
		}
		TreeItem parent = this;
		while ((parent = parent.getParentItem()) != null) {
			String parentPath = parent.getText();
			if (parentPath.contains("/") || parentPath.contains("\\")) {
				parentPath = "'" + parentPath + "'";
			}
			
			path = parentPath + "/" + path;
		}
		
		return "/" + path;
	}
	
	public static int getFontSize() {
		return fontSize;
	}

	public static void setFontSize(int fontSize) {
		VCNTreeItem.fontSize = Math.min(210, Math.max(4, fontSize));
	}
	
	public static void applyNewFontSize(Tree tree) {
		FontData[] fD = boldFont.getFontData();
		fD[0].setStyle(SWT.BOLD);
		fD[0].setHeight(fontSize);
		newBoldFont = new Font(tree.getDisplay(), fD);
		
		traverseTree(tree);
		
		boldFont.dispose();
		boldFont = newBoldFont;
	}
	
	private static void traverseTree(Tree tree) {
		TreeItem[] treeItems = tree.getItems();
		for (TreeItem treeItem : treeItems) {
			traverseNode(treeItem);
		}
	}
	
	private static void traverseNode(TreeItem treeItem) {
		VCNTreeItem item = (VCNTreeItem) treeItem;
		if (item.isBold) {
			item.setFont(newBoldFont);
		}
		
		for (TreeItem ti : treeItem.getItems()) {
			traverseNode(ti);
		}
	}
	
	@Override
	public Color getForeground() {
		if (foregroundColor == null) {
			return super.getForeground();
		}
		
		return foregroundColor;
	}

	@Override
	public void setForeground(Color foregroundColor) {
		assert foregroundColor != null; 
		if (this.foregroundColor != null && this.foregroundColor.getRGB().equals(foregroundColor.getRGB())) {
			return;
		}
		
		super.setForeground(foregroundColor);
		
		this.foregroundColor = foregroundColor;
	}
	
	public void resetForeground() {
		if (foregroundColor == null) {
			return;
		}
		
		if (foregroundColor != null && !foregroundColor.isDisposed()) {
			foregroundColor.dispose();
		}
		
		foregroundColor = null;
		
		super.setForeground(defaultForeground);
	}
	
    public boolean isOwnForeground() {
    	return foregroundColor != null;
    }	

	public boolean isBold() {
		return isBold;
	}

	public void setBold(boolean isBold) {
		this.isBold = isBold;
		
		if (isBold) {
			makeBold();
		} else {
			makePlain();
		}
	}
	
	public void revertBold() {
		setBold(!isBold);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VCNTreeItem other = (VCNTreeItem) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		if (isDisposed()) {
			return "VCNTreeItem [Disposed; isWrap=" + isWrap + ", id=" + id
					+ ", foregroundColor=" + foregroundColor + ", isBold="
					+ isBold + "]";
		}
		
		return "VCNTreeItem [name = " + getText() + ", isWrap=" + isWrap + ", id=" + id +
				", foregroundColor=" + foregroundColor + ", isBold="
				+ isBold + "]";
	}



	
}
