@SuppressWarnings("unused")
class CounterThread extends Thread {

    @Override
    public void run() {
        long counter = 0;

        while (!this.isInterrupted()) {
            try {
                counter++;
                Thread.sleep(100);
            }catch (InterruptedException ex) {
                System.out.println("It was interrupted");
                break;
            }
        }
    }
}