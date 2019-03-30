import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class Algorithms {

	static int counter = 0; //determines which int in random-numbers to read
	static int num_process = 0; //records how many processes unterminated, to determine when scheduling is done
	static int current = 0; //current #cycle - for each cycle, determine status of each process
	static String verbose = "This detailed printout gives the state and remaining burst for each process\n\n"; //record status of each cycle for "-verbose" - detailed
	static String show = ""; //record status of each cycle for "-show-random" - show_random
	
	
	//for summary data
	static int finishing;
	static float CPU_utilization;
	static float IO_utilization;
	static float throughput;// - expressed in processes completed per hundred time units
	static float average_turnaround = 0;
	static float average_waiting = 0;
	
	
	static int randomOS(int U){	
		URL path = Algorithms.class.getResource("random-numbers.txt");
		File f = new File(path.getFile());
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int num=0;
		int read=0;
		for (int i=0; i<counter; i++) {
			try {
				reader.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		counter++;
		
		try {
			read = Integer.parseInt(reader.readLine());
			//System.out.println("I read" + read);
			
			num = 1 + read % U;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//System.out.println("I get" +num);
		return num;
	}

	//for summary data
	public static void getSummary(Process[] processes) {
		System.out.println("Summary Data:");
		System.out.println("\tFinishing time: " + finishing);
		System.out.printf("\tCPU Utilization: %.6f \n", CPU_utilization/finishing);
		System.out.printf("\tI/O Utilization: %.6f \n", IO_utilization/finishing);
		System.out.printf("\tThroughput: %.6f processes per hundred cycles\n", 100.0 * processes.length/finishing);
		
		for (Process p: processes) {
			average_turnaround += p.getTurnaround();
			average_waiting += p.getReady();
		}
		System.out.println("\tAverage turnaround time: " + String.format("%.6f", average_turnaround/processes.length));
		System.out.println("\tAverage waiting time: " + String.format("%.6f", average_waiting/processes.length));

	}
	
	
	static void FCFS(Process[] processes) {
		Queue<Process> unstarted = new LinkedList<Process>();
		for (Process p: processes) {
			unstarted.add(p);
		}
		
		Queue<Process> ready = new LinkedList<Process>();
		 
		ArrayList<Process> blocked = new ArrayList<Process>();
		
		Process running = null;
		
		num_process = processes.length;
		
		//while processes are not all terminated yet
		while (num_process > 0) {
			//System.out.println("Before Cycle "+ String.format("%3d", current) + ": "+ Process.getStatus(processes)+".\r\n");
			
			verbose += "Before Cycle "+ String.format("%3d", current) + ": "+ Process.getStatus(processes)+".\r\n"; 
			show += "Before Cycle "+ String.format("%3d", current) +": "+ Process.getStatus(processes)+".\r\n"; 
			
			
			//deal with all the blocked process
			for (Process p: processes) {
				p.setIOBurstTime(p.getIOBurstTime()-1);
				if (p.getIOBurstTime() == 0) {
					p.setState("ready");
					ready.add(p);
				}
			}
			
			//find unstarted process to put to ready
			while (unstarted.peek()!=null && unstarted.peek().getA()<=current) {
				unstarted.peek().setState("ready");
				ready.offer(unstarted.poll());
			}
			
			//if there is nothing running now, find a process from ready queue to run + determine CPU burst time
			if (running == null) {
				//if ready queue has something, poll ready head as running
				if (ready.peek() != null) {
					running = ready.poll();
					//running.setReady(running.getReady()-1);
					running.setState("running");
					running.setCPUBurstTime(Math.min(running.getRemaining(), randomOS(running.getB())));
				}
				else {
				}
			}
			else{
				//if there is something running, run it for one cycle
				running.setCPUBurstTime(running.getCPUBurstTime()-1);
				running.setRemaining(running.getRemaining()-1);
				
				//if this CPU burst time runs out, check total CPU time to determine either terminated or blocked
				if (running.getCPUBurstTime()==0) {
						
					//if process CPU time runs out, process terminated
					if (running.getRemaining()==0) {
						running.setState("terminated");
						num_process--;
						running.setFinishing(current);
						running.setTurnaround(running.getFinishing()-running.getA());
					}
					//if CPU time is still there, goes to IO burst and blocked queue
					else {
						running.setState("blocked");
						int t = randomOS(running.getIO());
						running.setIOBurstTime(t);
						running.setBlocked(running.getBlocked()+t);
						blocked.add(running);
					}
					
					running = null;
					if (ready.peek() != null) {
						running = ready.poll();
						running.setState("running");
						running.setCPUBurstTime(Math.min(running.getRemaining(), randomOS(running.getB())));
					}
					else {
					}
				}
				//if CPU burst time still there, don't need to do anything..keep running in the next cycle
				else {
				}
			}
			
			//for summary data
			boolean CPU = false;
			boolean IO = false;
			for (Process p: processes) {
				if (p.getState() == "ready")
					p.setReady(p.getReady()+1);
				else if (p.getState() == "blocked")
					IO = true;
				else if (p.getState() == "running")
					CPU = true;
			}
			
			if (CPU == true)
				CPU_utilization ++;
			if (IO == true)
				IO_utilization ++;
			
			//move on to next cycle
			current++;
		}
		finishing = current-1;
	}
	
	
	static Process runNew(Queue<Process> ready) {
		Process running = null;
		//if ready queue has something, poll ready head as running
		if (ready.peek() != null) {

			running = ready.poll();
			
			running.setQuantum(2);
			
			running.setState("running");
			
			//if CPU does not have burst time, random generate burst time
			if (running.getCPUBurstTime()==0) {
				running.setCPUBurstTime(Math.min(running.getRemaining(), randomOS(running.getB())));
			}
		}
		return running;
	}
	
	//sort available processes and add all to ready
	static void sortAvailable(ArrayList<Process> available, Queue<Process> ready){
		for(int i=0; i<available.size()-1;i++) {
			for(int j=0; j<available.size()-1-i;j++) {
				if ((available.get(j)).getA()>available.get(j+1).getA()) {
					Collections.swap(available, j, j+1);
				}
				else if((available.get(j)).getA() == available.get(j+1).getA() && (available.get(j)).getInput_index()>available.get(j+1).getInput_index()) {
					Collections.swap(available, j, j+1);
				}
			}
		}
		
		for (Process p: available) {
			ready.add(p);
		}
		available.clear();
	}

	
	
	static void RR(Process[] processes) {
		Queue<Process> unstarted = new LinkedList<Process>();
		for (Process p: processes) {
			unstarted.add(p);
		}
		
		Queue<Process> ready = new LinkedList<Process>();
		
		Process running = null;
		num_process = processes.length;
		
		//while processes are not all terminated yet
		while (num_process > 0) {
			//System.out.println("Before Cycle "+ String.format("%3d", current) + ": "+ Process.getStatus(processes)+".\r\n");
			verbose += "Before Cycle "+ String.format("%3d", current) + ": "+ Process.getStatus(processes)+".\r\n"; 
			show += "Before Cycle "+ String.format("%3d", current) +": "+ Process.getStatus(processes)+".\r\n"; 

			
			//deal with all the blocked process
			ArrayList<Process> available = new ArrayList<Process>();
			for (Process p: processes){
				if (p.getState()=="blocked") {
					//System.out.println("before blocked change" + p.getIOBurstTime());
					p.setIOBurstTime(p.getIOBurstTime()-1);
					//System.out.println("after blocked change" + p.getIOBurstTime());
					if (p.getIOBurstTime() == 0) {
						p.setState("ready");
						available.add(p);
					}
				}
			}

			//find unstarted process to put to ready
			while (unstarted.peek()!=null && unstarted.peek().getA()<=current) {
				unstarted.peek().setState("ready");
				available.add(unstarted.poll());
			}
			
			//sortAvailable(available,ready);
			
			//if there is something running
			if (running != null) {
				running.setCPUBurstTime(running.getCPUBurstTime()-1);
				running.setRemaining(running.getRemaining()-1);
				running.setQuantum(running.getQuantum()-1);;
				
				//if this CPU burst time runs out, check total CPU time to determine either terminated or blocked
				if (running.getRemaining()==0) {
					running.setState("terminated");
					num_process--;
					running.setFinishing(current);
					running.setTurnaround(running.getFinishing()-running.getA());
					
					sortAvailable(available,ready);
					running = runNew(ready);
				}
				else if (running.getCPUBurstTime()==0) {
					running.setState("blocked");
					
					int t = randomOS(running.getIO());
					
					//System.out.print(running.getInput_index());
					//System.out.println("generated IO burst is:" + t);
					running.setIOBurstTime(t);
					
					//System.out.println("before IO burst time is:" + running.getBlocked());
					
					running.setBlocked(running.getBlocked()+t);
					//System.out.println("after IO burst time is:" + running.getBlocked());
					
					sortAvailable(available,ready);
					running = runNew(ready);
				}
				else if(running.getQuantum()==0) {
					running.setState("preempted");
					running.setStart_preemp(current);
					
					available.add(running);
					sortAvailable(available,ready);
					//ready.add(running);
					
					running = runNew(ready);
				}
				
				sortAvailable(available,ready);
			}
			else {
				
				sortAvailable(available,ready);
				running = runNew(ready);
			}
			
			//for summary data
			boolean CPU = false;
			boolean IO = false;
			for (Process p: processes) {
				if (p.getState() == "ready" || p.getState() =="preempted")
					p.setReady(p.getReady()+1);
				else if (p.getState() == "blocked")
					IO = true;
				else if (p.getState() == "running")
					CPU = true;
			}
			
			if (CPU == true)
				CPU_utilization ++;
			if (IO == true)
				IO_utilization ++;
			
			//move on to next cycle
			current++;
		}
		finishing = current-1;
	}
	
	static void Uniprogrammed(Process[] processes) {
		Queue<Process> unstarted = new LinkedList<Process>();
		for (Process p: processes) {
			unstarted.add(p);
		}
		
		Queue<Process> ready = new LinkedList<Process>();
		 
		//ArrayList<Process> blocked = new ArrayList<Process>();
		
		Process running = null;
		
		num_process = processes.length;
		
		//while processes are not all terminated yet
		while (num_process > 0) {
			//System.out.println("Before Cycle "+ String.format("%3d", current) + ": "+ Process.getStatus(processes)+".\r\n");
			
			verbose += "Before Cycle "+ String.format("%3d", current) + ": "+ Process.getStatus(processes)+".\r\n"; 
			show += "Before Cycle "+ String.format("%3d", current) +": "+ Process.getStatus(processes)+".\r\n"; 

			
			//find unstarted process to put to ready
			while (unstarted.peek()!=null && unstarted.peek().getA()<=current) {
				unstarted.peek().setState("ready");
				ready.offer(unstarted.poll());
			}
			
			
			//if there is nothing running now, find a process from ready queue to run + determine CPU burst time
			if (running == null) {
				//if ready queue has something, poll ready head as running
				if (ready.peek() != null) {
					running = ready.poll();
					//running.setReady(running.getReady()-1);
					running.setState("running");
					running.setCPUBurstTime(Math.min(running.getRemaining(), randomOS(running.getB())));
				}
			}
			//if there is something running now, determine if it's blocked or running
			else{
				if (running.getState().equals("running")) {
					//if there is something running, run it for one cycle
					running.setCPUBurstTime(running.getCPUBurstTime()-1);
					running.setRemaining(running.getRemaining()-1);
					
					//if this CPU burst time runs out, check total CPU time to determine either terminated or blocked
					if (running.getCPUBurstTime()==0) {
							
						//if process CPU time runs out, process terminated
						if (running.getRemaining()==0) {
							running.setState("terminated");
							num_process--;
							running.setFinishing(current);
							running.setTurnaround(running.getFinishing()-running.getA());
							//find another process in ready queue to run
							if (ready.peek() != null) {
								running = ready.poll();
								running.setState("running");
								running.setCPUBurstTime(Math.min(running.getRemaining(), randomOS(running.getB())));
							}
						}
						//if CPU time is still there, goes to IO burst and blocked queue
						else {
							running.setState("blocked");
							int t = randomOS(running.getIO());
							running.setIOBurstTime(t);
							running.setBlocked(running.getBlocked()+t);
						}
					}
					//if CPU burst time still there, don't need to do anything..keep running in the next cycle
					else {
					}
				}
				else if (running.getState().equals("blocked")){
					//if the current running process is blocked
					running.setIOBurstTime(running.getIOBurstTime()-1);
					
					//if the IO burst time runs out, set a new CPU burst time
					if (running.getIOBurstTime()==0) {
						running.setState("running");
						running.setCPUBurstTime(Math.min(running.getRemaining(), randomOS(running.getB())));
					}
				}
			}
			
			//for summary data
			boolean CPU = false;
			boolean IO = false;
			for (Process p: processes) {
				if (p.getState() == "ready")
					p.setReady(p.getReady()+1);
				else if (p.getState() == "blocked")
					IO = true;
				else if (p.getState() == "running")
					CPU = true;
			}
			
			if (CPU == true)
				CPU_utilization ++;
			if (IO == true)
				IO_utilization ++;
			
			//move on to next cycle
			current++;
		}
		finishing = current-1;
	}
	
	static void SJF(Process[] processes) {
		Queue<Process> unstarted = new LinkedList<Process>();
		for (Process p: processes) {
			unstarted.add(p);
		}
		
		ArrayList<Process> ready = new ArrayList<Process>();
		 
		ArrayList<Process> blocked = new ArrayList<Process>();
		
		Process running = null;
		
		num_process = processes.length;
		
		//while processes are not all terminated yet
		while (num_process > 0) {
			//System.out.println("Before Cycle "+ String.format("%3d", current) + ": "+ Process.getStatus(processes)+".\r\n");
			
			verbose += "Before Cycle "+ String.format("%3d", current) + ": "+ Process.getStatus(processes)+".\r\n"; 
			show += "Before Cycle "+ String.format("%3d", current) +": "+ Process.getStatus(processes)+".\r\n"; 
			
			
			//deal with all the blocked process
			for (Process p: processes) {
				p.setIOBurstTime(p.getIOBurstTime()-1);
				if (p.getIOBurstTime() == 0) {
					p.setState("ready");
					ready.add(p);
				}
			}
			
			//find unstarted process to put to ready
			while (unstarted.peek()!=null && unstarted.peek().getA()<=current) {
				unstarted.peek().setState("ready");
				ready.add(unstarted.poll());
			}
			
			//if there is nothing running now, find a process from ready queue to run + determine CPU burst time
			if (running == null) {
				//if ready queue has something, poll ready head as running
				if (ready.isEmpty()==false) {
					Process next = ready.get(0);
					for (Process p: ready) {
						if (p.getRemaining()<next.getRemaining())
							next = p;
					}
					
					running = next;
					ready.remove(next);
					running.setState("running");
					running.setCPUBurstTime(Math.min(running.getRemaining(), randomOS(running.getB())));
				}
				else {
				}
			}
			else{
				//if there is something running, run it for one cycle
				running.setCPUBurstTime(running.getCPUBurstTime()-1);
				running.setRemaining(running.getRemaining()-1);
				
				//if this CPU burst time runs out, check total CPU time to determine either terminated or blocked
				if (running.getCPUBurstTime()==0) {
						
					//if process CPU time runs out, process terminated
					if (running.getRemaining()==0) {
						running.setState("terminated");
						num_process--;
						running.setFinishing(current);
						running.setTurnaround(running.getFinishing()-running.getA());
					}
					//if CPU time is still there, goes to IO burst and blocked queue
					else {
						running.setState("blocked");
						int t = randomOS(running.getIO());
						running.setIOBurstTime(t);
						running.setBlocked(running.getBlocked()+t);
						blocked.add(running);
					}
					
					running = null;
					if (ready.isEmpty()==false) {
						Process next = ready.get(0);
						for (Process p: ready) {
							if (p.getRemaining()<next.getRemaining())
								next = p;
						}
						
						running = next;
						ready.remove(next);
						running.setState("running");
						running.setCPUBurstTime(Math.min(running.getRemaining(), randomOS(running.getB())));
					}
					else {
					}
				}
				//if CPU burst time still there, don't need to do anything..keep running in the next cycle
				else {
				}
			}
			
			//for summary data
			boolean CPU = false;
			boolean IO = false;
			for (Process p: processes) {
				if (p.getState() == "ready")
					p.setReady(p.getReady()+1);
				else if (p.getState() == "blocked")
					IO = true;
				else if (p.getState() == "running")
					CPU = true;
			}
			
			if (CPU == true)
				CPU_utilization ++;
			if (IO == true)
				IO_utilization ++;
			
			//move on to next cycle
			current++;
		}
		finishing = current-1;
	}
	

}
