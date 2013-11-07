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
	
	public GlobalSearch(VermilionCascadeNotebook vermilionCascadeNotebook) {
		this.vermilionCascadeNotebook = vermilionCascadeNotebook;
	}
	
	public void globalSearchAction() {
		GlobalSearchDialog documentSearchDialog = new GlobalSearchDialog(
				vermilionCascadeNotebook.getTree().getShell());
		documentSearchDialog.open();
		GlobalSearchDialog.DialogResult dr = documentSearchDialog.getResult();

		if (dr != GlobalSearchDialog.DialogResult.CANCEL) {
			searchAction(null, dr.searchText);
		}
	}
	
	public void globalReSearchAction() {
		searchAction(lastFoundItem, lastSearchText);
	}

	private void searchAction(TreeItem lastFoundItem, String searchText) {
		if (searchText == null || searchText.isEmpty()) {
			return;
		}
		
		TreeItem foundItem = search(lastFoundItem, searchText);
		
		if (foundItem != null) {
			vermilionCascadeNotebook.getTree().setSelection(foundItem);
			Event treeSelectionEvent = new Event();
			treeSelectionEvent.item = foundItem;
			vermilionCascadeNotebook.getTree().notifyListeners(SWT.Selection, treeSelectionEvent);
		}
		
		GlobalSearch.this.lastFoundItem = foundItem;
		lastSearchText = searchText;
	}
	
	private boolean lastItemAlreadyFound;
	private TreeItem search(TreeItem lastFoundItem, String searchString) {
		lastItemAlreadyFound = lastFoundItem == null;
		
		TreeItem[] treeItems = vermilionCascadeNotebook.getTree().getItems();
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
			
			if (itemContent.indexOf(searchString) >= 0) {
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
	
	

}
