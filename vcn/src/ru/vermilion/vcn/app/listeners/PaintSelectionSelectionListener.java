package ru.vermilion.vcn.app.listeners;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import ru.vermilion.vcn.app.VermilionCascadeNotebook;
import ru.vermilion.vcn.app.staff.VCNTreeItem;

public class PaintSelectionSelectionListener implements Listener{

	@Override
	public void handleEvent(Event event) {
		event.detail &= ~SWT.HOT;
		if ((event.detail & SWT.SELECTED) != 0) {
			GC gc = event.gc;
			Rectangle area = VermilionCascadeNotebook.getInstance().getTree().getClientArea();

			int width = area.x + area.width - event.x;
			if (width > 0) {
				Region region = new Region();
				gc.getClipping(region);
				region.add(event.x, event.y, width, event.height);
				gc.setClipping(region);
				region.dispose();
			}
			gc.setAdvanced(true);
			if (gc.getAdvanced()) {
				gc.setAlpha(127);
			}
			Rectangle rect = event.getBounds();
			Color background = gc.getBackground();
			gc.setForeground(event.display.getSystemColor(SWT.COLOR_BLUE));
			gc.setBackground(event.display.getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			gc.fillGradientRectangle(0, rect.y, 450, rect.height, false);
			gc.setBackground(background);

			VCNTreeItem treeItem = (VCNTreeItem) event.item;
			gc.setForeground(treeItem.getForeground());

			event.detail &= ~SWT.SELECTED;
		}
	}

}
