import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Scanner;

public class Reader {
	
	static Process[] read(String input_filename) {
		
		//System.out.println(input_filename);
		URL path = Algorithms.class.getResource(input_filename);
		File f = new File(path.getFile());
		Scanner sc = null;
		try {
			sc = new Scanner(f);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		int PROCESS_COUNT=0;
		PROCESS_COUNT = sc.nextInt();
		//System.out.println("process_count"+PROCESS_COUNT);
		
		Process[] processes = new Process[PROCESS_COUNT];
		
		for (int i=0; i<PROCESS_COUNT; i++) {
			processes[i] = new Process(sc.nextInt(),sc.nextInt(),sc.nextInt(),sc.nextInt());
			processes[i].setRemaining(processes[i].getC());
			processes[i].setInput_index(i);
		}
		
		//Process.printProcesses(processes);
		return processes;
	}
}
