import java.util.ArrayList;
import java.util.Scanner;

public class Main {
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