package processManagement;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import Communication.Communication;
import Interpreter.Interpreter;
import fileManagement.FileSystem;
import globals.Globals;
import memoryManagement.Memory;
import processorManager.ProcessorManager;

public class ProcessManagment {
	static lista_procesow istniejace_procesy;

	public ProcessManagment(Memory memory) {
		istniejace_procesy = new lista_procesow(memory); // tworzy liste procesow
	} // na ktorej juz jest proces bezczynnosci

	public int create_process(String nname,int size, String file_nam, Memory memory) {
		try {
			istniejace_procesy.add_to_list(new process_control_block(nname,size, file_nam, memory));
		} catch(Exception e) {
			Globals.terminalArea.append("CREATE PROCESS - nie utworzono procesu \n");
			return 0;
		}
		return 1;
	}

	public void delete_process(int id) {
		if (istniejace_procesy.delete_from_list(id) == 1)
			Globals.terminalArea.append("nie ma takiego procesu \n");
	}

	public void delete_process_iter(int iter) {
		istniejace_procesy.delete_on_iter(iter);
	}

	public String print_procesy() {
		String result = "";
		result += "\n";
		Globals.terminalArea.append("\n");
		for (int i = 0; i < istniejace_procesy.size(); i++) {
			istniejace_procesy.getPCB(i).print();
			//Globals.terminalArea.append("\n");
			
			result += istniejace_procesy.getPCB(i).print();
			result += "\n";
		}
		return result;
	}

	public String print_procesy_gotowe() {
		String result = "";
		result += "\n";
		Globals.terminalArea.append("\n");
		for (int i = 0; i < istniejace_procesy.size(); i++) {
			if (istniejace_procesy.getPCB(i).getStan() == 0) {
				istniejace_procesy.getPCB(i).print();
				Globals.terminalArea.append("\n");
				
				result += istniejace_procesy.getPCB(i).print();
				result += "\n";
			}
		}
		return result;
	}

	public String print_procesy_oczekujace() {
		String result = "";
		result += "\n";
		Globals.terminalArea.append("\n");
		for (int i = 0; i < istniejace_procesy.size(); i++) {
			if (istniejace_procesy.getPCB(i).getStan() == 1) {
				istniejace_procesy.getPCB(i).print();
				Globals.terminalArea.append("\n");
				
				result += istniejace_procesy.getPCB(i).print();
				result += "\n";
			}
		}
		return result;
	}

	public void usun_skonczone() {
		for (int i = 0; i < istniejace_procesy.size(); i++) {
			if (istniejace_procesy.getPCB(i).getStan() == 2)
				{istniejace_procesy.getPCB(i).pageTable.deleteFromMemory(istniejace_procesy.getPCB(i).getID());
				delete_process_iter(i);}
		}
	}

	public void sortuj_procesy() {
		istniejace_procesy.gotowe_na_poczatek();
	}

	public void uporzadkuj_procesy() {
		usun_skonczone();
		sortuj_procesy();
	}

	public static process_control_block getPCB(int ID) {
		return istniejace_procesy.getPCB_by_ID(ID);
	}

	public lista_procesow getIstniejaceProcesy() {
		return istniejace_procesy;
	}

}
