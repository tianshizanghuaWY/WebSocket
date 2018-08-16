package com.netty.business;

import com.netty.business.task.ITask;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.*;

/**
 * <br>
 *
 * @author 千阳
 * @date 2018-08-13
 */
public class BusinessTaskExecutor {
    //private Logger logger = Logger.getLogger(TelelCommandExecutor.class);

    private static BusinessTaskExecutor instance;
    public synchronized static BusinessTaskExecutor getInstance(){
        if(instance == null){
            instance = new BusinessTaskExecutor();
        }
        return instance;
    }
    private BusinessTaskExecutor(){
        init();
    }

    ThreadFactory threadFactory = new DefaultThreadFactory("用户线程-pool-%d");
    ExecutorService taskExecutor = new ThreadPoolExecutor(1,
            1,5L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), threadFactory);

    private static final BlockingQueue<ITask> TASK_QUEUE = new LinkedBlockingQueue<ITask>();
    private void init(){
        taskExecutor.execute(new TeleCommandHandlerTask());
    }

    public void addTask(ITask task) {
        if(task != null){
            try {
                TASK_QUEUE.put(task);
            } catch(InterruptedException e) {
                //logger.error("添加TeleCommand失败", e);
                //throw new MogoException(AbstractErrorCodes.SYS_ERROR, "添加TeleCommand失败");
            }
        }
    }

    class TeleCommandHandlerTask implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    //logger.info(Thread.currentThread().getId() + "-try get task");
                    ITask task = TASK_QUEUE.take();
                    //logger.info(Thread.currentThread().getId() + "-Sync FACADE_QUEUE size:" + FACADE_QUEUE.size());
                    //logger.info(Thread.currentThread().getId() + "-get one task");

                    task.execute();
                }catch( RuntimeException e ) {
                    //logger.info(e.getMessage(), e);
                } catch (Exception e) {
                    //logger.error("Error when run the thread", e);
                }
            }
        }
    }
}
