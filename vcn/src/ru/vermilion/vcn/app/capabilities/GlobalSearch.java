package ru.vermilion.vcn.app.capabilities;

import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import ru.vermilion.vcn.app.dialogs.GlobalSearchDialog;
import ru.vermilion.vcn.app.staff.VCNTreeItem;

public class GlobalSearch implements ICapability {

	private Tree tree;
	
	public GlobalSearch(Tree tree) {
		this.tree = tree;
		
		addGlobalSearchKeyListener();
	}

	private void addGlobalSearchKeyListener() {
		
		
		tree.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				
				System.out.println("#: Event" + e);
				// Ctrl+J pressed -- TODO -> Ctrl-H
				if (e.character == 0xa && e.keyCode == 0x6a && e.stateMask == 0x40000) {
					GlobalSearchDialog documentSearchDialog = new GlobalSearchDialog(tree.getShell());
					documentSearchDialog.open();
					GlobalSearchDialog.DialogResult dr = documentSearchDialog.getResult();
					
					
					

				}
				
				// F3 - Pressed
				if (e.character == 0x00 && e.keyCode == 0x100000c && e.stateMask == 0x0) {

				}
			}

			public void keyReleased(KeyEvent e) { }
		});

	}
	
	
	private boolean lastItemAlreadyFound;
	private TreeItem search(TreeItem lastFoundItem, String searchString) {
		
		lastItemAlreadyFound = lastFoundItem == null;
		
		TreeItem[] treeItems = tree.getItems();
		for (TreeItem treeItem : treeItems) {
			TreeItem itemFound = realSearch(treeItem, searchString, lastFoundItem);
			
			if (itemFound != null) {
				return itemFound;
			}
		}	
		
		return null;
	}
	
	private TreeItem realSearch(TreeItem investigatingItem, String searchString, TreeItem lastFoundItem) {
	    
		if (lastItemAlreadyFound) {
			String itemContent = (String) ((VCNTreeItem)investigatingItem).getContent();
			if (itemContent.indexOf(searchString) > 0) {
				return investigatingItem;
			}
			
			TreeItem[] childrenItems = investigatingItem.getItems();
			for (TreeItem treeItem : childrenItems) {
				TreeItem itemFound = realSearch(treeItem, searchString, null);
				
				if (itemFound != null) {
					return itemFound;
				}
			}
		} else {
			if (lastFoundItem == investigatingItem) {
				lastItemAlreadyFound = true;
				
				TreeItem[] childrenItems = investigatingItem.getItems();
				for (TreeItem treeItem : childrenItems) {
					TreeItem itemFound = realSearch(treeItem, searchString, null);
					
					if (itemFound != null) {
						return itemFound;
					}					
				}
			} else {
				TreeItem[] childrenItems = investigatingItem.getItems();
				for (TreeItem treeItem : childrenItems) {
					TreeItem itemFound = realSearch(treeItem, searchString, lastFoundItem);
					
					if (itemFound != null) {
						return itemFound;
					}
				}				
			}
			
		}
		
	    return null;
	}
	

}
