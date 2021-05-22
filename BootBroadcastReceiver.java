package com.example.womanssafetyapp.Service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.legacy.content.WakefulBroadcastReceiver;

import static android.content.ContentValues.TAG;

public class BootBroadcastReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Idhar se bhej raha instruction chalu hone ka");
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent mIntent = new Intent(context, ShakeService.class);
            mIntent.putExtra("maxCountValue", 10);
            ShakeService.enqueueWork(context, mIntent);
        }
    }
}
