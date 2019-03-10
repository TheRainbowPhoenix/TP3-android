package ml.pho3.tp3.service;


import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;

import ml.pho3.tp3.service.UpdaterService;
import ml.pho3.utils.Utils;

public class UpdateJob extends JobService {
    private static final String TAG = "SyncService";

    @Override
    public boolean onStartJob(JobParameters params) {
        Intent service = new Intent(getApplicationContext(), UpdaterService.class);
        getApplicationContext().startService(service);
        Utils.scheduleJob(getApplicationContext()); // reschedule the job
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return true;
    }
}
