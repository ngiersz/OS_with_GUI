package mutexLock;

import java.util.LinkedList;
import java.util.Queue;

import globals.Globals;
import processManagement.process_control_block;

public class MutexLock {
	private Queue<process_control_block> queue;
	public static int i=0;
	public int ID;
	private boolean isLocked;
	private process_control_block currentProcess;
	protected static LinkedList<MutexLock> locks = new LinkedList<MutexLock>();
	
	public MutexLock() {
		ID = i;
		i++;
		queue = new LinkedList<process_control_block>();
		locks.add(this);
	}
	
	public boolean isLocked() {
		return isLocked;
	}
	
	public void lock(process_control_block pcb) {
		//Jesli zamek jest odblokowany, ustaw aktualny proces w zamku na pcb i zamknij zamek
		if(isLocked == false) {
			isLocked = true;
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
			currentProcess = pcb;
		}
	}
	public static void showLocks() {
		Globals.terminalArea.append("Locks states: \n");
		for(int j=0;j<locks.size();j++) {
			 Globals.terminalArea.append("Lock" + locks.get(j).ID  + ": "  + " " +  locks.get(j).isLocked() + "\n");

		}
	}

	public static void deleteLock(int ID) {
		for(int j=0;j<locks.size();j++) {
			 if(locks.get(j).ID == ID) MutexLock.locks.remove(j);
		}
	}
}
