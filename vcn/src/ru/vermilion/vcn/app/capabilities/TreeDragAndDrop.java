package ru.vermilion.vcn.app.capabilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import ru.vermilion.vcn.app.VermilionCascadeNotebook;
import ru.vermilion.vcn.app.staff.VCNTreeItem;

public class TreeDragAndDrop implements ICapability {

	private Tree tree;
	
	private VermilionCascadeNotebook vermilionCascadeNotebook;

	public TreeDragAndDrop(VermilionCascadeNotebook vermilionCascadeNotebook) {
		this.tree = vermilionCascadeNotebook.getTree();
		this.vermilionCascadeNotebook = vermilionCascadeNotebook;

		addDNDCapability();
	}

	private void addDNDCapability() {
		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
		int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;

		final DragSource source = new DragSource(tree, operations);
		source.setTransfer(types);
		final VCNTreeItem[] dragSourceItem = new VCNTreeItem[1];
		source.addDragListener(new DragSourceListener() {
			public void dragStart(DragSourceEvent event) {
				TreeItem[] selection = tree.getSelection();
				if (selection.length == 1) {
					event.doit = true;
					dragSourceItem[0] = (VCNTreeItem)selection[0];
				} else {
					event.doit = true;
				}
			};

			public void dragSetData(DragSourceEvent event) {
				event.data = dragSourceItem[0].getText();
			}

			public void dragFinished(DragSourceEvent event) {
				if (event.detail == DND.DROP_MOVE)
					dragSourceItem[0].dispose();
				dragSourceItem[0] = null;
				vermilionCascadeNotebook.setModified();
			}
		});

		DropTarget target = new DropTarget(tree, operations);
		target.setTransfer(types);
		target.addDropListener(new DropTargetAdapter() {
			public void dragOver(DropTargetEvent event) {
				event.feedback = DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
				if (event.item != null) {
					TreeItem item = (TreeItem) event.item;
					Point pt = tree.getDisplay().map(null, tree, event.x, event.y);
					Rectangle bounds = item.getBounds();
					if (pt.y < bounds.y + bounds.height / 3) {
						event.feedback |= DND.FEEDBACK_INSERT_BEFORE;
					} else if (pt.y > bounds.y + 2 * bounds.height / 3) {
						event.feedback |= DND.FEEDBACK_INSERT_AFTER;
					} else {
						event.feedback |= DND.FEEDBACK_SELECT;
					}
				}
			}

			public void drop(DropTargetEvent event) {
				VCNTreeItem sourceItem = dragSourceItem[0];
				if (event.data == null || sourceItem == null) {
					event.detail = DND.DROP_NONE;
					return;
				}
				
				// it self
				if (sourceItem == event.item) {
					event.detail = DND.DROP_NONE;
					return;
				}
				
				if (isInnerElement(sourceItem, event.item)) {
					System.out.println("Inner element!!");
					event.detail = DND.DROP_NONE;
					return;
				}
				
				System.out.println("sourceItem = " + sourceItem.getText());
				String text = (String) event.data;
				System.out.println("event.data = " + text);
				System.out.println("event.item = " + ((TreeItem) event.item));
				
				if (event.item == null) {
					copyTree(sourceItem, tree, null);
				} else {
					TreeItem item = (TreeItem) event.item;
					Point pt = tree.getDisplay().map(null, tree, event.x, event.y);
					Rectangle bounds = item.getBounds();
					TreeItem parent = item.getParentItem();
					if (parent != null) {
						TreeItem[] items = parent.getItems();
						int index = 0;
						for (int i = 0; i < items.length; i++) {
							if (items[i] == item) {
								index = i;
								break;
							}
						}
						if (pt.y < bounds.y + bounds.height / 3) {
							copyTree(sourceItem, parent, index);
						} else if (pt.y > bounds.y + 2 * bounds.height / 3) {
							copyTree(sourceItem, parent, index + 1);
						} else {
							copyTree(sourceItem, item, null);
						}
					} else {
						TreeItem[] items = tree.getItems();
						int index = 0;
						for (int i = 0; i < items.length; i++) {
							if (items[i] == item) {
								index = i;
								break;
							}
						}
						if (pt.y < bounds.y + bounds.height / 3) {
							copyTree(sourceItem, tree, index);
						} else if (pt.y > bounds.y + 2 * bounds.height / 3) {
							copyTree(sourceItem, tree, index + 1);
						} else {
							copyTree(sourceItem, item, null);
						}
					}

				}
			}
		});

	}
	
	private boolean isInnerElement(TreeItem sourceTreeItem, Object investigationItem) {
		boolean isInnerElement = false;
		
		assert sourceTreeItem != null;
		
		if (investigationItem == null) {
			return false;
		}
		
		if (sourceTreeItem == investigationItem) {
			return true;
		}
		
		for (TreeItem childItem : sourceTreeItem.getItems()) {
			isInnerElement |= isInnerElement(childItem, investigationItem);
		}
		
		return isInnerElement;
	}
	
	private void copyTree(VCNTreeItem sourceItem, Object destinationItem, Integer index) {
		assert sourceItem != null && destinationItem != null;
		
		VCNTreeItem newItem;
		
		if (destinationItem instanceof Tree) {
			if (index != null) {
				newItem = new VCNTreeItem((Tree) destinationItem, SWT.NONE, index);
			} else {
				newItem = new VCNTreeItem((Tree) destinationItem, SWT.NONE);
			}
		} else {
			if (index != null) {
				newItem = new VCNTreeItem((TreeItem) destinationItem, SWT.NONE, index);
			} else {
				newItem = new VCNTreeItem((TreeItem) destinationItem, SWT.NONE);
			}			
		}
//		newItem.setText(sourceItem.getText());
//		newItem.setContent(sourceItem.getContent());
		newItem.copyItemFields(sourceItem);
		
		for (TreeItem childItem : sourceItem.getItems()) {
			copyTree((VCNTreeItem)childItem, newItem, null);
		}
		
	}

}
