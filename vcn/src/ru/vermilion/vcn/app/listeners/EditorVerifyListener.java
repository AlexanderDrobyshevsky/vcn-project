package ru.vermilion.vcn.app.listeners;

import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Text;

import ru.vermilion.vcn.app.VermilionCascadeNotebook;

public class EditorVerifyListener implements VerifyListener {

	private VermilionCascadeNotebook vermilionCascadeNotebook;

	public EditorVerifyListener(VermilionCascadeNotebook vermilionCascadeNotebook) {
		this.vermilionCascadeNotebook = vermilionCascadeNotebook;
	}

	public void verifyText(VerifyEvent e) {
		StringBuffer buffer = new StringBuffer(((Text) e.getSource()).getText());
		buffer.replace(e.start, e.end, e.text);
		String newText = buffer.toString().trim();

		if (vermilionCascadeNotebook.getEditor().getText() != null
				&& !vermilionCascadeNotebook.getEditor().equals(newText)) {
			vermilionCascadeNotebook.setModified();
		}
	}

}
