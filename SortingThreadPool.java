import java.util.ArrayList;
import java.util.List;
 
 
 
public class SortingThreadPool<E extends SortTask > {
 
 
  List<E> taskList = new ArrayList<E>();
  List<WorkerThread> workers = new ArrayList<WorkerThread>();
  boolean shutdown = false;
 
  public SortingThreadPool(int noOfThreads){
    //add worker to connection pool
    for (int i=0; i <noOfThreads;i++){
      WorkerThread workerThread =new WorkerThread("Worker: "+i );
      workers.add(workerThread);
      workerThread.start();
    }
  }
 
   
  public void addTask(E e){
    synchronized(taskList){
      taskList.add(e);
      //System.out.println("Added task " + e);
      //done adding all tasks, notify all waiting threads.
      taskList.notifyAll();
    }
 
  }
  public void shutDown(){
    shutdown = true;
    synchronized(taskList){
      taskList.notifyAll();
    }
  }
 
 
  private class WorkerThread extends Thread {
    public WorkerThread(String name){
      super(name);
    }
 
    public void run(){
      System.out.println("Starting thread ");
      while (!shutdown || !taskList.isEmpty() ){
        //System.out.println("Reading task 2");
        try{
          synchronized(taskList){
           // System.out.println("Reading task");
            E tempTask= null;
            for (E myTask : taskList){
             // System.out.println( "Checking " +myTask);
              if ( myTask.isReadyToProcess()){
                tempTask =  myTask;
              }
            }
            if (tempTask !=null){
              //System.out.println( this.getName() + " working on " + tempTask);
              taskList.remove(tempTask);
              tempTask.run();
            }else {
              if ( !shutdown || !taskList.isEmpty()){
                System.out.println("Going to wait " + this.getName() + " size " + taskList.size());
                taskList.wait();
              }
            }
            //give other treads chance to work on tasks
            Thread.yield();
          }
        }catch(Exception e){
          e.printStackTrace();
        }
      }
      System.out.println("Exiting thread " + this.getName());
    }
  }
 
}
