package com.lifeleveling.app.services.FirebaseCloudMessaging

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.lifeleveling.app.util.AndroidLogger

/**
 * Worker classes are instantiated at runtime by WorkManager and the .doWork method is called on a pre-specified background thread
 * (see Configuration.executor). This method is for synchronous processing of your work, meaning that once you return from that method,
 * the Worker is considered to be finished and will be destroyed. If you need to do your work asynchronously or call asynchronous APIs,
 * you should use ListenableWorker.
 *
 * In case the work is preempted for any reason, the same instance of Worker is not reused.
 * This means that .doWork is called exactly once per Worker instance. A new Worker is created if a unit of work needs to be rerun.
 *
 * A Worker is given a maximum of ten minutes to finish its execution and return
 * an androidx.work.ListenableWorker.Result. After this time has expired, the Worker will be signalled to stop.
 *
 * this is an example worker thread that may need to handle any tasks longer than 10 seconds that are triggered by FCM
 * @param appContext the context in which this worker will be spawned from
 * @param workerParams
 */
class LLWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {

    val logger = AndroidLogger()
    override fun doWork(): Result {
        logger.d(TAG, "Performing long running task in scheduled job")
        // TODO(developer): add any long running tasks triggered by FCM here
        return Result.success()
    }

    companion object {
        private const val TAG = "LLWorker"
    }
}