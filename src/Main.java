import java.text.DecimalFormat;
import java.util.*;

public class Main {
    public static void FCFS(ArrayList<Process> processes) {
        List<Process> processList = new ArrayList<>(processes);

        Collections.sort(processList, Comparator.comparingInt(process -> process.getArrivalTime()));
        List<Integer[]> result = new ArrayList<>();

        int t = 0;
        for (int i = 0; i < processList.size(); i++) {
            int startTime;
            if (t >= processList.get(i).getArrivalTime()) {
                startTime = t;
            } else {
                startTime = processList.get(i).getArrivalTime();
            }
            int processId = processList.get(i).getProcessId();
            int endTime = startTime + processList.get(i).getBurstTime();
            int waitingTime = endTime - processList.get(i).getArrivalTime() - processList.get(i).getBurstTime();
            result.add(new Integer[]{processId, startTime, endTime, waitingTime});
            t = endTime;
        }
        // Sort the result by process id
        result.sort(Comparator.comparingInt(obj -> ((Integer)obj[0])));

        for (int i = 0; i < result.size(); i++){
            System.out.println(result.get(i)[0] + " start time: " + result.get(i)[1] + " end time: " + result.get(i)[2] + " | Waiting time: " + result.get(i)[3]);
        }

        float totalWaitingTime = 0;
        float averageWaitingTime;
        for (int i = 0; i < result.size(); i++){
           totalWaitingTime += result.get(i)[3];
        }

        averageWaitingTime = totalWaitingTime/result.size();
        System.out.println("Average waiting time: " + averageWaitingTime);
    }

    public static void SJF(ArrayList<Process> processList) {
        ArrayList<Process> all = new ArrayList<>(processList);
        ArrayList<Process> queue = new ArrayList<>();
        ArrayList<Process> completed = new ArrayList<>();
        int time = 0;

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

            if(!queue.isEmpty()){
                Process next = Collections.min(queue, Comparator.comparingInt(Process::getBurstTime));
                int duration = next.getBurstTime();
                queue.remove(next);
                next.setStartTime(time);
                time += duration;
//                System.out.println(time);
                next.setEndTime(time);
                completed.add(next);
                for(Process p : queue)
                    p.setWaitingTime(p.getWaitingTime()+duration);
            }
            else{
                Process next_arriving = Collections.min(all, Comparator.comparingInt(Process::getArrivalTime));
                time = next_arriving.getArrivalTime();
                System.out.println(time);
            }
        }

        completed.sort(Comparator.comparingInt(Process::getProcessId));
        float avgWait = 0;
        for(Process p : completed){
            System.out.println(p.getProcessId() + " start time: " + p.getStartTime() + " end time: " + p.getEndTime() + " | Waiting time: " + p.getWaitingTime());
            avgWait += p.getWaitingTime();
        }
        avgWait /= completed.size();
        System.out.println("Average waiting time: " + avgWait);
    }


    public static void RR(ArrayList<Process> processes, int Z) {
        processes.sort(Comparator.comparingInt(Process::getArrivalTime));
        LinkedList<Process> q = new LinkedList<>();
        HashMap<Process, Integer> lastFinished = new HashMap<>();
        for (Process p : processes) lastFinished.put(p, p.getArrivalTime());
        q.add(processes.get(0));
        int t = 0;
        int i = 1;
        while (!q.isEmpty()) {
            Process p = q.poll();
            if(t < p.getArrivalTime()) t = p.getArrivalTime();
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

    public static void SRTF(ArrayList<Process> processes){
        int currentTime = 0;
        int numCompleted = 0;
        int num = processes.size();
        ArrayList<Process> completed = new ArrayList<>();
        DecimalFormat df = new DecimalFormat("0.00");
        double avgWaitTime = 0;
        Process previousProcess = null;

        while (numCompleted < num){
            int shortestProcess = Integer.MIN_VALUE;
            int shortestBurst = Integer.MAX_VALUE;

            int  i = 0;
            for(Process process: processes){
                if(process.getArrivalTime() <= currentTime && process.getRemainingTime() < shortestBurst && process.getRemainingTime() > 0){
                    shortestProcess = i;
                    shortestBurst = process.getRemainingTime();
                }
                i++;
            }

            if(!(shortestProcess == Integer.MIN_VALUE)){
                Process shortest = processes.get(shortestProcess);

                if(previousProcess != null){
                    if(shortest.getProcessId() != previousProcess.getProcessId()){
                        previousProcess.addEndTime(currentTime);
                        shortest.addStartTime(currentTime);
                    }
                }
                else {
                    shortest.addStartTime(currentTime);
                }

                //set the "previous process" variable to the current for the next loop iteration
                previousProcess = shortest;
                shortest.setRemainingTime(shortest.getRemainingTime()-1);
                if(shortest.getRemainingTime() == 0){
                    numCompleted++;
                    if(numCompleted == num){
                        shortest.addEndTime(currentTime+1);
                    }
                }


            }
            currentTime++;
        }

        int waitTime = 0;
        for(Process process: processes){
            for(int i = 0; i < process.getStartTimes().size(); i++){
                if(i == 0){
                    waitTime= process.getStartTimes().get(i) - process.getArrivalTime();
                }
                else{
                    waitTime = process.getStartTimes().get(i) - process.getEndTimes().get(i-1);
                }
                process.addWaitTime(waitTime);
                System.out.println(process.getProcessId() + " start time: " + process.getStartTimes().get(i) +
                        " end time: " + process.getEndTimes().get(i) + " | Waiting time: " + process.getWaitTimes().get(i));
                avgWaitTime = avgWaitTime + process.getWaitTimes().get(i);
            }
        }

        System.out.println("Average waiting time: " + df.format(avgWaitTime / num));

    }

    public static void main(String[] args) {
        System.out.println("Hello world!");
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
        switch(X){
            case 0:
                FCFS(processList);
                break;
            case 1:
                SJF(processList);
                break;
            case 2:
                ScheduleTest.SRTF(processList);
                break;
            case 3:
                RR(processList, Z);
        }



    }
}
