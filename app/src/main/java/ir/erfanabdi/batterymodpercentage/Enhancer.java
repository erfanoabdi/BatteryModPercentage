package ir.erfanabdi.batterymodpercentage;

import android.content.res.XResources;
import android.os.Build;
import android.util.Log;

import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;

/**
 * Created by erfanabdi on 8/15/17.
 */

public class Enhancer implements IXposedHookZygoteInit {
    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        Log.i("EffEnhc", "Efficiency Enhancer Started");
        XSharedPreferences prefs = new XSharedPreferences("ir.erfanabdi.batterymodpercentage", "EffEnhc");
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M)
            prefs.makeWorldReadable();

        boolean eff_on_pref = prefs.getBoolean("eff_on", false);
        String y = eff_on_pref ? "Enabled" : "Disabled";
        Log.i("EffEnhc", "Efficiency Enhancer is " + y);
        if (eff_on_pref){
            String stop = prefs.getString("soc_stop", "80");
            String start = prefs.getString("soc_start", "79");
            enhance(start, stop);
        }

    }

    public static void enhance(String start, String stop){
        String array = start + "," + stop + "," + start + "," + stop + ",16,100,1";
        Log.i("EffEnhc", "Setting Efficiency Array to " + array);
        XResources.setSystemWideReplacement("com.motorola.modservice", "array", "eb_defaults", StringToIntArray(array));
    }

    public static int[] StringToIntArray(String input) throws NumberFormatException{
        String[] stringArray = input.split(",");
        int[] result = new int[stringArray.length];

        for(int i = 0; i < stringArray.length; i++)
            try {
                result[i] = Integer.parseInt(stringArray[i].trim());
            } catch (NumberFormatException nfe) {
                throw nfe;
            };

        return result;
    }
}
