package ir.erfanabdi.batterymodpercentage;

import android.os.Build;
import android.util.Log;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by erfanabdi on 8/15/17.
 */

public class Enhancer implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.motorola.modservice"))
            return;

        XSharedPreferences prefs = new XSharedPreferences("ir.erfanabdi.batterymodpercentage", "EffEnhc");
        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M)
            prefs.makeWorldReadable();

        boolean eff_on_pref = prefs.getBoolean("eff_on", false);
        String y = eff_on_pref ? "Enabled" : "Disabled";
        Log.i("EffEnhc", "Efficiency Enhancer is " + y);
        if (eff_on_pref) {
            try {
                XposedHelpers.findAndHookMethod("com.motorola.modservice.ui.g", lpparam.classLoader, "a", "android.content.Context", int.class, new XC_MethodHook() {
                    protected final void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                        int i = (Integer) methodHookParam.args[1];
                        Log.i("EffEnhc", "methodHookParam : " + i);

                        XSharedPreferences prefs = new XSharedPreferences("ir.erfanabdi.batterymodpercentage", "EffEnhc");
                        if (android.os.Build.VERSION.SDK_INT <= Build.VERSION_CODES.M)
                            prefs.makeWorldReadable();

                        String stop = prefs.getString("soc_stop", "80");
                        String start = prefs.getString("soc_start", "79");
                        String array = start + "," + stop + "," + start + "," + stop + ",16,100,1";
                        Log.i("EffEnhc", "Efficiency Array : " + array);
                        String[] stringArray = array.split(",");
                        methodHookParam.setResult(Integer.parseInt(stringArray[i].trim()));
                    }
                });
            } catch (Throwable t) {
                XposedBridge.log(t);
            }
        }
    }
}
