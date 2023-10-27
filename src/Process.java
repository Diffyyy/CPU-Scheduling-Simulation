import java.util.ArrayList;

public class Process {
    private int processId;
    private int arrivalTime;
    private int burstTime;
    private int startTime;
    private int endTime;
    private int remainingTime;
    private int waitingTime;

    public Process(int processId, int arrivalTime, int burstTime){
        this.processId = processId;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.startTime = -1;
        this.endTime = -1;
        this.waitingTime = 0;
        this.remainingTime = burstTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getProcessId() {
        return processId;
    }

    public int getStartTime(){
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }
    public void decrementRemaining(int time){
        remainingTime-=time;
    }
}
