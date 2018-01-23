package mutexLock;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import globals.Globals;
import processManagement.process_control_block;

public class MutexLock {
	private Queue<process_control_block> queue;
	private static List<Boolean> LocksState = new ArrayList<Boolean>();
	private static int i=0;
	private int ID;
	private boolean isLocked;
	private process_control_block currentProcess;
	
	public MutexLock() {
		isLocked=false;
		ID = i;
		i++;
		queue = new LinkedList<process_control_block>();
		LocksState.add(isLocked);
	}
	
	public boolean isLocked() {
		return isLocked;
	}
	
	public static void printLocks() {
		Globals.terminalArea.append("Locks states: \n");
		for(int i=0;i<LocksState.size();i++) {
			Globals.terminalArea.append("Lock" + i + ": " + LocksState.get(i) + "\n");
		}
	}
	
	public void lock(process_control_block pcb) {
		//Jesli zamek jest odblokowany, ustaw aktualny proces w zamku na pcb i zamknij zamek
		if(isLocked == false) {
			isLocked = true;
			LocksState.set(ID, isLocked);
			currentProcess = pcb;
			Globals.terminalArea.append("Locked lock with pcb: " + pcb.getID() + "\n");
		}
		//Jesli zamek jest zablokowany, zmien stan procesu na oczekujacy i dodaj go do kolejki
		else {
			Globals.terminalArea.append("Lock locked! Adding " + pcb.getID() + " to queue.\n");
			//Dodac zmiane state procesu na waiting
			pcb.Setstan(1);
			queue.add(pcb);
		}
	}
	
	public void lock(process_control_block pcb, boolean isEmpty) {
		//Jesli potok jest pusty, dodaj do kolejki
		Globals.terminalArea.append("Lock locked! Adding " + pcb.getID() + " to queue.\n");
		//Dodac zmiane state procesu na waiting
		pcb.Setstan(1);
		queue.add(pcb);
	}
	
	public void unlock() {
		//Jesli kolejka jest pusta, otworz zamek
		if(queue.isEmpty()) {
			if(currentProcess != null) 
				Globals.terminalArea.append("CurrentProcess value before checking queue: " + currentProcess.getID() + ". Queue is empty. Unlocking lock.\n");
			isLocked = false;
			currentProcess = null;
			LocksState.set(ID, isLocked);
		}
		//Jesli kolejka nie jest pusta, wez kolejny proces z kolejki i zmien jego stan na gotowy
		else {
			Globals.terminalArea.append("Changing currentProcess from : " +  currentProcess.getID() + "\n");
			currentProcess = queue.poll();
			currentProcess.Setstan(0);
			Globals.terminalArea.append(" to: " +  currentProcess.getID() + "\n");
		}
	}
	
	public void addToQueue(process_control_block pcb)
	{
		queue.add(pcb);
		pcb.Setstan(1);
	}
	//Todo later
	/*public void unlock(boolean isEmpty) {
		//Jesli kolejka jest pusta, otworz zamek
		if(queue.isEmpty()) {
			Globals.terminalArea.append("CurrentProcess value before checking queue: " + currentProcess.getID() + ". Queue is empty. Unlocking lock.");
			isLocked = false;
			currentProcess = null;
		}
	}*/
	
	public void trylock(process_control_block pcb) {
		if(isLocked == false) {
			isLocked = true;
			LocksState.set(ID, isLocked);
			currentProcess = pcb;
		}
	}
}