package ru.vermilion.vcn.app.elements;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import ru.vermilion.vcn.app.VermilionCascadeNotebook;
import ru.vermilion.vcn.app.staff.VCNTreeItem;
import ru.vermilion.vcn.auxiliar.MessageOKCancelDialog;
import ru.vermilion.vcn.auxiliar.StringRequestOKCancelDialog;


public class ApplicationTreePopupMenu {

	private VermilionCascadeNotebook vermilionCascadeNotebook;
	
	private Tree tree;
	
	private Shell shell;
	
	public ApplicationTreePopupMenu(VermilionCascadeNotebook vermilionCascadeNotebook) {
		this.vermilionCascadeNotebook = vermilionCascadeNotebook;
		this.tree = vermilionCascadeNotebook.getTree();
		this.shell = vermilionCascadeNotebook.getShell();
	}
	
	public void addTreePopupMenu() {
		Menu popupMenu = new Menu(tree);

		MenuItem newItem = new MenuItem(popupMenu, SWT.NONE);
	    newItem.setText("New subnode..");
	    newItem.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {
	    		TreeItem [] selectedItems = tree.getSelection();
	    		
	    		if (selectedItems == null || selectedItems.length == 0) {
	    			return;
	    		}
	    		
	    		TreeItem selectedItem = selectedItems[0];
	    		
	    		StringRequestOKCancelDialog dialog = new StringRequestOKCancelDialog(shell, "", "New Subnode", "New subnode name:");
	    		String s = dialog.open();
	    		
	    		if (s != null) {
	    			TreeItem jItem = new VCNTreeItem(selectedItem, 0);
	    			jItem.setText(s);
	    			selectedItem.setExpanded(true);
	    			vermilionCascadeNotebook.setModified();
	    		}
	    	}
	    });
	    
		MenuItem siblingItem = new MenuItem(popupMenu, SWT.NONE);
		siblingItem.setText("New sibling node..");
		siblingItem.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {
	    		TreeItem [] selectedItems = tree.getSelection();
	    		
	    		if (selectedItems == null || selectedItems.length == 0) {
	    			return;
	    		}
	    		
	    		TreeItem selectedItem = selectedItems[0];
	    		
	    		StringRequestOKCancelDialog dialog = 
	    				new StringRequestOKCancelDialog(shell, "", "New Sibling Node", "New sibling node name: ");
	    		String s = dialog.open();
	    		
	    		if (s != null) {
	    			TreeItem jItem;
	    			
	    			TreeItem parentItem = selectedItem.getParentItem();
	    			if (parentItem == null) {
	    				jItem = new VCNTreeItem(tree, 0);
	    			} else {
	    				jItem = new VCNTreeItem(parentItem, 0);
	    			}

	    			jItem.setText(s);
	    			
	    			vermilionCascadeNotebook.setModified();
	    		}
	    	}
	    });
	    
	    
	    MenuItem renameItem = new MenuItem(popupMenu, SWT.NONE);
	    renameItem.setText("Rename node..");
	    renameItem.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {
	    		TreeItem [] selectedItems = tree.getSelection();
	    		
	    		if (selectedItems == null || selectedItems.length == 0) {
	    			return;
	    		}
	    		
	    		TreeItem selectedItem = selectedItems[0];
	    		
	    		StringRequestOKCancelDialog dialog = 
	    				new StringRequestOKCancelDialog(shell, selectedItem.getText(), 
	    						"Rename Node", "New node name: ");
	    		String s = dialog.open();
	    		
	    		if (s != null) {
	    			selectedItem.setText(s);
	    			
	    			vermilionCascadeNotebook.setModified();
	    		}
	    	}
	    });
	    
	    
	    MenuItem deleteItem = new MenuItem(popupMenu, SWT.NONE);
	    deleteItem.setText("Delete node..");
	    deleteItem.addSelectionListener(new SelectionAdapter() {
	    	public void widgetSelected(SelectionEvent e) {
	    		
	    		TreeItem [] selectedItems = tree.getSelection();
	    		
	    		if (selectedItems == null || selectedItems.length == 0) {
	    			return;
	    		}
	    		
	    		TreeItem selectedItem = selectedItems[0];
	    		
	    		MessageOKCancelDialog.DialogCase result = 
	    				new MessageOKCancelDialog(shell, 
	    						"Delete Item", 
	    						"Are you sure? Do you really want to delete the node " +
	    						"'" + selectedItem.getText() + "' ?").open();

	    		System.out.println("out = " + result);
	    		
				if (result == MessageOKCancelDialog.DialogCase.OK) {
					selectedItem.dispose();

					if (tree.getItems().length == 0) {
						TreeItem iItem = new VCNTreeItem(tree, 0);
						iItem.setText("root");
					}

					vermilionCascadeNotebook.setModified();
				}
	    	}
	    });
	    

//	    Menu newMenu = new Menu(popupMenu);
//	    newItem.setMenu(newMenu);

//	    MenuItem shortcutItem = new MenuItem(newMenu, SWT.NONE);
//	    shortcutItem.setText("Shortcut");
//	    MenuItem iconItem = new MenuItem(newMenu, SWT.NONE);
//	    iconItem.setText("Icon");
	    
	    tree.setMenu(popupMenu);
	}
	
	
}
