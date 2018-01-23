package Communication;

import java.util.LinkedList;
import java.util.Scanner;

import globals.Globals;
import processManagement.process_control_block;

public class Communication
{
	//variables
	private process_control_block pcb; //process running
	public Communication(process_control_block pcb)
	{
		this.pcb = pcb;
	}
	
	//methods
	public int createPipe(String pipeName)
	{
		for(int i =0;i< Pipe.pipes.size();i++)
		{
			if(Pipe.pipes.get(i).getName().equals(pipeName) ) 
				{
				Globals.terminalArea.append("Communication: The pipe already exists.\n");
					return 0;
				}
		}
		Pipe.pipes.add(new Pipe(pipeName));
		Globals.terminalArea.append("Communication: Pipe created.\n");
		return 1;
	}
	public int deletePipe(String pipeName)
	{
		for(int i =0;i< Pipe.pipes.size();i++) // przegląd łącz
		{	
			if(Pipe.pipes.get(i).getName().equals(pipeName) ) 
			{
				Pipe.pipes.remove(i);
				Globals.terminalArea.append("Communication: Pipe deleted.\n");
				return 1;
			}
		}
		Globals.terminalArea.append("Communication: The pipe does not exist.\n");
		return 0;
	}
	public int readPipe(String pipeName, int numberOfSigns, int memoryAddress)
	{
		for(int i = 0;i< Pipe.pipes.size();i++) // przegad lacz
		{
			if(Pipe.pipes.get(i).getName().equals(pipeName)) // szukanie lacza o danej nazwie
			{
				Pipe.pipes.get(i).lock.lock(pcb);
				if(numberOfSigns > Pipe.pipes.get(i).getData().size())
				{
					Pipe.pipes.get(i).lock.unlock();
					Pipe.pipes.get(i).lock.addToQueue(pcb);
					return 2;
				}
				for(int j = 0;j < numberOfSigns; j++) // czytanie po jednym znaku
				{
					if(Pipe.pipes.get(i).data.size() == 0) 
						{	
							Pipe.pipes.get(i).lock.unlock();	// jezeli pusty to unlock		
							Pipe.pipes.get(i).lock.addToQueue(pcb);
							break;
						}
					else 
						{									
							pcb.pageTable.writeToMemory(memoryAddress + j, Pipe.pipes.get(i).data.removeFirst(), pcb.getID() ); // zapisywanie do pameci modul: NATALIA 
							Pipe.pipes.get(i).lock.unlock();
						}
					if(j == numberOfSigns-1) 
						{
							return 1;
						}
				}
				//Pipe.pipes.get(i).lock.unlock();
				
				return 2;
			}
			else 
			{	
				//Globals.terminalArea.append("Communication: The pipe does not exist.\n"); // TODO: komunikat czy nie
				return 2;
			}
		}
		//Globals.terminalArea.append("Communication: The pipe does not exist.\n"); // TODO: komunikat czy nie
		return 2;
		
	}
	public int writePipe(String pipeName, String message)
	{
		for(int i =0;i< Pipe.pipes.size();i++) // przeglad lacz
		{	
			if(Pipe.pipes.get(i).getName().equals(pipeName)) // szukanie lacza o danej nazwie
			{
					for(int j = 0; j < message.length(); j++) //dzielenie komunikatu na pojedyncze znaki i zapisywanie do lacza
					{
						Pipe.pipes.get(i).lock.lock(pcb);
						Pipe.pipes.get(i).data.add((Character)message.charAt(j));
						Pipe.pipes.get(i).lock.unlock();
					}
					Globals.terminalArea.append("Communication: Saved in pipe.\n");
				return 1;
			}
			else 
			{	
				//Globals.terminalArea.append("Communication: The pipe does not exist.\n");
				return 2;
			}
		}	
		//Globals.terminalArea.append("Communication: The pipe does not exist.\n");
		return 2;
	}
	public static String showAllPipes()
	{
		String result = "";
		result += "Pipes: ";
		if(Pipe.pipes.size() == 0)
		{
			result += "There are no pipes.";
		}
		else
		for(int i = 0;i< Pipe.pipes.size();i++)
		{	
			result += (Pipe.pipes.get(i).getName() + ": " + Pipe.pipes.get(i).getData().toString() + '\n');
		}
		return result;
	}
	
}