import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void FCFS(ArrayList<Process> processes) {
        List<Process> processList = new ArrayList<>(processes);

        Collections.sort(processList, Comparator.comparingInt(process -> process.getArrivalTime()));
        List<Integer[]> result = new ArrayList<>();

        int t = 0;
        for (int i = 0; i < processList.size(); i++) {
            if (t >= processList.get(i).getArrivalTime()) {
                int processId = processList.get(i).getProcessId();
                int startTime = t;
                int endTime = startTime + processList.get(i).getBurstTime();
                int waitingTime = endTime - processList.get(i).getArrivalTime() - processList.get(i).getBurstTime();
                result.add(new Integer[]{processId, startTime, endTime, waitingTime});
                t = endTime;
            } else {
                t = processList.get(i).getArrivalTime();
            }
        }
        // Sort the result by process id
        result.sort(Comparator.comparingInt(obj -> ((Integer)obj[0])));

        System.out.println(result);
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



        //FOR DEBUGGING ONLY
        for(i = 0; i < Y; i++){
            System.out.println("Process " + processList.get(i).processId + " | Arrival Time: " + processList.get(i).arrivalTime + " | burstTime: " + processList.get(i).burstTime);
        }

    }
}
