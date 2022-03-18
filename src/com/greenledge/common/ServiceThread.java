package com.greenledge.common;

import java.util.LinkedList;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Thread to run background tasks inside.
 * @author Vadim Lopatin
 */
public class ServiceThread extends Thread {

	public ServiceThread(String name) {
		super(name);
	}

	/**
	 * Post task for execution.
	 * @param task is runnable to call
	 */
	public void post(Runnable task) {
		synchronized (mQueue) {
			if (mHandler == null || mStopped) {
				Log.w("ServiceThread","Thread is not yet started, just adding to queue " + task);
				mQueue.addLast(task);
			} else {
				postQueuedTasks();
				mHandler.post(task);
			}
		}
	}

	/**
	 * Post task for execution at front of queue.
	 * @param task is runnable to call
	 */
	public void postAtFrontOfQueue(Runnable task) {
		synchronized (mQueue) {
			if (mHandler == null || mStopped)
				mQueue.addLast(task);
			else {
				postQueuedTasks();
				mHandler.postAtFrontOfQueue(task);
			}
		}
	}

	/**
	 * Post task for execution with delay.
	 * @param task is runnable to call
	 */
	public void postDelayed(Runnable task, long delayMillis) {
		synchronized (mQueue) {
			if (mHandler == null || mStopped)
				mQueue.addLast(task);
			else {
				postQueuedTasks();
				mHandler.postDelayed(task, delayMillis);
			}
		}
	}

	private void postQueuedTasks() {
		while (mQueue.size() > 0) {
			Runnable t = mQueue.removeFirst();
			Log.w("ServiceThread","Executing queued task " + t);
			mHandler.post(t);
		}
	}

	public boolean waitForCompletion(long timeout) {
		final Object lock = new Object();
		synchronized (lock) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					synchronized (lock) {
						lock.notify();
					}
				}
			});
			try {
				lock.wait(timeout);
				return true;
			} catch (InterruptedException e) {
				Log.i("ServiceThread","Waiting is interrupted");
			}
		}
		return false;
	}

	public void stop(final long timeout) {
		Log.i("ServiceThread","Stop is called. Not supported.");
		waitForCompletion(timeout);
		mHandler.getLooper().quit();
	}

	public boolean isStopped() {
		return mStopped;
	}

	private LinkedList<Runnable> mQueue = new LinkedList<Runnable>();

	@Override
	public void run() {
		Log.i("ServiceThread","Running service thread");
		Looper.prepare();
		mHandler = new Handler() {
			public void handleMessage( Message message )
			{
				Log.d("ServiceThread","message: " + message);
			}
		};
		Log.i("ServiceThread","Service thread handler is created");
		synchronized (mQueue) {
			postQueuedTasks();
			mStopped = false;
		}
		Looper.loop();
		mHandler = null;
		Log.i("ServiceThread","Exiting background thread");
	}
	private Handler mHandler;
	private boolean mStopped = true;
}
