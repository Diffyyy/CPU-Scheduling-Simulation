import java.util.ArrayList;

public class Process{
    private int processId;
    private int arrivalTime;
    private int burstTime;
    private int startTime;
    private int endTime;
    private int remainingTime;
    private int waitingTime;
    private ArrayList<Integer> startTimes = new ArrayList<>();
    private ArrayList<Integer> endTimes = new ArrayList<>();
    private ArrayList<Integer> waitTimes = new ArrayList<>();

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


    public void setBurstTime(int burstTime) {
        this.burstTime = burstTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public ArrayList<Integer> getStartTimes() {
        return startTimes;
    }

    public ArrayList<Integer> getEndTimes() {
        return endTimes;
    }

    public ArrayList<Integer> getWaitTimes() {
        return waitTimes;
    }

    public void addStartTime(int start){
        this.startTimes.add(start);
    }
    public void addEndTime(int end){
        this.endTimes.add(end);
    }

    public void addWaitTime(int wait){
        this.waitTimes.add(wait);
    }


}
