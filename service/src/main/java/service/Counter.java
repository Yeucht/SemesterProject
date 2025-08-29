package service;


public class Counter {

    private int counter;

    public void updateCounter(int i){ counter = counter + i; }

    public int getCounter() { return counter; }

    public void resetCounter() {counter = 0;}

    public  void setCounter(int i){counter = i;}
}
