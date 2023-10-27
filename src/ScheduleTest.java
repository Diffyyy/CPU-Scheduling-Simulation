import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.util.*;

import static org.junit.Assert.*;

public class ScheduleTest {

    public static void RR(ArrayList<Process> processes, int Z) {
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        LinkedList<Process> q = new LinkedList<>();
        HashMap<Process, Integer> lastFinished = new HashMap<>();
        for (Process p : processes) lastFinished.put(p, p.getArrivalTime());
        q.add(processes.get(0));
        int t = processes.get(0).getArrivalTime();
        int i = 1;
        while (!q.isEmpty()) {
            Process p = q.poll();
            int wait = t-lastFinished.get(p);
            p.setWaitingTime(p.getWaitingTime() + wait);
            p.addStartTime(t);
            int processTime = Math.min(Z, p.getRemainingTime());
            p.decrementRemaining(processTime);
            t += processTime;
            lastFinished.put(p, t);
            p.addEndTime(t); p.addWaitTime(wait);
            while (i < processes.size() && t >= processes.get(i).getArrivalTime()) q.offer(processes.get(i++));
            if (p.getRemainingTime() > 0) q.add(p);
        }
        float avg = 0;
        processes.sort(Comparator.comparingInt(Process::getProcessId));
        for(Process p: processes){
            for(int j = 0; j < p.getStartTimes().size(); j++){
                System.out.printf("%d start time: %d end time: %d | Waiting time: %d\n",p.getProcessId(), p.getStartTimes().get(j),p.getEndTimes().get(j), p.getWaitTimes().get(j));
            }

            avg +=p.getWaitingTime();
        }
        avg/=processes.size();
        System.out.println("Average waiting time: "+avg);

    }

    public static void SRTF(ArrayList<Process >processes){
        Comparator<Process> comparator = new Comparator<Process>() {
            @Override
            public int compare(Process o1, Process o2) {
                if(o1.getArrivalTime()==o2.getArrivalTime()) return Integer.compare(o1.getRemainingTime(), o2.getRemainingTime());
                return Integer.compare(o1.getArrivalTime(), o2.getArrivalTime());
            }
        };

        processes.sort(comparator);
        int n = processes.size();
        HashMap<Process, Integer> lastFinished = new HashMap<>();
        for (Process p : processes) lastFinished.put(p, p.getArrivalTime());

        PriorityQueue<Process> q = new PriorityQueue<>(Comparator.comparingInt(Process::getRemainingTime));
        q.add(processes.get(0));
        int t = processes.get(0).getArrivalTime();
        int i = 1;

        while (!q.isEmpty()){
            Process p = q.poll();
            int remaining = p.getRemainingTime();
            int wait = t-lastFinished.get(p);
            p.setWaitingTime(p.getWaitingTime() + wait);
            p.addStartTime(t);

            int elapsed = p.getRemainingTime();
            while (i < n && t+elapsed >= processes.get(i).getArrivalTime()){
                int nextRemaining = processes.get(i).getRemainingTime();
                int nextArrival = processes.get(i).getArrivalTime();
                q.offer(processes.get(i++));
                int temp = nextArrival -t;
                if(remaining - temp > nextRemaining ){ //preempt
                    elapsed = temp; break;
                }
            }
            p.decrementRemaining(elapsed);
            t+=elapsed;
            lastFinished.put(p, t);
            p.addEndTime(t);
            p.addWaitTime(wait);
            if(p.getRemainingTime()>0)q.add(p);
        }

        float avg = 0;
        processes.sort(Comparator.comparingInt(Process::getProcessId));
        for(Process p: processes){
            for(int j = 0; j < p.getStartTimes().size(); j++){
                System.out.printf("%d start time: %d end time: %d | Waiting time: %d\n",p.getProcessId(), p.getStartTimes().get(j),p.getEndTimes().get(j), p.getWaitTimes().get(j));
            }

            avg +=p.getWaitingTime();
        }
        avg/=processes.size();
        System.out.println("Average waiting time: "+avg);
    }
    public static void FCFS(ArrayList<Process> processes){
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        float avg = 0;
        int t = processes.get(0).getArrivalTime();
        for(Process p: processes){
            int wait = t - p.getArrivalTime();
            int end = t +  p.getBurstTime();
            p.setStartTime(t);
            p.setEndTime(end);
            p.setWaitingTime(wait);

            t = end;
        }
        processes.sort(Comparator.comparingInt(Process::getProcessId));
        for(Process p: processes){
            avg+=p.getWaitingTime();
            System.out.printf("%d start time: %d end time: %d | Waiting time: %d\n",p.getProcessId(), p.getStartTime(),p.getEndTime(), p.getWaitingTime() );
        }
        avg/=processes.size();
        System.out.println("Average waiting time: "+avg);
    }
    public static void SJF(ArrayList<Process> processes){
        Comparator<Process> comparator = new Comparator<Process>() {
            @Override
            public int compare(Process o1, Process o2) {
                if(o1.getArrivalTime()==o2.getArrivalTime()) return Integer.compare(o1.getRemainingTime(), o2.getRemainingTime());
                return Integer.compare(o1.getArrivalTime(), o2.getArrivalTime());
            }
        };
        processes.sort(comparator);
        int n = processes.size();
        HashMap<Process, Integer> lastFinished = new HashMap<>();
        for (Process p : processes) lastFinished.put(p, p.getArrivalTime());

        PriorityQueue<Process> q = new PriorityQueue<>(Comparator.comparingInt(Process::getRemainingTime));
        q.add(processes.get(0));
        int t = processes.get(0).getArrivalTime();
        int i = 1;

        while (!q.isEmpty()){
            Process p = q.poll();
            int wait = t-lastFinished.get(p);
            p.setWaitingTime(p.getWaitingTime() + wait);
            p.setStartTime(t);

            t+=p.getBurstTime() ;
            while (i < n && t >= processes.get(i).getArrivalTime()){
                q.offer(processes.get(i++));
            }
            lastFinished.put(p, t);
            p.setEndTime(t);
            p.setWaitingTime(wait);
        }

        float avg = 0;
        processes.sort(Comparator.comparingInt(Process::getProcessId));
        for(Process p: processes){
            System.out.printf("%d start time: %d end time: %d | Waiting time: %d\n",p.getProcessId(), p.getStartTime(),p.getEndTime(), p.getWaitingTime());
            avg +=p.getWaitingTime();
        }
        avg/=processes.size();
        System.out.println("Average waiting time: "+avg);
    }

    public static ArrayList<Process> processes_1(){
        Process p1 = new Process(1, 1, 20);
        Process p2 = new Process(2, 3,4);
        Process p3 = new Process(3, 8, 6);
        Process p4 = new Process(4, 11, 12);
        ArrayList<Process> processes = new ArrayList<>();
        processes.addAll(List.of(p1, p2, p3, p4));
        return processes;
    }
    public static ArrayList<Process>[] randomProcesses(){
        int n = (int) (Math.random() * 98+3);
        ArrayList<Process>[] ret = new ArrayList[2];
        ArrayList<Process > processes = new ArrayList<>();
        ArrayList<Process> processes1 = new ArrayList<>();
        for(int i = 0; i < n; i++){
            int id = i+1;
            int arrival = (int)(Math.random()*21);
            int burst = (int) (Math.random()*21);
            processes.add(new Process(id, arrival, burst));
            processes1.add(new Process(id, arrival, burst));
        }
        ret[0] = processes;
        ret[1] = processes1;
        return ret;
    }
    public String convertProcessesToString(ArrayList<Process> processes){
        StringBuilder sb = new StringBuilder();
        for(Process p : processes){
            sb.append(String.format("%d %d %d\n", p.getProcessId(), p.getArrivalTime(), p.getBurstTime()));
        }
        return sb.toString();

    }
    @ClassRule
    public static final SystemOutRule systemOutRule = new SystemOutRule().enableLog();
    @Test
    public void test_1(){
        Main.SRTF(processes_1());
        String first = systemOutRule.getLog();
        systemOutRule.clearLog();
        SRTF(processes_1());
        String second = systemOutRule.getLog();

        assertEquals(first, second);
    }

    @Test
    public void random(){
        List<String> failedAssertions = new ArrayList<>();
        int i = 0;
        while(i < 5){
            for(int j = 0; j < 3; j++){
                ArrayList<Process>[] test = randomProcesses();
                String first;
                String second;
                String name;
                systemOutRule.clearLog();
                switch(j){
                    case 0:
                        FCFS(test[0]);
                        first = systemOutRule.getLog();
                        systemOutRule.clearLog();
                        Main.FCFS(test[1]);
                        second = systemOutRule.getLog();
                        name = "FCFS";
                        break;
                    case 1:
                        SJF(test[0]);
                        first = systemOutRule.getLog();
                        systemOutRule.clearLog();
                        Main.SJF(test[1]);
                        second = systemOutRule.getLog();
                        name = "SJF";
                        break;
                    case 2:
                        SRTF(test[0]);
                        first = systemOutRule.getLog();
                        systemOutRule.clearLog();
                        SRTF(test[1]);
                        second = systemOutRule.getLog();
                        name = "SRTF";
                        break;
                    default:
                        int Z = (int) (Math.random()*100+1);
                        RR(test[0], Z);
                        first = systemOutRule.getLog();
                        systemOutRule.clearLog();
                        Main.RR(test[1], Z);
                        second = systemOutRule.getLog();
                        name = "RR";
                        break;


                }
                if(!first.equals(second)){
                    failedAssertions.add("\n-------------------------------------------------\n");
                    failedAssertions.add(name+" | FAILED TEST CASE FOR: \n");
                    failedAssertions.add(convertProcessesToString(test[0])+"\n");
                    failedAssertions.add(String.format("Expected: \n%s\n, Actual: \n%s", first, second));
                    failedAssertions.add("\n-------------------------------------------------\n");
                }
            }


            i++;
        }
        assertTrue("Failed assertions: " + String.join("", failedAssertions), failedAssertions.isEmpty());
    }
}