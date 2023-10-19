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
        System.out.println("Average Waiting Time: " + averageWaitingTime);
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
                System.out.println(time);
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
    public static void main(String[] args) {
        System.out.println("Hello world!");
        int X, Y, Z, processId, arrivalTime, burstTime, i;
        ArrayList<Process> processList = new ArrayList<Process>();
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


        if (X == 0)
            FCFS(processList);
        else if (X == 1)
            System.out.println("Right");
            Z = 1;
            SJF(processList);

        //FOR DEBUGGING ONLY
        for(i = 0; i < Y; i++){
            System.out.println("Process " + processList.get(i).getProcessId() + " | Arrival Time: " + processList.get(i).getArrivalTime() + " | burstTime: " + processList.get(i).getBurstTime());
        }

    }
}
