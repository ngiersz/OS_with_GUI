package Communication;

import java.util.LinkedList;
import mutexLock.MutexLock;
public class Pipe
{
	private String name;
	protected MutexLock lock = new MutexLock();
	protected LinkedList<Character> data = new LinkedList<Character>();
	protected static LinkedList<Pipe> pipes = new LinkedList<Pipe>();
	public Pipe(String name)
	{
		this.name = name;
	}
	
	public String getName()
	{
		return name;
	}
	public LinkedList<Character> getData()
	{
		return data;
	}

}