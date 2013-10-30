package ru.vermilion.vcn.app.capabilities;

import java.util.Timer;

import ru.vermilion.vcn.app.AutoSaveTimerTask;
import ru.vermilion.vcn.app.VermilionCascadeNotebook;

public class ProgramAutoSave implements ICapability {
	
	private Timer autoSaveTimer;
	
	public ProgramAutoSave(VermilionCascadeNotebook vermilionCascadeEditor) {
		autoSaveTimer = new Timer(true);
		autoSaveTimer.schedule(new AutoSaveTimerTask(vermilionCascadeEditor), 10000, 10000);
	}

}
