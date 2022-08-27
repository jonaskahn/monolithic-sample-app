package io.github.tuyendev.mbs.common.thread;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class RetryOnRejectedExecution implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        try {
            executor.getQueue().put(r);
        } catch (InterruptedException e) {
            throw new RejectedExecutionException("Unexpected InterruptedException while retry add task to queue...", e);
        }
    }
}
