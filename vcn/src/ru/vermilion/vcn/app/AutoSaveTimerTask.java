package ru.vermilion.vcn.app;

import java.util.TimerTask;

public class AutoSaveTimerTask extends TimerTask {

	private VermilionCascadeNotebook app;

	public AutoSaveTimerTask(VermilionCascadeNotebook app) {
		this.app = app;
	}

	public void run() {
		try {
			app.getShell().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (!app.getShell().isDisposed()) {
						app.save();
					}
				}
			});
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
