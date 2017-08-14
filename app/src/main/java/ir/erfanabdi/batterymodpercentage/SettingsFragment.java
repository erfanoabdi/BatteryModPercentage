package ir.erfanabdi.batterymodpercentage;

/**
 * Created by erfanabdi on 8/15/17.
 */
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsFragment extends PreferenceActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.app_preferences);
    }

}
