package pt.fabm;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class AsyncResponse<T> implements Callable<T> {
    private T response;
    CountDownLatch countDownLatch = new CountDownLatch(1);
    @Override
    public T call() throws Exception {
        System.out.println("start:"+System.nanoTime());
        if(!countDownLatch.await(1000, TimeUnit.SECONDS)){
            throw new IllegalStateException("Time out!");
        }
        return response;
    }

    public void setResponse(T response) {
        System.out.println("set " + Thread.currentThread().getName());
        this.response = response;
        countDownLatch.countDown();
    }

}
