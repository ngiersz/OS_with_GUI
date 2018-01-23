package Shell;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import Interpreter.Interpreter;
import processManagement.process_control_block;
import processManagement.ProcessManagment;
import processorManager.ProcessorManager;
import Communication.Communication;

import fileManagement.FileSystem;
import memoryManagement.Memory;

import globals.Globals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.text.AbstractDocument.Content;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

//TODO: wpisywanie do pliku informacji przy jego tworzeniu "Wrong command"
//TODO: nie ma b³êdu jak wpiszesz cf nazwa cokolwiek
//TODO: cf-e ggg 
public class ShellGUI extends JFrame
{
	
	private BufferedReader UserInput;
	private String User_Command;
	boolean ScriptFlag;
	boolean WrongCFlag;
	private Interpreter interpreter;
	private Memory memory;
	//public static process_control_block runningProcess = new process_control_block();
	private ProcessorManager processormanager;
	private FileSystem filesystem;
	public process_control_block  maka = new process_control_block();
	private ProcessManagment processManagment;

	
	public Action inputListener;
	public Action inputListenerTemp;
	public JOptionPane fileContent;
	
	public boolean done = false;
	JTextArea memoryArea = new JTextArea(0,0);
	JTextArea diskArea = new JTextArea(0,0);
	JTextArea pipesArea = new JTextArea(0,0);
	JTextArea processesArea = new JTextArea(0,0);
	JTextArea registersArea = new JTextArea(0,0);
	JTextField inputField = new JTextField();
	
	

	public ShellGUI()  throws IOException
	{
		memory = new Memory();
		filesystem = new FileSystem();
		processManagment = new ProcessManagment(memory);
		interpreter = new Interpreter(memory,processManagment, filesystem);
		processormanager = new ProcessorManager(interpreter, processManagment);
		ScriptFlag = false;
		WrongCFlag = false;
		UserInput = new BufferedReader(new InputStreamReader(System.in));
		
	
		
		GridBagLayout layout = new GridBagLayout();
		setLayout(layout);
		
	     
		
		
		//memory
		JPanel memoryPanel = new JPanel();
		memoryPanel.setBackground(Color.GRAY);
		memoryPanel.setLayout(new GridLayout(0,1));
		Border memoryBorder = BorderFactory.createTitledBorder("Memory");
		memoryPanel.setBorder(memoryBorder);
		memoryArea.setEditable(false);
		JScrollPane memoryScroll = new JScrollPane(memoryArea);
		memoryPanel.add(memoryScroll);

		
		//disk
		JPanel diskPanel = new JPanel();
		diskPanel.setBackground(Color.GRAY);
		diskPanel.setLayout(new GridLayout(0,1));
		Border diskBorder = BorderFactory.createTitledBorder("Disk");
		diskPanel.setBorder(diskBorder);
		diskArea.setEditable(false);
		JScrollPane diskScroll = new JScrollPane(diskArea);
		diskPanel.add(diskScroll);
		
		
		//pipes
		JPanel pipesPanel = new JPanel();
		pipesPanel.setBackground(Color.GRAY);
		pipesPanel.setLayout(new GridLayout(0,1));
		Border pipesBorder = BorderFactory.createTitledBorder("Pipes");
		pipesPanel.setBorder(pipesBorder);	
		pipesArea.setEditable(false);
		JScrollPane pipesScroll = new JScrollPane(pipesArea);
		pipesPanel.add(pipesScroll);
		
		//terminal
		JPanel terminalPanel = new JPanel();
		terminalPanel.setBackground(Color.GRAY);
		terminalPanel.setLayout(new GridLayout(0,1));
		Border terminalBorder = BorderFactory.createTitledBorder("Terminal");
		terminalPanel.setBorder(terminalBorder);	
		JScrollPane terminalScroll = new JScrollPane(Globals.terminalArea);
		terminalPanel.add(terminalScroll);

		this.getContentPane().setBackground(Color.gray);
		
		//input listener
		inputListener = new AbstractAction("Input")
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Globals.terminalArea.append('>' + inputField.getText() + '\n');
				User_Command = inputField.getText().trim();
				inputField.setText("");
				try
				{
					READCOMM();
				} catch (IOException e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				if(!(User_Command.isEmpty()) && User_Command.length() == 4 && User_Command.charAt(0) == 'e' && User_Command.charAt(1) == 'x' && User_Command.charAt(2) == 'i' && User_Command.charAt(3) == 't')
				{
					//break;
				}
				if(!(WrongCFlag))
				{
				if(ScriptFlag)
				{
						try
						{
							SCRIPTEXEC();
						} catch (IOException | InterruptedException e1)
						{
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
				} else
					try
					{
						COMMANDEXEC();
					} catch (IOException | InterruptedException e1)
					{
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				//System.out.println(User_Command.length());
				}
				
				ScriptFlag = false;
				WrongCFlag = false;
				updateGUI();
			}
		};
		
		inputField.setAction(inputListener);
		//input
		JPanel inputPanel = new JPanel();
		inputPanel.setBackground(Color.GRAY);
		inputPanel.setLayout(new GridLayout(0,1));
		Border inputBorder = BorderFactory.createTitledBorder("Input");
		inputPanel.setBorder(inputBorder);	
		inputPanel.add(inputField);
		

		
		//processes
		JPanel processesPanel = new JPanel();
		processesPanel.setLayout(new GridLayout(0,1));
		processesPanel.setBackground(Color.GRAY);
		Border processesBorder = BorderFactory.createTitledBorder("Processes");
		processesPanel.setBorder(processesBorder);		
		processesArea.setEditable(false);
		JScrollPane processesScroll = new JScrollPane(processesArea);
		processesPanel.add(processesScroll);
		
		//registers
		JPanel registersPanel = new JPanel();
		registersPanel.setBackground(Color.GRAY);
		registersPanel.setLayout(new GridLayout(0,1));
		Border registersBorder = BorderFactory.createTitledBorder("Registers");
		registersPanel.setBorder(registersBorder);	
		registersArea.setEditable(false);
		JScrollPane registersScroll = new JScrollPane(registersArea);
		registersPanel.add(registersScroll);

		/*memoryArea.setLineWrap(true);
		memoryArea.setWrapStyleWord(true);
		diskArea.setLineWrap(true);
		diskArea.setWrapStyleWord(true);
		pipesArea.setLineWrap(true);
		pipesArea.setWrapStyleWord(true);
		Globals.terminalArea.setLineWrap(true);
		Globals.terminalArea.setWrapStyleWord(true);*/
		
		JButton but = new JButton("ggg");
		//but.addActionListener(listener);
		add(but, new GBC(0,0));
		add(terminalPanel, new GBC(0,1,3,4).setAnchor(GBC.CENTER).setFill(GBC.BOTH).setWeight(100, 100));
		add(registersPanel, new GBC(3,0).setAnchor(GBC.NORTH).setFill(GBC.BOTH).setWeight(50,50));
		add(processesPanel, new GBC(3,1).setAnchor(GBC.NORTH).setFill(GBC.BOTH).setWeight(50,100));
		add(memoryPanel, new GBC(3,2).setAnchor(GBC.NORTH).setFill(GBC.BOTH).setWeight(50,100));
		add(diskPanel, new GBC(3,3).setAnchor(GBC.NORTH).setFill(GBC.BOTH).setWeight(50,100));
		add(pipesPanel, new GBC(3,4).setAnchor(GBC.NORTH).setFill(GBC.BOTH).setWeight(50,30));
		add(inputPanel, new GBC(0,5).setAnchor(GBC.CENTER).setFill(GBC.BOTH).setWeight(100, 10));
		
		pack();
		updateGUI();
	}
	
	public void updateGUI()
	{
		memoryArea.setText(memory.print());
		pipesArea.setText(Communication.showAllPipes());
		registersArea.setText(interpreter.Show_Regs());
		processesArea.setText(processManagment.print_procesy());

		
		//show disk
		String disk = "";
		disk += ("Free memory: " + 32*filesystem.numberOfFreeBlocks() + "\n");
		disk += ("Memory used: " + 32*(32 - filesystem.numberOfFreeBlocks()) + "\n");
		disk += filesystem.showFAT();
		disk += filesystem.showData();
		disk += filesystem.showBitVector();
		diskArea.setText(disk);
		//memoryArea.repaint();
		
		

	}

	
	public static void main(String[] args) throws IOException
	{

		ShellGUI shell = new ShellGUI();
		EventQueue.invokeLater(()->
		{
			try 
			{
				JFrame frame = shell;
				frame.setTitle("TigerOS");
				frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
	
			}
			catch(Exception excep) 
			{
				System.out.println("Error: " + excep.getMessage() + excep);
			}
			
		});
		
		/*while(true)
		{

			try
			{
				shell.READCOMM();
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!(shell.User_Command.isEmpty()) && shell.User_Command.length() == 4 && shell.User_Command.charAt(0) == 'e' && shell.User_Command.charAt(1) == 'x' && shell.User_Command.charAt(2) == 'i' && shell.User_Command.charAt(3) == 't')
			{
				//break;
			}
			if(!(shell.WrongCFlag))
			{
			if(shell.ScriptFlag)
			{
					shell.SCRIPTEXEC();
			} else
				shell.COMMANDEXEC();

			//System.out.println(User_Command.length());
			}
			
			shell.ScriptFlag = false;
			shell.WrongCFlag = false;
		}*/
	}
	
	
	
	void SCRIPTEXEC() throws IOException, InterruptedException
	{
		
		try(BufferedReader br = new BufferedReader(new FileReader(User_Command.substring(2) + ".sch"))) 
		{
            User_Command = br.readLine();
            while(User_Command != null)
            {
                COMMANDEXEC();
                User_Command = br.readLine();
                if(!(User_Command.isEmpty()) && User_Command.length() == 4 && User_Command.charAt(0) == 'e' && User_Command.charAt(1) == 'x' && User_Command.charAt(2) == 'i' && User_Command.charAt(3) == 't')
    			{
    				break;
    			}
                
            }
		}
		catch(IOException no_f)
		{
			Globals.terminalArea.append("No file found\n");
		}
	
	}
	void COMMANDEXEC() throws IOException, InterruptedException
	{
		if(User_Command.length()==2)
		{
		switch(User_Command)
		{
		    //load program
			case("p1"):
			{
				processManagment.create_process("P1", 60,  "Prog1", memory);
				break;
			}
			case("p2"):
			{
				processManagment.create_process("P2", 60, "Prog2", memory);
				break;
			}
			case("p3"):
			{
				processManagment.create_process("P3", 50, "Prog3", memory);
				break;
			}
			/*case("p4"):
			{
				processManagment.create_process("P4", 30, "Prog4", memory);
				break;
			}
			case("p5"):
			{
				processManagment.create_process("P5", 30, "Prog5", memory);
				break;
			}*/
			case("cp"): //TODO:
			{
				Globals.terminalArea.append("Please enter the name of the process:\n");
				String S1 = UserInput.readLine().trim();
				Globals.terminalArea.append("Please enter the size of the process:\n");
				int size = Integer.parseInt(UserInput.readLine().trim());
				Globals.terminalArea.append("Please enter the name of the file with program:\n");
				String S2 = UserInput.readLine().trim();
				processManagment.create_process(S1, size, S2, memory);
				break;
			}
			default:
			{
				Globals.terminalArea.append("Wrong Command\n");
			}	
		}
		}
		
		if(User_Command.length()==3)
		{
		switch(User_Command)
		{
			case("lsf"):
			{
				Globals.terminalArea.append(filesystem.showMainCatalog() + "\n");
				break;
			}
			default:
			{
				Globals.terminalArea.append("Wrong Command\n");
			}
		}
		}
		
		if(User_Command.length() > 3)
		{
			if(User_Command.charAt(0)=='r' && User_Command.charAt(1)=='m'  && User_Command.length() > 4 && User_Command.charAt(2)=='f')
			{
				//delete file
				filesystem.deleteFile(User_Command.substring(4));
				return;
			}
			if(User_Command.charAt(0)=='c' && User_Command.charAt(1)=='f' )
			{
				//create file
				if(User_Command.charAt(3) == '-' && User_Command.length()>6)
				{
					if(User_Command.charAt(4) == 'e') filesystem.createEmptyFile(User_Command.substring(6));
					return;
				}
				else
				{
					if(User_Command.charAt(2) == ' ') 
					{

						Globals.terminalArea.append("Enter the content of file:\n");
						String content;
						content = fileContent.showInputDialog("Enter the content of file: ");
						if (content==null) content="";
						filesystem.createFile(User_Command.substring(3), content);
						/*inputListenerTemp = new AbstractAction("InputTemp") // zmiana akcji pola inputListener, aby wpisac zawartosc pliku
						{
								@Override
								public void actionPerformed(ActionEvent e)
								{
									inputField.removeActionListener(inputListenerTemp);
									inputField.removeActionListener(inputListener);
									String content = inputField.getText(); //UserInput.readLine(); TODO:
									filesystem.createFile(User_Command.substring(3), content);
									inputField.addActionListener(inputListener);
									inputField.setText("");
									updateGUI();
									//this.notifyAll();
								}
						};
						inputField.removeActionListener(inputListener);
						inputField.addActionListener(inputListenerTemp);*/
						//this.wait();

							
						return;
					}
					System.out.println("Wrong Command");
					Globals.terminalArea.append("Wrong Command\n");
					return;
				}	
			}
			if(User_Command.charAt(0)=='l' && User_Command.charAt(1)=='s'  && User_Command.length() > 4 && User_Command.charAt(2)=='f')
			{
				if(User_Command.charAt(5) == 'r')
				filesystem.showRootEntry(User_Command.substring(7));
			}
		}
		
		if(User_Command.length()==4)
		{
		switch(User_Command)
		{
			case("help"):
			{
				Globals.terminalArea.append("Commands:\n");
				
				Globals.terminalArea.append("p1   load Prog1 as P1 with size of 60\n");
				Globals.terminalArea.append("p2   load Prog2 as P2 with size of 60\n");
				Globals.terminalArea.append("p3   load Prog3 as P3 with size of 50\n\n");
				
				Globals.terminalArea.append("cp   load program and fill in variables\n");
				Globals.terminalArea.append("cp x y z    load z as x with size of y\n\n");
				Globals.terminalArea.append("lsf   display root");
				Globals.terminalArea.append("lsf -r   displays specific root entry\n");
				
				Globals.terminalArea.append("\ncf  x   create file x\n");
				Globals.terminalArea.append("cf -e x   create empty file x\n\n");
				Globals.terminalArea.append("rmf x   remove file x\n\n");
				
				
				Globals.terminalArea.append("memo   display memory status\n");
				Globals.terminalArea.append("regs   display registers\n");
				Globals.terminalArea.append("step   execute one command\n");
				
				Globals.terminalArea.append("wait   execute commands until one program ends\n");
				Globals.terminalArea.append("pipe   displays all pipes\n");
				Globals.terminalArea.append("\nlsp -a   lists all processes\n");
				Globals.terminalArea.append("lsp -c   lists current process\n");
				Globals.terminalArea.append("lsp -r   lists ready processes\n");
				Globals.terminalArea.append("lsp -w   lists waiting processes\n\n");
				
				Globals.terminalArea.append("\ndisk -a   displays FAT table, bit vector, data currently on disk and free and taken memory\n");
				Globals.terminalArea.append("disk -f   displays FAT table\n");
				Globals.terminalArea.append("disk -d   displays data currently on disk\n");
				Globals.terminalArea.append("disk -b   displays bit vector\n");
				Globals.terminalArea.append("disk -m   displays the usage of the memory\n");
				Globals.terminalArea.append("disk -s   displays the content of one block\n\n");
				break;
			}
			//print memory
			case("memo"):
			{
				Globals.terminalArea.append(memory.print() + '\n');
				break;
			}
			//show registers
			case("regs"):
			{
				Globals.terminalArea.append(interpreter.Show_Regs() + '\n');
				break;
			}
			//1 step on processor
			case("step"):
			{
				processormanager.Scheduler();
				break;
			}
			case("wait"):
			{
				while(!interpreter.Flag_End)
				{
					processormanager.Scheduler();
					if(processormanager.Running.getID()==0) break;
				}
				interpreter.Flag_End = false;
				break;
			}
			case("pipe"):
			{
				Globals.terminalArea.append(Communication.showAllPipes() + '\n');
				break;
			}
			default:
			{
				Globals.terminalArea.append("Wrong Command\n");
			}
		}
		}
		
		if(User_Command.length() == 6)
		{
		switch(User_Command.substring(0, 3))
		{
		//list processes
			case("lsp"):
			{
			switch(User_Command.charAt(5))
			{
				case('c'):
				{
					Globals.terminalArea.append(processormanager.showRunning() + '\n');
					break;
				}
				case('r'):
				{
					Globals.terminalArea.append(processManagment.print_procesy_gotowe() + '\n');
					break;
				}
				case('w'):
				{
					Globals.terminalArea.append(processManagment.print_procesy_oczekujace() + '\n');
					break;
				}
				case('a'):
				{
					Globals.terminalArea.append(processManagment.print_procesy() + '\n');
					break;
				}
				default:
				{
					Globals.terminalArea.append("Wrong Command\n");
					break;
				}
			}
			}	
		}
		}
		
		
		
			
		if(User_Command.length() >= 7 && User_Command.charAt(0)=='d' && User_Command.charAt(1)!='p')
		{
		switch(User_Command.substring(0, 4))
		{
			case("disk"):
			{
				switch(User_Command.charAt(6))
				{
				case('f'):
				{
					//print FAT table
					Globals.terminalArea.append(filesystem.showFAT() + '\n');
					break;
				}
				case('d'):
				{
					//print data on disk
					Globals.terminalArea.append(filesystem.showData() + '\n');
					break;
				}
				case('b'):
				{
					//show bitVector
					Globals.terminalArea.append(filesystem.showBitVector() + '\n');
					break;
				}
				case('m'):
				{
					//show memory usage
					Globals.terminalArea.append("Free memory: " + 32*filesystem.numberOfFreeBlocks() + '\n');
					Globals.terminalArea.append("Memory used: " + 32*(32 - filesystem.numberOfFreeBlocks()) + '\n');
					break;	
				}
				/*case('r'):
				{
					filesystem.showRootEntry(User_Command.substring(8));
					break;
				}*/
				case('s'):
				{
					int x = Integer.parseInt(User_Command.substring(8));
					Globals.terminalArea.append(filesystem.showBlockContent(x) + '\n');
					break;
				}
				case('a'):
				{
					Globals.terminalArea.append("Free memory: " + 32*filesystem.numberOfFreeBlocks() + '\n');
					Globals.terminalArea.append("Memory used: " + 32*(32 - filesystem.numberOfFreeBlocks()) + '\n');
					Globals.terminalArea.append(filesystem.showFAT() + '\n');
					Globals.terminalArea.append(filesystem.showData() + '\n');
					Globals.terminalArea.append(filesystem.showBitVector() + '\n');
					break;
				}
				default:
				{
					Globals.terminalArea.append("Wrong Command\n");
				}
				}
			}
		}
		}
		
		if(User_Command.length() > 7 && User_Command.charAt(0)=='c' && User_Command.charAt(1)=='p')
		{
			int index = 3;
			String S1 = " ";
			String S2 = " ";
			String S3 = " ";
			while(true)
			{										//cp ania 120 prog7
				S1 += User_Command.charAt(index);
				index++;
				if(User_Command.charAt(index) == ' ')
				{
					index++;
					break;
				}
			}
			while(true)
			{
				S2 += User_Command.charAt(index);
				index++;
				if(User_Command.charAt(index) == ' ')
				{
					index++;
					break;
				}
			}
			S3 = User_Command.substring(index);
			S1.trim();
			S2.trim();
			S3.trim();
			//System.out.println(S1.substring(1));
			int size = Integer.parseInt(S2.substring(1));
			//System.out.println(size);
			//System.out.println(S3);
			processManagment.create_process(S1.substring(1), size, S3, memory);
			
			
		}
		
		
		
	}
	void READCOMM() throws IOException
	{
		
		//User_Command = UserInput.readLine().trim();
		if(User_Command.length() < 2)
		{
			WrongCFlag = true;
			Globals.terminalArea.append("Wrong\n");
			
		}
		else
		{String Scriptcheck = User_Command.substring(0, 2);
		if(Scriptcheck.contains("./"))
		{
			ScriptFlag = true;
		}
		else ScriptFlag = false;
		}
		
	}
	public static void Set_Running()
	{
		
	}
	void print()
	{
		Set_Running();
	}
}

