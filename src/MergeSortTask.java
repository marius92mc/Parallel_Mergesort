import java.util.concurrent.atomic.AtomicBoolean;
 
 
/**
 * @author MaheshT
 *
 */
public class MergeSortTask extends SortTask{
  private int sortArray [];
  private int iStart = 0;
  private int iEnd  =0;
  private  int noOfSubtask =0;
  private final int SPLIT_THRESHOLD = 100000;
  private int taskDone =0;
  private MergeSortTask parentTask = null;
  private SortingThreadPool<SortTask> threadPool = null;
  private AtomicBoolean  splitDone =new AtomicBoolean(false);
  private Object waitForTaskDone = new Object();
 
   
   
  public MergeSortTask(SortingThreadPool<SortTask> threadPool,MergeSortTask parentTask,int [] inList,int start, int end){
    this.sortArray = inList;
    this.iStart = start;
    this.iEnd   = end;
    this.threadPool = threadPool;
    this.parentTask = parentTask;
 
  }
    
  private void splitSortNMerge(){
    splitSortNMerge(sortArray, iStart,iEnd);
    
  }
  
  private void splitSortNMerge(int inList[],int start, int end){
    int diff = end - start;
    int mid  = 0;
    int startIdxFirstArray =0;
    int endIdxFirstArray =0;
    int startIdxSecondArray=0;
    int endIdxSecondArray=0;
     
    if (diff > 1){ // more than two then split
      mid  = diff/2;
      startIdxFirstArray = start;
      endIdxFirstArray = start + mid;
      startIdxSecondArray= start + mid +1;
      endIdxSecondArray= end;
      if (diff > SPLIT_THRESHOLD){
        submitTaskToPool(new MergeSortTask(threadPool,this,sortArray,startIdxFirstArray, endIdxFirstArray));
        submitTaskToPool(new MergeSortTask(threadPool,this,sortArray,startIdxSecondArray, endIdxSecondArray));
        submitTaskToPool(this);
        noOfSubtask=2;
        //merge will be done by calling merge() function, when above tasks are done
        splitDone.set(true);
        return;
      }else {
        //recursive split and merge
        splitSortNMerge (inList,startIdxFirstArray, endIdxFirstArray);
        splitSortNMerge (inList,startIdxSecondArray, endIdxSecondArray);
        merge(inList,startIdxFirstArray,endIdxFirstArray,startIdxSecondArray,  endIdxSecondArray);
      }
    }else if (diff == 1){ // two element
      if (inList[start] > inList[end]){ //swap
        int tempVar = inList[start];
        inList[start] = inList[end];
        inList[end]   = tempVar;
      }
    }
  }
   
  private void merge(){
     
    int startIdxFirstArray =0;
    int endIdxFirstArray =0;
    int startIdxSecondArray=0;
    int endIdxSecondArray=0;
     
    int diff = iEnd - iStart;
    int mid  = 0;
     
    mid  = diff/2;
    startIdxFirstArray = iStart;
    endIdxFirstArray = iStart + mid;
    startIdxSecondArray=iStart + mid +1;
    endIdxSecondArray= iEnd;
    merge(sortArray,startIdxFirstArray,endIdxFirstArray,startIdxSecondArray,endIdxSecondArray);
 
 
  }
   
  private static void merge(int [] inList,int startIdxFirstArray,int endIdxFirstArray,
    int startIdxSecondArray, int endIdxSecondArray){
 
    int firstArryPtr = startIdxFirstArray;
    int secondArryPtr = startIdxSecondArray;
     
    int  []  tempArry = new int [endIdxSecondArray-startIdxFirstArray +1 ] ;
    //merge in sorted order
     int tempIdx =0;
      
     for (tempIdx=0;tempIdx < tempArry.length ; tempIdx++){
       if ( inList[firstArryPtr] < inList[secondArryPtr]){
         tempArry[tempIdx] = inList[firstArryPtr];
         firstArryPtr++;
         if (firstArryPtr > endIdxFirstArray) break;
       }else {
         tempArry[tempIdx] = inList[secondArryPtr];
         secondArryPtr++;
         if (secondArryPtr > endIdxSecondArray) break;
       }
     }
     if (firstArryPtr > endIdxFirstArray){
       while (tempIdx < tempArry.length-1){
         tempIdx++;
         tempArry[tempIdx]= inList[secondArryPtr];
         secondArryPtr++;
       }
     }else if (secondArryPtr > endIdxSecondArray){
       while (tempIdx < tempArry.length-1){
          tempIdx++;
         tempArry[tempIdx]= inList[firstArryPtr];
         firstArryPtr++;
       }
     }
      
     //copy sorted array
     for (int j=0;j < tempArry.length ; j++){
       inList[startIdxFirstArray+j] = tempArry[j];
     }
  }
   
  public boolean isReadyToProcess(){
    if (!splitDone.get()){
      return true;
    }else if (taskDone==noOfSubtask){
      return true;
    }else {
      return false;
    }
  }
   
  public synchronized void subTaskDone(){//
    taskDone++;
  }
   
  private void submitTaskToPool(MergeSortTask sortTask){
    threadPool.addTask(sortTask);
  }
   
  public void run(){
    if (splitDone.get()){
      this.merge();
    }else {
      this.splitSortNMerge();
     }
     
    if (taskDone==noOfSubtask) {
      if (parentTask !=null){
        this.parentTask.subTaskDone();
      }
      synchronized(waitForTaskDone){
        waitForTaskDone.notifyAll();
      }
    }
  }
   
  public  int [] get() throws InterruptedException {
    if (splitDone.get() && taskDone==noOfSubtask ){
      return sortArray;
    }else {
      synchronized(waitForTaskDone){
        waitForTaskDone.wait();
      }
    }
    return sortArray;
  }
   
  public String toString() {
    return "Task to sort from " + this.iStart +" To "+this.iEnd ;
  }
}
