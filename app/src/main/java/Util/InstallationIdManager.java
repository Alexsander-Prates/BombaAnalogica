package Util;

import android.content.Context;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import java.util.UUID;

public class InstallationIdManager {

    private static final String UNIQUE_WORK_NAME = "InstallationIdWorker";
    private static final String KEY_INSTALLATION_ID = "installationId";

    private static String installationId;

    public static String getInstallationId(Context context) {
        if (installationId == null) {
            installationId = retrieveInstallationId(context);
            if (installationId == null) {
                installationId = generateInstallationId(context);
                storeInstallationId(context, installationId);
            }
        }
        return installationId;
    }

    private static String retrieveInstallationId(Context context) {
        String installationId = null;
        try {
            installationId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return installationId;
    }

    private static String generateInstallationId(Context context) {
        return UUID.randomUUID().toString();
    }

    private static void storeInstallationId(Context context, String installationId) {
        Data inputData = new Data.Builder()
                .putString(KEY_INSTALLATION_ID, installationId)
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(InstallationIdWorker.class)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(context.getApplicationContext())
                .enqueueUniqueWork(UNIQUE_WORK_NAME, ExistingWorkPolicy.REPLACE, workRequest);
    }

    public static class InstallationIdWorker extends Worker {

        public InstallationIdWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
            super(context, workerParams);
        }

        @NonNull
        @Override
        public Result doWork() {
            String installationId = getInputData().getString(KEY_INSTALLATION_ID);
            if (installationId != null) {
                Context context = getApplicationContext();
                context.getSharedPreferences("InstallationId", Context.MODE_PRIVATE)
                        .edit()
                        .putString(KEY_INSTALLATION_ID, installationId)
                        .apply();
            }
            return Result.success();
        }
    }
}
