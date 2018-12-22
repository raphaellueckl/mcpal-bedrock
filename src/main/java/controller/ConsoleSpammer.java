package controller;

public class ConsoleSpammer implements Runnable {

    private final String msg;

    public ConsoleSpammer(String msg) {
        this.msg = msg;
    }

    @Override
    public void run() {
        while(true) {
            System.out.println(msg);
            try { Thread.sleep(5000); } catch (InterruptedException e) { }
        }
    }
}
