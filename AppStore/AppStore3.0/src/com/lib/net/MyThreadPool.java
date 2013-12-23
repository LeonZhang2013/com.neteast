package com.lib.net;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.util.Log;

/**
 * 该线程池传入的线程需要实现 Comparable 接口
 * @author LeonZhang E-mail: zhanglinlang1@163.com
 * @createtime 2012-11-16
 */
public class MyThreadPool extends ThreadPoolExecutor {

	private static MyThreadPool threadPool;
	private static int threadNum = 4;
	
	public static synchronized MyThreadPool getThreadPool(){
		if(threadPool==null){
			Log.i("url", "TimeUnit.MILLISECONDS = "+TimeUnit.MILLISECONDS);
			threadPool = new MyThreadPool(threadNum, threadNum, 0L, TimeUnit.MILLISECONDS, new PriorityBlockingQueue<Runnable>());
		}
		return threadPool;
	}
	
	/**
	 * 构造方法
	 * @param corePoolSize 指的是保留的线程池大小。
	 * @param maximumPoolSize 指的是线程池的最大大小。
	 * @param keepAliveTime 指的是空闲线程结束的超时时间。
	 * @param unit 是一个枚举，表示 keepAliveTime 的单位。
	 * @param workQueue 线程队列
	 */
	
    public MyThreadPool(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	@Override
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new ComparableFutureTask<T>(runnable, value);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new ComparableFutureTask<T>(callable);
    }

    protected class ComparableFutureTask<V>
            extends FutureTask<V> implements Comparable<ComparableFutureTask<V>> {
        private Object object;

        public ComparableFutureTask(Callable<V> callable) {
            super(callable);
            object = callable;
        }

        public ComparableFutureTask(Runnable runnable, V result) {
            super(runnable, result);
            object = runnable;
        }

        @SuppressWarnings("unchecked")
        public int compareTo(ComparableFutureTask<V> o) {
            if (this == o) {
                return 0;
            }
            if (o == null) {
                return -1; // high priority
            }
            if (object != null && o.object != null) {
                if (object.getClass().equals(o.object.getClass())) {
                    if (object instanceof Comparable) {
                        return ((Comparable<Object>) object).compareTo(o.object);
                    }
                }
            }
            return 0;
        }
    }
}
