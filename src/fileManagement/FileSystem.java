package fileManagement;

import java.util.ArrayList;
import java.util.List;

import globals.Globals;
import mutexLock.MutexLock;
import processManagement.*;

public class FileSystem {
	// Final variables - parameters of memory.
	private final int DISK_SIZE = 1024;
	private final int BLOCK_SIZE = 32;
	private final int NUMBER_OF_BLOCKS = DISK_SIZE / BLOCK_SIZE;

	// Data structures.
	private char[] dataArea = new char[DISK_SIZE];
	private int[] fileAllocationTable = new int[NUMBER_OF_BLOCKS];
	private boolean[] bitVector = new boolean[NUMBER_OF_BLOCKS];
	private List<File> mainCatalog = new ArrayList<File>();

	// Fill dataArea & bitVector with default values.
	public FileSystem() {
		for (int i = 0; i < DISK_SIZE; i++) {
			dataArea[i] = ' ';
		}
		for (int i = 0; i < NUMBER_OF_BLOCKS; i++) {
			bitVector[i] = false;
		}
		for (int i = 0; i < NUMBER_OF_BLOCKS; i++) {
			fileAllocationTable[i] = -2;
		}
	}

	// Secondary method for checking value of file's last block index.
	private int indexOfLastBlock(String name) {
		int fileNumber = fileNumberSetter(name);
		int lastBlockIndex = mainCatalog.get(fileNumber).indexOfFirstBlock;
		int lastBlockValue = fileAllocationTable[lastBlockIndex];
		while (lastBlockValue != -1) {
			if (lastBlockValue == -2)
				break;
			lastBlockIndex = lastBlockValue;
			lastBlockValue = fileAllocationTable[lastBlockIndex];
		}
		return lastBlockIndex;
	}

	// Secondary method for finding first free block for new file.
	private int findFreeBlock() {
		int freeBlock = -1;
		for (int i = 0; i < NUMBER_OF_BLOCKS; i++) {
			if (bitVector[i] == false) {
				freeBlock = i;
				break;
			}
		}
		return freeBlock;
	}

	// Secondary method for checking if name of the new file is occupied.
	private boolean checkNameAvailability(String searchedName) {
		boolean available = true;
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name.equals(searchedName)) {
				available = false;
				break;
			}
		}
		return available;
	}

	// Open file. If it finds file with exact name changes its open flag to "true".
	public void openFile(String name, process_control_block processName) {
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name.equals(name)) {
				mainCatalog.get(i).lock.lock(processName);
				mainCatalog.get(i).open = true;
				Globals.terminalArea.append("FileSystem: File " + name + " opened.");
				break;
			}
		}
	}

	// Method needed for testing.
	public void openFileWithOutProcess(String name) {
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name.equals(name)) {
				if (!mainCatalog.get(i).open) {
					mainCatalog.get(i).open = true;
					break;
				} else
					break;
			}
		}
	}

	// Method needed for testing.
	public void closeFileWithOutProcess(String name) {
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name.equals(name)) {
				if (mainCatalog.get(i).open) {
					mainCatalog.get(i).open = false;
					mainCatalog.get(i).readChars = 0;
					break;
				} else
					break;
			}
		}
	}

	// Close file. If it finds file with exact name changes flag to false.
	public void closeFile(String name) {
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name.equals(name)) {
				mainCatalog.get(i).lock.unlock();
				mainCatalog.get(i).open = false;
				Globals.terminalArea.append("FileSystem: File " + name + " closed.\n");
				increaseNumberOfReadChars(name, 0);
				break;
			}
		}
	}

	// Return size of file.
	private int getFileSize(String name) {
		int size = 0;
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name.equals(name)) {
				size = mainCatalog.get(i).size;
				break;
			}
		}
		return size;
	}

	// Return size of file.
	private boolean getFileFlagStatus(String name) {
		boolean open = false;
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name.equals(name)) {
				open = mainCatalog.get(i).open;
				break;
			}
		}
		return open;
	}

	// Return firstIndex of file.
	private int getFileFirstIndex(String name) {
		int index = 0;
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name.equals(name)) {
				index = mainCatalog.get(i).indexOfFirstBlock;
				break;
			}
		}
		return index;
	}

	// Check if file have space in last block.
	private boolean isFileHaveSpace(String name) {
		boolean have = false;
		if (getFileSize(name) % BLOCK_SIZE != 0 || getFileSize(name) == 0)
			have = true;
		return have;
	}

	// Return number of free blocks.
	public int numberOfFreeBlocks() {
		int freeBlocks = 0;
		for (int i = 0; i < NUMBER_OF_BLOCKS; i++)
			if (bitVector[i] == false)
				freeBlocks++;
		return freeBlocks;
	}

	// Check if new data will fit in memory.
	private boolean isEnoughMemory(String name, int neededSpace) {
		boolean isEnough = false;
		int freeMemory = BLOCK_SIZE * (numberOfFreeBlocks() + 1);
		if (isFileHaveSpace(name)) {
			int size = getFileSize(name);
			int howMany = size % 32;
			freeMemory += howMany;
		}
		if (freeMemory >= neededSpace)
			isEnough = true;
		return isEnough;

	}

	// Return amount of free space in last block of file.
	private int getNumberOfFreeSpaceInLastBlock(String name) {
		int freeSpace = 0;
		freeSpace = BLOCK_SIZE - getFileSize(name) % BLOCK_SIZE;
		return freeSpace;
	}

	// Increase size of updated file.
	private void increaseFileSize(String name, int amount) {
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name.equals(name)) {
				mainCatalog.get(i).size += amount;
				break;
			}
		}
	}

	// Secondary method for setting file number in catalog.
	private int fileNumberSetter(String name) {
		int tempFileNumber = -1;
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name.equals(name)) {
				tempFileNumber = i;
				break;
			}
		}
		return tempFileNumber;
	}

	// Fill new block with data.
	private void fillNewBlock(int newBlock, String charTable) {
		int index = newBlock * BLOCK_SIZE;
		for (int i = 0; i < charTable.length(); i++, index++)
			dataArea[index] = charTable.charAt(i);
	}

	// Fill with data block which still has some free space to use.
	private void fillNotFullBlock(String name, String charTable) {
		int index = indexOfLastBlock(name) * BLOCK_SIZE + getFileSize(name) % BLOCK_SIZE;
		for (int i = 0; i < charTable.length(); i++) {
			dataArea[index] = charTable.charAt(i);
			index++;
		}
	}

	// Amplify file entry in FAT.
	private void amplifyEntry(String name, int newBlock) {
		fileAllocationTable[indexOfLastBlock(name)] = newBlock;
		fileAllocationTable[newBlock] = -1;

	}

	// Making amount of file blocks narrower by removing last FAT node.
	private void taperEntry(String name) {
		int lastBlock = indexOfLastBlock(name);
		if (lastBlock != getFileFirstIndex(name)) {
			for (int i = 0; i < NUMBER_OF_BLOCKS; i++) {
				if (fileAllocationTable[i] == lastBlock)
					fileAllocationTable[i] = -1;
			}
			fileAllocationTable[lastBlock] = -2;
		} else
			fileAllocationTable[lastBlock] = -2;
	}

	// Remove any informations from catalog about file.
	private void removeDirectoryEntry(String name) {
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name.equals(name)) {
				mainCatalog.remove(i);
			}
		}
	}

	// Increase value of read chars.
	private void increaseNumberOfReadChars(String name, int value) {
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name.equals(name)) {
				mainCatalog.get(i).readChars += value;
				break;
			}
		}
	}

	// Set value of read chars.
	private void setNumberOfReadChars(String name, int value) {
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name.equals(name)) {
				mainCatalog.get(i).readChars = value;
				break;
			}
		}
	}

	// Return number of read chars.
	private int getNumberOfReadChars(String name) {
		int readChars = 0;
		for (int i = 0; i < mainCatalog.size(); i++) {
			if (mainCatalog.get(i).name.equals(name)) {
				readChars = mainCatalog.get(i).readChars;
				break;
			}
		}
		return readChars;
	}

	// Creating a empty file.
	public int createEmptyFile(String name) {
		if (!checkNameAvailability(name)) {
			Globals.terminalArea.append("FileSystem: Name " + name + " is occupied\n");
			return 0;
		} else {
			int newBlock = findFreeBlock();
			if (newBlock != -1) {
				bitVector[newBlock] = true;
				File plik = new File(name, 0, newBlock);
				mainCatalog.add(plik);
				fileAllocationTable[newBlock] = -1;
				Globals.terminalArea.append("FileSystem: File " + name + " was created.\n");
				return 1;
			} else {
				Globals.terminalArea.append("FileSystem: Cannot find free block. File cannot be created.\n");
				return 0;
			}
		}
	}

	// Create file with data.
	public int createFile(String name, String content) {
		if (createEmptyFile(name) == 0)
			return 0;
		else {
			openFileWithOutProcess(name);
			appendToFile(name, content);
			for (int i = 0; i < mainCatalog.size(); i++) {
				if (mainCatalog.get(i).name.equals(name)) {
					mainCatalog.get(i).open = false;
					break;
				}
			}
			closeFileWithOutProcess(name);
			return 1;
		}

	}

	// Add text to file.
	public int appendToFile(String name, String content) {
		if (!getFileFlagStatus(name)) {
			Globals.terminalArea.append("FileSystem: Cannot append to file. File is closed.\n");
			return 0;
		}
		int indexChar = 0;
		String charTable = "";
		if (!checkNameAvailability(name)) {
			if (isEnoughMemory(name, content.length())) {
				if (isFileHaveSpace(name)) {
					for (; indexChar < getNumberOfFreeSpaceInLastBlock(name)
							&& indexChar < content.length(); indexChar++)
						charTable += content.charAt(indexChar);
					fillNotFullBlock(name, charTable);
					increaseFileSize(name, charTable.length());

				}
				while (indexChar < content.length()) {
					charTable = "";
					for (int i = 0; i < BLOCK_SIZE && indexChar < content.length(); i++, indexChar++)
						charTable += content.charAt(indexChar);
					int index = findFreeBlock();
					amplifyEntry(name, index);
					fillNewBlock(index, charTable);
					bitVector[index] = true;
					increaseFileSize(name, charTable.length());
				}
				return 1;
			} else {
				Globals.terminalArea.append("FileSystem: Cannot append to file. Low memory.\n");
				return 0;
			}
		} else {
			Globals.terminalArea.append("FileSystem: File " + name + " doesn't exist.\n");
			return 0;
		}
	}

	// Delete file.
	public int deleteFile(String name) {
		if (!checkNameAvailability(name)) {
			int size = getFileSize(name);
			if (size > 0 && size < 32) {
				int index = getFileFirstIndex(name);
				for (int i = index * BLOCK_SIZE; i < index * BLOCK_SIZE + BLOCK_SIZE; i++) {
					dataArea[i] = ' ';
				}
				bitVector[index] = false;
				taperEntry(name);
				removeDirectoryEntry(name);
				Globals.terminalArea.append("FileSystem: File " + name + " removed.\n");
				return 1;
			} else if (size > 32) {
				int blocks = size / BLOCK_SIZE;
				if (size % BLOCK_SIZE != 0) {
					blocks++;
				}
				while (blocks > 0) {
					int index = indexOfLastBlock(name);
					for (int i = index * BLOCK_SIZE; i < index * BLOCK_SIZE + BLOCK_SIZE; i++) {
						dataArea[i] = ' ';
					}
					bitVector[index] = false;
					taperEntry(name);
					blocks--;
				}
				removeDirectoryEntry(name);
				Globals.terminalArea.append("FileSystem: File " + name + " removed.\n");
				return 1;
			} else {
				int index = indexOfLastBlock(name);
				bitVector[index] = false;
				taperEntry(name);
				removeDirectoryEntry(name);
				Globals.terminalArea.append("FileSystem: File " + name + " removed.\n");
				return 1;
			}
		} else {
			Globals.terminalArea.append("FileSystem: File " + name + " doesn't exist.\n");
			return 0;
		}
	}

	// Prints file's content.
	public void readFile(String name, int... charsToRead) {
		if (!getFileFlagStatus(name)) {
			Globals.terminalArea.append("FileSystem: Cannot read file. File is closed.\n");
			return;
		}
		int size = getFileSize(name);
		if (size == 0) {
			Globals.terminalArea.append("FileSystem: File " + name + " is empty.\n");
			return;
		}
		int index = getFileFirstIndex(name);
		int leftToRead = 0;
		if (charsToRead.length == 0)
			leftToRead = size;
		else {
			if (charsToRead[0] < 0) {
				Globals.terminalArea.append("FileSystem: You can't read negative number of characters.\n");
				return;
			}

			if (charsToRead[0] > size)
				leftToRead = size;
			else
				leftToRead = charsToRead[0];
		}
		int i = 0;
		int j = getNumberOfReadChars(name);
		int k = 0;
		if (j == size) {
			Globals.terminalArea.append(
					"FileSystem: File is readed. To read it's content \nyou need to close it, then open once again.\n");
			return;
		}
		Globals.terminalArea.append("FileSystem: File " + name + " contains:");
		Globals.terminalArea.append("\n");
		while (leftToRead != 0) {

			if (j == 32) {
				j = 0;
				int newIndex = fileAllocationTable[index];
				index = newIndex;
				if (index == -1)
					break;
			}
			i = index * BLOCK_SIZE + j++;
			Globals.terminalArea.append(Character.toString(dataArea[i]));
			k++;
			leftToRead--;
			if (getNumberOfReadChars(name) + k == size) {
				setNumberOfReadChars(name, size);
				Globals.terminalArea.append("\n");
				return;
			}
		}
		Globals.terminalArea.append("\n");
		if (charsToRead.length == 0)
			increaseNumberOfReadChars(name, size);
		else
			increaseNumberOfReadChars(name, charsToRead[0]);
	}

	// Prints value of every memory cell.
	public String showData() {
		String result = "";
		int x = 1;
		for (int i = 0; i < DISK_SIZE; i++) {
			if (i % 32 == 0) {
				result += (x + " ");
				if (x < 10) {
					result += (" ");
				}
				x++;
			}
			result += (dataArea[i]);
			if ((i + 1) % 8 == 0) {
				result += ("\t");
			}
			if ((i + 1) % 32 == 0) {
				result += ("\n");
			}
		}
		result += ("\n");
		return result;

	}

	// Shows which blocks are used and which aren't.
	public String showBitVector() {
		String result = "";
		result += ("bitVector:\n");
		int y;
		for (int i = 0; i < NUMBER_OF_BLOCKS; i++) {
			if (bitVector[i] == false)
				y = 0;
			else
				y = 1;
			result += (y + " ");
			if ((i + 1) % 16 == 0) {
				result += ("\n");
			}
		}
		result += ("\n");
		return result;
	}

	// Shows values of file allocation table.
	public String showFAT() {
		String result = "";
		result += ("File Allocation Table:\n");
		for (int i = 1; i <= NUMBER_OF_BLOCKS; i++) {
			result += ("[");
			result += (i + ": ");
			if (fileAllocationTable[i - 1] == -1) {
				result += (-1);
			} else if (fileAllocationTable[i - 1] != -2) {
				result += (fileAllocationTable[i - 1] + 1);
			}
			result += ("]");
			if ((i) % 8 == 0) {
				result += ("\n");
			}
		}
		result += ("\n");
		return result;
	}

	// Prints every file from catalog and its informations.
	public String showMainCatalog() {
		String result = "";
		result += ("root:\n");
		if (mainCatalog.isEmpty()) {
			result += "Main Catalog is Empty\n";
		} else {
			result += "NR\tNAME\t\tFIRST BLOCK\tSIZE\tREAD CHARS\n";
			for (int i = 0; i < mainCatalog.size(); i++) {
				result += (i + 1) + "\t";
				if (mainCatalog.get(i).name.length() < 8)
					result += (mainCatalog.get(i).name + "\t" + "\t");
				else
					result += (mainCatalog.get(i).name + "\t");
				result += (mainCatalog.get(i).indexOfFirstBlock + 1 + "\t\t");
				result += (mainCatalog.get(i).size + "\t");
				result += (mainCatalog.get(i).readChars);
				result += ("\n");
			}
		}
		result += ("\n");
		return result;
	}

	// Show informations about file.
	public String showRootEntry(String name) {
		String result = "";
		if (mainCatalog.isEmpty()) {
			result += ("Main Catalog is Empty\n");
		} else {
			result += ("File " + name + " informations:\n");
			result += ("NAME\tFIRST BLOCK\tSIZE\tREAD CHARS\n");
			int fileNumber = fileNumberSetter(name);
			result += (mainCatalog.get(fileNumber).name + "\t");
			result += (mainCatalog.get(fileNumber).indexOfFirstBlock + "\t\t");

			if (mainCatalog.get(fileNumber).size > 32)
				result += (mainCatalog.get(fileNumber).size / 32 + "\t");
			else
				result += ("1" + "\t");
			result += (mainCatalog.get(fileNumber).readChars + "\t\t");
			result += ("\n\n");
		}
		return result;
	}

	// Prints content of one block.
	public String showBlockContent(int block) {
		String result = "";
		if (block < 1) {
			result += "FileSystem: Wrong value of block.\n";
			return result;
		}
		if (bitVector[block - 1] == true) {
			for (int i = (block - 1) * BLOCK_SIZE; i < (block - 1) * BLOCK_SIZE + BLOCK_SIZE; i++) {
				result += dataArea[i];
				if ((i + 1) % 8 == 0)
					result += "\t";
				if ((i + 1) % 32 == 0)
					result += "\n";
			}
			result += "\n";
		} else {
			result += "FileSystem: Block is empty.\n";
		}
		return result;
	}
}