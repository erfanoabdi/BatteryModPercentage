package ir.erfanabdi.batterymodpercentage;

import android.content.res.XResources;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

/**
 * Created by erfanabdi on 8/15/17.
 */

public class Enhancer implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.motorola.modservice"))
            return;
    }

    public static void enhance(String start, String stop){
        String array = start + "," + stop + "," + start + "," + stop + ",16,100,1";

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
