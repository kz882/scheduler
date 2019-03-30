import java.util.Scanner;

public class Scheduler {
	

	static String flag = "";
	
	public static void main (String[] args) {
		
		//read command line from user input 
		Scanner commands = new Scanner (System.in);
		String command = commands.nextLine();
		String[] splited = command.split(" ");
		String program_name = splited[0];
		
		String input_filename;
		if (splited[1].contains("verbose")) {
			flag = "verbose";
			input_filename = splited[2];
		}
		else if (splited[1].contains("show")) {
			flag = "show";
			input_filename = splited[2];
		}
		else {
			input_filename = splited[1];
		}
		commands.close();
		
		//read processes info from input file
		Process[] processes = null;
		processes = Reader.read(input_filename);
		
		//print original process
		System.out.print("The original input was: ");
		Process.printProcesses(processes);
				
		//sort process and print
		Process.sortProcesses(processes);
		System.out.print("The (sorted) input is:  ");
		Process.printProcesses(processes);
		System.out.println();

		//process the sorted processes according to program_name
		if (program_name.equals("fcfs")) {
			Algorithms.FCFS(processes);
			if (flag == "verbose") {
				System.out.print(Algorithms.verbose);
			}
			System.out.println("The scheduling algorithm used was First Come First Served\r\n");
		}
		else if (program_name.equals("rr")) {
			Algorithms.RR(processes);
			if (flag == "verbose") {
				System.out.print(Algorithms.verbose);
			}
			System.out.println("The scheduling algorithm used was Round Robbin; Quantum is 2\r\n");
		}
		else if (program_name.equals("uni")) {
			Algorithms.Uniprogrammed(processes);
			if (flag == "verbose") {
				System.out.print(Algorithms.verbose);
			}
			System.out.println("The scheduling algorithm used was Uniprocessor\r\n");
		}
		else{
			Algorithms.SJF(processes);
			if (flag == "verbose") {
				System.out.print(Algorithms.verbose);
			}
			System.out.println("The scheduling algorithm used was Shortest Job First\r\n");
		}
		
		Process.printForEach(processes);
		Algorithms.getSummary(processes);
		
	}
	
}
