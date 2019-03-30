import java.util.ArrayList;
import java.util.Queue;

public class Process {
	int A; //arrival time of the process
	int B; //CPU burst time is uniformly distributed random integers (UDRI) in the interval (0, B].
	int C; //total CPU time needed
	int IO; //assume the IO time to be UDRI in the interval (, IO]
	int remaining; //remaining CPU time
	
	int start_preemp = -1;
	int quantum = 2;
	int input_index = -1;
	
	public int getStart_preemp() {
		return start_preemp;
	}
	public void setStart_preemp(int start_preemp) {
		this.start_preemp = start_preemp;
	}

	public int getQuantum() {
		return quantum;
	}
	public void setQuantum(int quantum) {
		this.quantum = quantum;
	}

	
	public int getInput_index() {
		return input_index;
	}
	public void setInput_index(int input_index) {
		this.input_index = input_index;
	}


	int finishing; 
	int turnaround; //Turnaround time (i.e., finishing time - A).
	int blocked; //I/O time (i.e., time in Blocked state).
	int ready; //Waiting time (i.e., time in Ready state).
	
	String state = "unstarted"; //unstarted, running, ready, blocked, terminated, preemptive??
	int CPUBurstTime = 0;
	int IOBurstTime = 0;
	
	public int getA() {
		return A;
	}
	public void setA(int a) {
		A = a;
	}
	public int getB() {
		return B;
	}
	public void setB(int b) {
		B = b;
	}
	public int getC() {
		return C;
	}
	public void setC(int c) {
		C = c;
	}
	public int getIO() {
		return IO;
	}
	public void setIO(int iO) {
		IO = iO;
	}
	
	public int getRemaining() {
		return remaining;
	}
	public void setRemaining(int remaining) {
		this.remaining = remaining;
	}

	//CPU utilization
	//I/O utilization
	

	public int getFinishing() {
		return finishing;
	}
	public void setFinishing(int finishing) {
		this.finishing = finishing;
	}
	public int getTurnaround() {
		return turnaround;
	}
	public void setTurnaround(int turnaround) {
		this.turnaround = turnaround;
	}
	
	public int getBlocked() {
		return blocked;
	}
	public void setBlocked(int blocked) {
		this.blocked = blocked;
	}
	public int getReady() {
		return ready;
	}
	public void setReady(int ready) {
		this.ready = ready;
	}
	
	//constructor for reader
	public Process(int a, int b, int c, int iO) {
		A = a;
		B = b;
		C = c;
		IO = iO;
	}
	
	//dynamic, state and time

	public String getState() {
		return state;
	}
	
	public void setState(String state) {
		this.state = state;
	}

	public int getCPUBurstTime() {
		return CPUBurstTime;
	}
	public void setCPUBurstTime(int cPUBurstTime) {
		CPUBurstTime = cPUBurstTime;
	}
	public int getIOBurstTime() {
		return IOBurstTime;
	}
	public void setIOBurstTime(int iOBurstTime) {
		IOBurstTime = iOBurstTime;
	}

	
	//printing methods
	static void printProcesses(Process[] processes) {
		for (Process p: processes) {
			System.out.print(p.getA()+" "+p.getB()+" "+p.getC()+" "+p.getIO()+"  ");
		}
		System.out.println();
	}
	
	static void printProcesses(ArrayList<Process> processes) {
		for (Process p: processes) {
			System.out.print(p.getA()+" "+p.getB()+" "+p.getC()+" "+p.getIO()+"  ");
		}
		System.out.println();
	}
	
	static void printProcesses(Queue<Process> processes) {
		for (Process p: processes) {
			System.out.print(p.getA()+" "+p.getB()+" "+p.getC()+" "+p.getIO()+"  ");
		}
		System.out.println();
	}
	
	static void printForEach(Process[] processes) {
		System.out.println("The number of processes is " + processes.length);
		for (int i=0; i<processes.length; i++) {
			Process p = processes[i];
			System.out.println("Process "+i+":");
			System.out.println("\t(A,B,C,IO) = (" + p.getA()+ ","+p.getB()+","+p.getC()+","+p.getIO() + ")");
			System.out.println("\tFinishing time: " + p.getFinishing());
			System.out.println("\tTurnaround time: " + p.getTurnaround());
			System.out.println("\tI/O time: " + p.getBlocked());
			System.out.println("\tWaiting time: " + p.getReady());
			System.out.println();
		}
	}
	
	static void sortProcesses(Process[] processes)
	{
		//sort processes table according to arrival time of each process, i.e. getA()
		for (int i=0; i<processes.length-1; i++) {
			for (int j=0; j<processes.length-1-i; j++) {
				if (processes[j].getA() > processes[j+1].getA()) {
					Process temp = processes[j];
					processes[j] = processes[j+1];
					processes[j+1] = temp;
				}
			}
		}
	}
	
	public static String getStatus(Process[] processes) {
		String statuses = "";
		for (Process p: processes) {
			String status = p.getState();
			int time = 0;
			if (status == "blocked") {
				time = p.getIOBurstTime();
			}
			else if (status == "running") {
				time = Math.min(p.getQuantum(), p.getCPUBurstTime());
			}
			else if (status == "preempted") {
				status = "ready";
				time = p.getQuantum();
			}
			statuses += String.format("%12s", status) + String.format("%3d", +time);
		}
		
		return statuses;
	}
	

}
