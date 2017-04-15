import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
 
 
public class MergeSortInParallel {
  
  public static void main (String[] args){
    int numOfElements = 10; 
    int numOfThreads = 10;
    boolean show = false;
    
    for (int i = 0; i < args.length; i++) {
        if (args[i].equals("-show")) {
            show = true;
        } else {
            if (args[i].equals("-threads")) {
                if (i + 1 < args.length) {
                    numOfThreads = Integer.parseInt(args[i + 1]);
                    i++;
                } else {
                    System.out.println("Please provide number of threads."); 
                    System.exit(1);
                }
            } else {
                int x = Integer.parseInt(args[i]);
                if (x < 1) {
                    System.out.println("Please provide > 0 number of elements."); 
                    System.exit(1);
                }
                if (x > 10000000) {
                    System.out.println("Please provide <= 10000000 number of elements."); 
                    System.exit(1);
                }
                numOfElements = x;
            }
        }  
    }
    
    Random random = new Random();
    
    int [] sortArray = new int[numOfElements];
    for (int i = 0; i < numOfElements; i++){
        sortArray[i] = random.nextInt(numOfElements );
    }
     
    //int []sortArray = {16,15,14,13,12,11,10,9,8,7,6,5,4,3,2,1};
    //int []sortArray = {10,9,8,7,6,5,4,3,2,1};
    SortingThreadPool<SortTask> threadPool = new SortingThreadPool<SortTask>(numOfThreads);
    MergeSortTask sortTask = new MergeSortTask(threadPool, 
                                               null, 
                                               sortArray, 
                                               0, 
                                               sortArray.length-1);
    double startTime = System.currentTimeMillis();
    threadPool.addTask(sortTask);
     
    try {
        sortTask.get();
    } catch(InterruptedException ie){
        ie.printStackTrace();
    }
    System.out.println("Time taken " + (System.currentTimeMillis() - startTime) + " ms.");  
     
    threadPool.shutDown();
   
    if (show) {
        for (int i = 0; i < sortArray.length; i++){
            System.out.println(" element at " + i + " : " + sortArray[i]);
        }
    }
     
  }
 
}
