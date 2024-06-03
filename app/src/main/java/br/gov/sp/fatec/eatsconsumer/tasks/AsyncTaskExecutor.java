package br.gov.sp.fatec.eatsconsumer.tasks;


import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class AsyncTaskExecutor<Params, Progress, Result> {

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public AsyncTaskExecutor() {
        // Optional: Initialize something if needed
    }

    protected abstract void onPreExecute();
    protected abstract Result doInBackground(Params... params);
    protected abstract void onPostExecute(Result result);

    public void execute(final Params... params) {
        onPreExecute();

        executorService.submit(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                final Result result = doInBackground(params);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        onPostExecute(result);
                    }
                });
                return null;
            }
        });
    }

    public void shutDown() {
        executorService.shutdown();
    }
}
