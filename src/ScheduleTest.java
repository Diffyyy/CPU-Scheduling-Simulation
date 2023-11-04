import org.junit.ClassRule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemOutRule;

import java.util.*;
import java.util.function.Consumer;

import static org.junit.Assert.*;

public class ScheduleTest {

    public static void RR(ArrayList<Process> processes, int Z) {
        ArrayList<Process> all = new ArrayList<>(processes);
        ArrayList<Process> queue = new ArrayList<>();
        ArrayList<Process> completed = new ArrayList<>();
        int time = 0;
        int duration;
        int numProcesses = all.size();
        Process next = null;

        while(!all.isEmpty() || !queue.isEmpty()){
            Iterator<Process> iterator = all.iterator();
            while(iterator.hasNext()){
                Process p = iterator.next();
                if(time >= p.getArrivalTime()){
                    iterator.remove();
                    p.setWaitingTime(time-p.getArrivalTime());
                    queue.add(p);
                }
            }
            if(next != null)
                if(next.getRemainingTime() != 0)
                    queue.add(next);

            if(!queue.isEmpty()){
                next = queue.get(0);
                queue.remove(next);
                next.setStartTime(time);
                if (next.getRemainingTime() > Z)
                    duration = Z;
                else
                    duration = next.getRemainingTime();
                next.setRemainingTime(next.getRemainingTime() - duration);
                time += duration;

                Process copy = new Process(next.getProcessId(), next.getArrivalTime(), next.getBurstTime());
                copy.setStartTime(next.getStartTime());
                copy.setEndTime(time);
                copy.setRemainingTime(next.getRemainingTime());
                copy.setWaitingTime(next.getWaitingTime());
                next.setWaitingTime(0);
                completed.add(copy);
                next.setEndTime(time);

                for(Process p : queue)
                    p.setWaitingTime(p.getWaitingTime()+duration);
            }
            else{
                Process next_arriving = Collections.min(all, Comparator.comparingInt(Process::getArrivalTime));
                time = next_arriving.getArrivalTime();
            }
        }

        completed.sort(Comparator.comparingInt(Process::getProcessId));
        float avgWait = 0;
        for(Process p : completed){
            System.out.println(p.getProcessId() + " start time: " + p.getStartTime() + " end time: " + p.getEndTime() + " | Waiting time: " + p.getWaitingTime());
            avgWait += p.getWaitingTime();
        }
        avgWait /= numProcesses;
        System.out.println("Average waiting time: " + avgWait);

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

        PriorityQueue<Process> q = new PriorityQueue<>(new Comparator<Process>() {
            @Override
            public int compare(Process o1, Process o2) {
                if(o1.getRemainingTime()==o2.getRemainingTime()){
                    if(o1.getArrivalTime() == o2.getArrivalTime()) return Integer.compare(o1.getProcessId(), o2.getProcessId());
                    return Integer.compare(o1.getArrivalTime(), o2.getArrivalTime());
                }
                return Integer.compare(o1.getRemainingTime(), o2.getRemainingTime());
            }
        });
        q.add(processes.get(0));
        int t=0;
        int i = 1;

        while (!q.isEmpty()){
            Process p = q.poll();
            if(t < p.getArrivalTime()) t = p.getArrivalTime();
            int remaining = p.getRemainingTime();
            int wait = t-lastFinished.get(p);
            p.setWaitingTime(p.getWaitingTime() + wait);
            p.addStartTime(t);
            int elapsed = p.getRemainingTime();
            while (i < n && t+elapsed >= processes.get(i).getArrivalTime() ){
                int nextRemaining = processes.get(i).getRemainingTime();
                int nextArrival = processes.get(i).getArrivalTime();
//                if(nextRemaining>0) {
                    q.offer(processes.get(i++));
                    int temp = nextArrival - t;
                    if (remaining - temp > nextRemaining) { //preempt
                        elapsed = temp;
                        break;
                    }
//                }else i++;
            }
            p.decrementRemaining(elapsed);
            t+=elapsed;
            lastFinished.put(p, t);
            p.addEndTime(t);
            p.addWaitTime(wait);
            if(p.getRemainingTime()>0)q.add(p);
            if(q.isEmpty() && i < processes.size())q.add(processes.get(i++));
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
        int t = 0;
        for(Process p: processes){
            if(t < p.getArrivalTime()) t = p.getArrivalTime()   ;
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

        PriorityQueue<Process> q = new PriorityQueue<>(new Comparator<Process>() {
            @Override
            public int compare(Process o1, Process o2) {
                if(o1.getRemainingTime()==o2.getRemainingTime()){
                    if(o1.getArrivalTime() == o2.getArrivalTime()) return Integer.compare(o1.getProcessId(), o2.getProcessId());
                    return Integer.compare(o1.getArrivalTime(), o2.getArrivalTime());
                }
                return Integer.compare(o1.getRemainingTime(), o2.getRemainingTime());
            }
        });
        q.add(processes.get(0));
        int t = 0;
        int i = 1;

        while (!q.isEmpty()){
            Process p = q.poll();
            if(t < p.getArrivalTime()) t = p.getArrivalTime();
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
            if(q.isEmpty() && i < processes.size())q.add(processes.get(i++));
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

    public static ArrayList<Process> processes_2(){
        Process p1 = new Process(1, 18, 6);
        Process p2 = new Process(2, 17,3);
        Process p3 = new Process(3, 9, 0);
        Process p4 = new Process(4, 14, 15);
        ArrayList<Process> processes = new ArrayList<>();
        processes.addAll(List.of(p1, p2, p3, p4));
        return processes;
    }

    public static void main(String[] args) {
        testInput();
    }
    public static void testInput(){
        int X, Y, Z, processId, arrivalTime, burstTime, i;
        ArrayList<Process> processList = new ArrayList<>();
        Scanner sc = new Scanner(System.in);

        X = sc.nextInt();
        Y = sc.nextInt();
        Z = sc.nextInt();

        for(i = 0; i < Y; i++){
            processId = sc.nextInt();
            arrivalTime = sc.nextInt();
            burstTime = sc.nextInt();
            processList.add(new Process(processId, arrivalTime, burstTime));
        }
        getString(X, processList, Z, false);
    }
    public static ArrayList<Process>[] randomProcesses(){   
        int n = (int) (Math.random() * 3+1);
        ArrayList<Process>[] ret = new ArrayList[2];
        ArrayList<Process > processes = new ArrayList<>();
        ArrayList<Process> processes1 = new ArrayList<>();
        for(int i = 0; i < n; i++){
            int id = i+1;
            int arrival = (int)(Math.random()*20);
            int burst = (int) (Math.random()*20+1);
            processes.add(new Process(id, arrival, burst));
            processes1.add(new Process(id, arrival, burst));
        }
        ret[0] = processes;
        ret[1] = processes1;
        return ret;
    }

    public static String convertProcessesToString(ArrayList<Process> processes){
        StringBuilder sb = new StringBuilder();
        for(Process p : processes){
            sb.append(String.format("%d %d %d\n", p.getProcessId(), p.getArrivalTime(), p.getBurstTime()));
        }
        return sb.toString();

    }
    public static String getString(int algorithm, ArrayList<Process> processes, int Z, boolean test){
        systemOutRule.clearLog();
        switch(algorithm){
            case 0:
                if(test) FCFS(processes);
                else Main.FCFS(processes);
                break;
            case 1:
                if(test) SJF(processes);
                else Main.SJF(processes);
                break;
            case 2:
                if(test) SRTF(processes);
                else Main.SRTF(processes);
                break;
            case 3:
                if(test) RR(processes, Z);
                else Main.RR(processes, Z);
                break;

        }
        return systemOutRule.getLog();
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
    public void test_2(){
        ArrayList<Process>[] tests = randomProcesses();
        System.out.println(convertProcessesToString(tests[0]));
//        ArrayList<Process> processes = processes_2();
        System.out.println();
        systemOutRule.clearLog();;

        Main.SRTF(tests[0]);
        String first =  systemOutRule.getLog()  ;
        systemOutRule.clearLog();
//        SRTF(processes_2());
        String second = systemOutRule.getLog();
        assertEquals(first, second);
    }
    @Test
        public void random(){
        List<String> failedAssertions = new ArrayList<>();
        int i = 0;
        while(i < 100){
            for(int j = 0; j < 4; j++){
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
                        Main.SRTF(test[1]);
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
                        name = "RR Z = "+Z;
                        break;


                }
                if(!first.equals(second)){
                    failedAssertions.add("\n-------------------------------------------------\n");
                    failedAssertions.add(name+" | FAILED TEST CASE FOR: \n");
                    failedAssertions.add(convertProcessesToString(test[0])+"\n");
                    failedAssertions.add(String.format("Expected: \n%s\nActual: \n%s", first, second));
                    failedAssertions.add("\n-------------------------------------------------\n");
                }
            }


            i++;
        }
        assertTrue("Failed assertions: " + String.join("", failedAssertions), failedAssertions.isEmpty());
    }
}
