package ru.vermilion.vcn.app.capabilities.search;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.TreeItem;

import ru.vermilion.vcn.app.VermilionCascadeNotebook;
import ru.vermilion.vcn.app.capabilities.ICapability;
import ru.vermilion.vcn.app.dialogs.GlobalSearchDialog;
import ru.vermilion.vcn.app.staff.VCNTreeItem;

public class GlobalSearch implements ICapability {

	private VermilionCascadeNotebook vermilionCascadeNotebook;
	
	private String lastSearchText = "Vermilion";
	
	private TreeItem lastFoundItem;
	
	boolean isCaseSensitive = false;
	
	boolean isCheckNodes = false;
	
	public GlobalSearch(VermilionCascadeNotebook vermilionCascadeNotebook) {
		this.vermilionCascadeNotebook = vermilionCascadeNotebook;
	}
	
	public boolean globalSearchAction() {
		GlobalSearchDialog documentSearchDialog = new GlobalSearchDialog(
				vermilionCascadeNotebook.getTree().getShell());
		documentSearchDialog.open();
		GlobalSearchDialog.DialogResult dr = documentSearchDialog.getResult();

		if (dr != GlobalSearchDialog.DialogResult.CANCEL) {
			lastFoundItem = null;
			lastSearchText = dr.searchText;
			isCaseSensitive = dr.isCaseSensitive;
			isCheckNodes = dr.isCheckNodes;
			
			TreeItem[] selection = vermilionCascadeNotebook.getTree().getSelection();
			if (!dr.isStartOver && selection.length == 1) {
				lastFoundItem = (VCNTreeItem)selection[0];
			}
			
			return searchAction();
		}
		
		return false;
	}
	
	public boolean globalReSearchAction() {
		return searchAction();
	}

	// is to be rechecked the page
	private boolean searchAction() {
		if (lastSearchText == null || lastSearchText.isEmpty()) {
			return false;
		}
		
		TreeItem foundItem = search();
		
		if (foundItem != null) {
			vermilionCascadeNotebook.getTree().setSelection(foundItem);
			Event treeSelectionEvent = new Event();
			treeSelectionEvent.item = foundItem;
			vermilionCascadeNotebook.getTree().notifyListeners(SWT.Selection, treeSelectionEvent);
		}
		
		GlobalSearch.this.lastFoundItem = foundItem;
		
		return !isCheckNodes;
	}
	
	private boolean lastItemAlreadyFound;
	private TreeItem search() {
		lastItemAlreadyFound = lastFoundItem == null;
		
		TreeItem[] treeItems = vermilionCascadeNotebook.getTree().getItems();
		for (TreeItem treeItem : treeItems) {
			TreeItem itemFound = realSearch(treeItem, lastSearchText, lastFoundItem);
			
			if (itemFound != null) {
				return itemFound;
			}
		}	
		
		return null;
	}
	
	private TreeItem realSearch(TreeItem investigatingItem, String searchString, TreeItem lastFoundItem) {
		if (lastItemAlreadyFound) {
			String itemContent = (String) ((VCNTreeItem)investigatingItem).getContent();
			String itemName = investigatingItem.getText();
			
		    String checkingString;
		    if (isCheckNodes) {
		    	checkingString = itemName; 
		    } else {
		    	checkingString = itemContent;
		    }
			
			if (isCaseSensitive && checkingString.indexOf(searchString) >= 0 ||
					!isCaseSensitive && checkingString.toLowerCase().indexOf(searchString.toLowerCase()) >= 0) {
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

	public String getLastSearchText() {
		return lastSearchText;
	}

	public boolean isCaseSensitive() {
		return isCaseSensitive;
	}
	
	

}
