package ir.erfanabdi.batterymodpercentage;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

/**
 * Created by erfanabdi on 3/20/17.
 */
public class ModManagerBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_BIND_MANAGER = "com.motorola.mod.action.BIND_MANAGER";
    public static final String ACTION_MOD_ATTACH = "com.motorola.mod.action.MOD_ATTACH";
    public static final String ACTION_MOD_ATTACH_FAILED = "com.motorola.mod.action.MOD_ATTACH_FAILED";
    public static final String ACTION_MOD_CAPABILITY_CHANGED = "com.motorola.mod.action.MOD_CAPABILITY_CHANGED";
    public static final String ACTION_MOD_DETACH = "com.motorola.mod.action.MOD_DETACH";
    public static final String ACTION_MOD_ENUMERATION_DONE = "com.motorola.mod.action.MOD_ENUMERATION_DONE";
    public static final String ACTION_MOD_ERROR = "com.motorola.mod.action.MOD_ERROR";
    public static final String ACTION_MOD_EVENT = "com.motorola.mod.action.MOD_EVENT";
    public static final String ACTION_MOD_FIRMWARE_CHECK_UPDATE_ERROR = "com.motorola.mod.action.MOD_FIRMWARE_CHECK_UPDATE_ERROR";
    public static final String ACTION_MOD_FIRMWARE_DOWNLOAD_STATUS = "com.motorola.mod.action.MOD_FIRMWARE_DOWNLOAD_STATUS";
    public static final String ACTION_MOD_FIRMWARE_UPDATE_AVAILABLE = "com.motorola.mod.action.MOD_FIRMWARE_UPDATE_AVAILABLE";
    public static final String ACTION_MOD_FIRMWARE_UPDATE_CANCELLED = "com.motorola.mod.action.MOD_FW_UPDATE_CANCELLED";
    public static final String ACTION_MOD_FIRMWARE_UPDATE_CANCEL_STATUS = "com.motorola.mod.action.MOD_FW_UPDATE_CANCEL_STATUS";
    public static final String ACTION_MOD_FIRMWARE_UPDATE_DONE = "com.motorola.mod.action.MOD_FIRMWARE_UPDATE_DONE";
    public static final String ACTION_MOD_FIRMWARE_UPDATE_START = "com.motorola.mod.action.MOD_FIRMWARE_UPDATE_START";
    public static final String ACTION_MOD_FIRMWARE_UPDATE_STATUS = "com.motorola.mod.action.FMW_UPDATE_STATUS";
    public static final String ACTION_MOD_PRE_ATTACH_USER_CONSENT = "com.motorola.mod.action.MOD_PRE_ATTACH_USER_CONSENT";
    public static final String ACTION_MOD_REQUEST_FIRMWARE = "com.motorola.mod.action.MOD_REQUEST_FIRMWARE";
    public static final String ACTION_MOD_SERVICE_STARTED = "com.motorola.mod.action.SERVICE_STARTED";
    public static final String ACTION_MOD_USB_CONFLICT_DETECTED = "com.motorola.mod.action.MOD_USB_CONFLICT_DETECTED";
    public static final String ACTION_OEM_SUBSYSTEM = "com.motorola.mod.action.OEM_SUBSYSTEM";
    public static final String ACTION_OEM_SUBSYSTEM_GET = "com.motorola.mod.action.OEM_SUBSYSTEM_GET ";
    public static final String ACTION_OEM_SUBSYSTEM_SET = "com.motorola.mod.action.OEM_SUBSYSTEM_SET ";
    public static final String ACTION_OEM_SUBSYSTEM_UPDATE = "com.motorola.mod.action.OEM_SUBSYSTEM_UPDATE";
    public static final String ACTION_REQUEST_CONSENT_FOR_UNSECURE_FIRMWARE_UPDATE = "com.motorola.mod.action.UNSEC_FMW_CONSENT_REQ";
    public static final String ACTION_USER_CONSENT_RESP_FOR_UNSECURE_FIRMWARE = "com.motorola.mod.action.UNSEC_FMW_CONSENT_RESP";

    public NotificationCompat.Builder b;
    public NotificationManager nm;

    public boolean Quit_Task = false;

    public void onReceive(Context context , Intent intent) {
        String action = intent.getAction();

        b = new NotificationCompat.Builder(context);
        nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(action.equals(ACTION_MOD_ATTACH)) {
            Toast t = Toast.makeText(context, "Battery Mod: " + MainActivity.getCapacity() + "%", Toast.LENGTH_SHORT);
            t.show();

            b.setAutoCancel(true)
                .setContentTitle("Battery Mod: " + MainActivity.getCapacity() + "%")
                .setSmallIcon(R.drawable.ic_battery_mgr_mod)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.icon))
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setOngoing(true);

            nm.notify(1, b.build());
            Quit_Task = false;

            class LongOperation extends AsyncTask<String, Void, String> {

                void Sleep(int ms)
                {
                    try
                    {
                        Thread.sleep(ms);
                    }
                    catch (Exception e)
                    {
                    }
                }

                protected String oldres="100";
                @Override
                protected String doInBackground(String... params) {
                    String result = "99";
                    while (!Quit_Task) {
                        Sleep(1000);
                        result = MainActivity.getCapacity();
                        if (result != oldres) {
                            b.setContentTitle("Battery Mod: " + result + "%");
                            nm.notify(1, b.build());
                            oldres = result;
                        }
                    }
                    return "";
                }

                @Override
                protected void onPostExecute(String result) {

                }

                @Override
                protected void onPreExecute() {}

                @Override
                protected void onProgressUpdate(Void... values) {}
            }

            new LongOperation().execute("");


        }else if (action.equals(ACTION_MOD_DETACH)){
            Quit_Task = true;
            nm.cancel(1);
        }

    }

}