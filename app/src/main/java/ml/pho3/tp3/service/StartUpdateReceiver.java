package ml.pho3.tp3.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ml.pho3.utils.Utils;

public class StartUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.scheduleJob(context);
    }
}
