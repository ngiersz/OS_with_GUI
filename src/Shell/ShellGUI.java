package Shell;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import Interpreter.Interpreter;
import processManagement.process_control_block;
import processManagement.ProcessManagment;
import processManagement.lista_procesow;
import processorManager.ProcessorManager;
import Communication.Communication;

import fileManagement.FileSystem;
import memoryManagement.Memory;
import mutexLock.MutexLock;
import globals.Globals;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.List;
import java.awt.SystemColor;
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
import javax.swing.text.DefaultCaret;
import javax.swing.JLabel;
import javax.swing.JOptionPane;


//TODO: wpisywanie do pliku informacji przy jego tworzeniu "Wrong command"
//TODO: nie ma b³êdu jak wpiszesz cf nazwa cokolwiek
//TODO: cf-e ggg 

public class ShellGUI extends JFrame
{
	int fontSize=15;
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

	public Action rootListener;
	public Action inputListener;
	public JOptionPane fileContent;
	
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
		DefaultCaret memoryCaret = (DefaultCaret)memoryArea.getCaret(); //pozycja scrolla niezmieniana automatycznie
		memoryCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		memoryPanel.setLayout(new GridLayout(0,1));
		Border memoryBorder = BorderFactory.createTitledBorder("Memory");
		memoryPanel.setBorder(memoryBorder);
		memoryArea.setEditable(false);
		JScrollPane memoryScroll = new JScrollPane(memoryArea);
		memoryPanel.add(memoryScroll);

		
		//disk
		JPanel diskPanel = new JPanel();
		diskPanel.setBackground(Color.GRAY);
		DefaultCaret diskCaret = (DefaultCaret)diskArea.getCaret(); //pozycja scrolla niezmieniana automatycznie
		diskCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		diskPanel.setLayout(new GridLayout(0,1));
		Border diskBorder = BorderFactory.createTitledBorder("Disk");
		diskPanel.setBorder(diskBorder);
		diskArea.setEditable(false);
		JScrollPane diskScroll = new JScrollPane(diskArea);
		diskPanel.add(diskScroll);
		
		
		//pipes
		JPanel pipesPanel = new JPanel();
		pipesPanel.setBackground(Color.GRAY);
		DefaultCaret pipesCaret = (DefaultCaret)pipesArea.getCaret(); //pozycja scrolla niezmieniana automatycznie
		pipesCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		pipesPanel.setLayout(new GridLayout(0,1));
		Border pipesBorder = BorderFactory.createTitledBorder("Pipes");
		pipesPanel.setBorder(pipesBorder);	
		pipesArea.setEditable(false);
		JScrollPane pipesScroll = new JScrollPane(pipesArea);
		pipesPanel.add(pipesScroll);
		
		//terminal
		JPanel terminalPanel = new JPanel();
		terminalPanel.setBackground(Color.GRAY);
		int len = Globals.terminalArea.getDocument().getLength();
		Globals.terminalArea.setCaretPosition(len);
		terminalPanel.setLayout(new GridLayout(0,1));
		Border terminalBorder = BorderFactory.createTitledBorder("Terminal");
		terminalPanel.setBorder(terminalBorder);	
	    Globals.terminalArea.setEditable(false);
		JScrollPane terminalScroll = new JScrollPane(Globals.terminalArea);
		terminalPanel.add(terminalScroll);

		//last command
		JPanel lastCommandPanel = new JPanel();
		lastCommandPanel.setBackground(Color.GRAY);
		lastCommandPanel.setLayout(new GridLayout(0,1));
		Border lastCommandBorder = BorderFactory.createTitledBorder("Last Command");
		lastCommandPanel.setBorder(lastCommandBorder);	
		Globals.lastCommandField.setEditable(false);
		lastCommandPanel.add(Globals.lastCommandField);
		
		this.getContentPane().setBackground(Color.gray);
		
		Globals.terminalArea.setBackground(Color.BLACK);
		Globals.terminalArea.setForeground(Color.GREEN);
		Globals.terminalArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize+5));
		
		pipesArea.setBackground(Color.BLACK);
		pipesArea.setForeground(Color.ORANGE);
		pipesArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));

		registersArea.setBackground(Color.BLACK);
		registersArea.setForeground(Color.ORANGE);
		registersArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));
		
		diskArea.setBackground(Color.BLACK);
		diskArea.setForeground(Color.RED);
		diskArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));

		memoryArea.setBackground(Color.BLACK);
		memoryArea.setForeground(Color.ORANGE);
		memoryArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));
		
		processesArea.setBackground(Color.BLACK);
		processesArea.setForeground(Color.RED);
		processesArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));
		
		inputField.setBackground(Color.BLACK);
		inputField.setForeground(Color.YELLOW);
		inputField.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));
		
		Globals.lastCommandField.setBackground(Color.BLACK);
		Globals.lastCommandField.setForeground(Color.YELLOW);
		Globals.lastCommandField.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize));
		
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
					dispose();
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
		DefaultCaret processesCaret = (DefaultCaret)processesArea.getCaret(); //pozycja scrolla niezmieniana automatycznie
		processesCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		Border processesBorder = BorderFactory.createTitledBorder("Processes");
		processesPanel.setBorder(processesBorder);		
		processesArea.setEditable(false);
		JScrollPane processesScroll = new JScrollPane(processesArea);
		processesPanel.add(processesScroll);
		
		//registers
		JPanel registersPanel = new JPanel();
		registersPanel.setBackground(Color.GRAY);
		DefaultCaret registersCaret = (DefaultCaret)registersArea.getCaret(); //pozycja scrolla niezmieniana automatycznie
		registersCaret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
		registersPanel.setLayout(new GridLayout(0,1));
		Border registersBorder = BorderFactory.createTitledBorder("Registers");
		registersPanel.setBorder(registersBorder);	
		registersArea.setEditable(false);
		JScrollPane registersScroll = new JScrollPane(registersArea);
		registersPanel.add(registersScroll);
		
		
		//AboutDialog rootWindow = new AboutDialog();
		
		/*memoryArea.setLineWrap(true);
		memoryArea.setWrapStyleWord(true);
		diskArea.setLineWrap(true);
		diskArea.setWrapStyleWord(true);
		pipesArea.setLineWrap(true);
		pipesArea.setWrapStyleWord(true);
		Globals.terminalArea.setLineWrap(true);
		Globals.terminalArea.setWrapStyleWord(true);*/
		
		add(terminalPanel, new GBC(0,0,3,5).setAnchor(GBC.CENTER).setFill(GBC.BOTH).setWeight(100, 100));
		add(registersPanel, new GBC(3,0).setAnchor(GBC.NORTH).setFill(GBC.BOTH).setWeight(50,60));
		add(processesPanel, new GBC(3,1).setAnchor(GBC.NORTH).setFill(GBC.BOTH).setWeight(50,100));
		add(memoryPanel, new GBC(3,2).setAnchor(GBC.NORTH).setFill(GBC.BOTH).setWeight(50,100));
		add(diskPanel, new GBC(3,3).setAnchor(GBC.NORTH).setFill(GBC.BOTH).setWeight(70,100));
		add(pipesPanel, new GBC(3,4).setAnchor(GBC.NORTH).setFill(GBC.BOTH).setWeight(50,30));
		add(inputPanel, new GBC(0,5).setAnchor(GBC.CENTER).setFill(GBC.BOTH).setWeight(100, 10));
		add(lastCommandPanel, new GBC(3,5).setAnchor(GBC.CENTER).setFill(GBC.BOTH).setWeight(50, 10));

		pack();
		updateGUI();
		Globals.terminalArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, fontSize+5));
		Globals.terminalArea.append("		 ________  __                                       ______    ______  \r\n" + 
				"		/        |/  |                                     /      \\  /      \\ \r\n" + 
				"		$$$$$$$$/ $$/   ______    ______    ______        /$$$$$$  |/$$$$$$  |\r\n" + 
				"		   $$ |   /  | /      \\  /      \\  /      \\       $$ |  $$ |$$ \\__$$/ \r\n" + 
				"		   $$ |   $$ |/$$$$$$  |/$$$$$$  |/$$$$$$  |      $$ |  $$ |$$      \\ \r\n" + 
				"		   $$ |   $$ |$$ |  $$ |$$    $$ |$$ |  $$/       $$ |  $$ | $$$$$$  |\r\n" + 
				"		   $$ |   $$ |$$ \\__$$ |$$$$$$$$/ $$ |            $$ \\__$$ |/  \\__$$ |\r\n" + 
				"		   $$ |   $$ |$$    $$ |$$       |$$ |            $$    $$/ $$    $$/ \r\n" + 
				"		   $$/    $$/  $$$$$$$ | $$$$$$$/ $$/              $$$$$$/   $$$$$$/  \r\n" + 
				"		              /  \\__$$ |                                              \r\n" + 
				"		              $$    $$/                                               \r\n" + 
				"		               $$$$$$/                                                \n");
	
	}


	public void updateGUI()
	{
		memoryArea.setText(memory.print());
		pipesArea.setText(Communication.showAllPipes());
		registersArea.setText(interpreter.Show_Regs());
		processesArea.setText(processManagment.print_procesy());

		
		//show disk
		String disk = "";
		disk += filesystem.showMainCatalog();
		disk += ("Free memory: " + 32*filesystem.numberOfFreeBlocks() + "\n");
		disk += ("Memory used: " + 32*(32 - filesystem.numberOfFreeBlocks()) + "\n");
		disk += filesystem.showFAT();
		disk += filesystem.showData();
		disk += filesystem.showBitVector();
		diskArea.setText(disk);
		//memoryArea.repaint();
		int len = Globals.terminalArea.getDocument().getLength();
		Globals.terminalArea.setCaretPosition(len);
		

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
				return;
			}
			case("p2"):
			{
				processManagment.create_process("P2", 60, "Prog2", memory);
				return;
			}
			case("p3"):
			{
				processManagment.create_process("P3", 50, "Prog3", memory);
				return;
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
			case("cp"):
			{
				Globals.terminalArea.append("Please enter the name of the process:\n");
				String S1;
				S1 = fileContent.showInputDialog("Please enter the name of the process: ");
				if (S1==null) S1="";
				
				Globals.terminalArea.append("Please enter the size of the process:\n");
				String sizeS;
				sizeS = fileContent.showInputDialog("Please enter the size of the process: ");
				if (sizeS==null) sizeS="";
				int size = Integer.parseInt(sizeS.trim());
				
				Globals.terminalArea.append("Please enter the name of the file with program:\n");
				String S2;
				S2 = fileContent.showInputDialog("Please enter the name of the file with program: ");
				if (S2==null) S2="";
				processManagment.create_process(S1, size, S2, memory);
				
				return;
			}
			default:
			{
				
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
				return;
			}
			default:
			{
				
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
					Globals.terminalArea.append("Wrong Command\n");
					return;
				}	
			}
			if(User_Command.charAt(0)=='l' && User_Command.charAt(1)=='s'  && User_Command.length() > 4 && User_Command.charAt(2)=='f')
			{
				if(User_Command.charAt(5) == 'r')
					{Globals.terminalArea.append(filesystem.showRootEntry(User_Command.substring(7)));
				return;}
			}
		}
		
		if(User_Command.length()==4)
		{
		switch(User_Command)
		{
			case("help"):
			{
				Globals.terminalArea.append("Commands:\n");
				
				Globals.terminalArea.append("p1\t\tload Prog1 as P1 with size of 60\n");
				Globals.terminalArea.append("p2\t\tload Prog2 as P2 with size of 60\n");
				Globals.terminalArea.append("p3\t\tload Prog3 as P3 with size of 50\n\n");
				
				Globals.terminalArea.append("cp\t\tload program and fill in variables\n");
				Globals.terminalArea.append("cp x y z\tload z as x with size of y\n\n");
				Globals.terminalArea.append("lsf\t\tdisplay root\n");
				Globals.terminalArea.append("lsf -r\t\tdisplays specific root entry\n");
				
				Globals.terminalArea.append("\ncf  x\t\tcreate file x\n");
				Globals.terminalArea.append("cf -e x\t\tcreate empty file x\n\n");
				Globals.terminalArea.append("rmf x\t\tremove file x\n\n");
				
				
				Globals.terminalArea.append("memo\t\tdisplay memory status\n");
				Globals.terminalArea.append("kill\t\tkill running process\n");
				Globals.terminalArea.append("kill id\t\tkill process by ID\n");
				Globals.terminalArea.append("regs\t\tdisplay registers\n");
				Globals.terminalArea.append("step\t\texecute one command\n");
				Globals.terminalArea.append("read name\t\tshow content of the file\n");
				Globals.terminalArea.append("wait\t\texecute commands until one program ends\n");
				Globals.terminalArea.append("pipe\t\tdisplays all pipes\n");
				Globals.terminalArea.append("\nlsp -a\t\tlists all processes\n");
				Globals.terminalArea.append("lsp -c\t\tlists current process\n");
				Globals.terminalArea.append("lsp -r\t\tlists ready processes\n");
				Globals.terminalArea.append("lsp -w\t\tlists waiting processes\n\n");
				
				Globals.terminalArea.append("\ndisk -a\t\tdisplays FAT table, bit vector, data currently on disk and free and taken memory\n");
				Globals.terminalArea.append("disk -f\t\tdisplays FAT table\n");
				Globals.terminalArea.append("disk -d\t\tdisplays data currently on disk\n");
				Globals.terminalArea.append("disk -b\t\tdisplays bit vector\n");
				Globals.terminalArea.append("disk -m\t\tdisplays the usage of the memory\n");
				Globals.terminalArea.append("disk -s\t\tdisplays the content of one block\n\n");
				return;
			}
			//print memory
			case("lock"):
			{
				MutexLock.showLocks();
			
				return;
			}
			case("memo"):
			{
				Globals.terminalArea.append(memory.print() + '\n');
				return;
			}
			//show registers
			case("regs"):
			{
				Globals.terminalArea.append(interpreter.Show_Regs() + '\n');
				return;
			}
			//1 step on processor
			case("step"):
			{
				processormanager.Scheduler();
				return;
			}
			case("kill"):
			{
				if(processormanager.Running.getID() == 0)
				{
					Globals.terminalArea.append("Killing idle process is forbidden." + '\n');
					processormanager.Clear();
					return;
				}
				processormanager.Running.Setstan(2);
				return;
			}
			case("wait"):
			{
				while(!interpreter.Flag_End)
				{
					processormanager.Scheduler();
					if(processormanager.Running.getID()==0) break;
				}
				interpreter.Flag_End = false;
				return;
			}
			case("pipe"):
			{
				Globals.terminalArea.append(Communication.showAllPipes() + '\n');
				return;
			}
			default:
			{
				
			}
		}
		}
		
		if(User_Command.length() > 4 && User_Command.charAt(0)=='k' && User_Command.charAt(1)=='i' && User_Command.charAt(2)=='l' && User_Command.charAt(3)=='l' )
		{
			int id = Integer.parseInt(User_Command.substring(5));
			if(processManagment.getIstniejaceProcesy().getPCB_by_ID(id)!=null && id != 0)
			{
				processManagment.getIstniejaceProcesy().getPCB_by_ID(id).Setstan(2);
			}
			else if (id==0) Globals.terminalArea.append("Killing idle process is forbidden"); else Globals.terminalArea.append("No such process exists\n");
			processormanager.Clear();
			return;
		}
		if(User_Command.length() > 4 && User_Command.charAt(0)=='r' && User_Command.charAt(1)=='e' && User_Command.charAt(2)=='a' && User_Command.charAt(3)=='d' )
		{
			
			filesystem.readFile(User_Command.substring(5));
			
			return;
			
		}
		if(User_Command.length() > 4 && User_Command.charAt(0)=='o' && User_Command.charAt(1)=='p' && User_Command.charAt(2)=='e' && User_Command.charAt(3)=='n' )
		{
			filesystem.openFileWithOutProcess(User_Command.substring(5));
			
			return;
			
		}
		if(User_Command.length() > 5 && User_Command.charAt(0)=='c' && User_Command.charAt(1)=='l' && User_Command.charAt(2)=='o' && User_Command.charAt(3)=='s' && User_Command.charAt(4)=='e' )
		{
			filesystem.closeFileWithOutProcess(User_Command.substring(6));
			
			return;
			
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
					return;
				}
				case('r'):
				{
					Globals.terminalArea.append(processManagment.print_procesy_gotowe() + '\n');
					return;
				}
				case('w'):
				{
					Globals.terminalArea.append(processManagment.print_procesy_oczekujace() + '\n');
					return;
				}
				case('a'):
				{
					Globals.terminalArea.append(processManagment.print_procesy() + '\n');
					return;
				}
				default:
				{
					
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
					return;
				}
				case('d'):
				{
					//print data on disk
					Globals.terminalArea.append(filesystem.showData() + '\n');
					return;
				}
				case('b'):
				{
					//show bitVector
					Globals.terminalArea.append(filesystem.showBitVector() + '\n');
					return;
				}
				case('m'):
				{
					//show memory usage
					Globals.terminalArea.append("Free memory: " + 32*filesystem.numberOfFreeBlocks() + '\n');
					Globals.terminalArea.append("Memory used: " + 32*(32 - filesystem.numberOfFreeBlocks()) + '\n');
					return;
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
					return;
				}
				case('a'):
				{
					Globals.terminalArea.append("Free memory: " + 32*filesystem.numberOfFreeBlocks() + '\n');
					Globals.terminalArea.append("Memory used: " + 32*(32 - filesystem.numberOfFreeBlocks()) + '\n');
					Globals.terminalArea.append(filesystem.showFAT() + '\n');
					Globals.terminalArea.append(filesystem.showData() + '\n');
					Globals.terminalArea.append(filesystem.showBitVector() + '\n');
					return;
				}
				default:
				{
					
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
			return;
			
		}
		
		
		Globals.terminalArea.append("Wrong Command\n");
		
		
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

