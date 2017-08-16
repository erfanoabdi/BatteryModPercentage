package ir.erfanabdi.batterymodpercentage;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.FileObserver;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static ir.erfanabdi.batterymodpercentage.Enhancer.enhance;

/**
 * Created by erfanabdi on 8/15/17.
 */

public class EffEnhancer extends AppCompatActivity {
    Switch eff_on;
    EditText soc_stop, soc_start;
    TextView textViewStart, textViewStop, effstatus;
    Button setButton;
    Context context;

    public interface FileObserverListener {
        void onFileUpdated(String path);
        void onFileAttributesChanged(String path);
    }

    private WorldReadablePrefs prefs;
    private FileObserver mFileObserver;
    private List<FileObserverListener> mFileObserverListeners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enhancer);
        context = this;
        fixFolderPermissionsAsync();
        String[] perms = new String[] { Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE };

        List<String> reqPerms = new ArrayList<>();
        for (String perm : perms) {
            if (context.checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                reqPerms.add(perm);
            }
        }
        if (!reqPerms.isEmpty())
            requestPermissions(reqPerms.toArray(new String[]{}), 0);


        mFileObserverListeners = new ArrayList<>();
        prefs =  new WorldReadablePrefs(context, "EffEnhc");
        mFileObserverListeners.add(prefs);
        registerFileObserver();

        eff_on = (Switch) findViewById(R.id.eff_on);
        soc_stop = (EditText) findViewById(R.id.eff_stop);
        soc_start = (EditText) findViewById(R.id.eff_start);
        textViewStart = (TextView) findViewById(R.id.textViewStart);
        textViewStop = (TextView) findViewById(R.id.textViewStop);
        effstatus = (TextView) findViewById(R.id.effstatus);
        setButton = (Button) findViewById(R.id.eff_set);

        boolean eff_on_pref = prefs.getBoolean("eff_on", false);
        String soc_stop_pref = prefs.getString("soc_stop", "80");
        String soc_start_pref = prefs.getString("soc_start", "79");
        eff_on.setChecked(eff_on_pref);
        soc_start.setText(soc_start_pref);
        soc_stop.setText(soc_stop_pref);
        setall(eff_on_pref);

        setButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String start, stop;
                start = soc_start.getText().toString().trim();
                stop = soc_stop.getText().toString().trim();
                prefs.edit().putString("soc_stop", stop).commit();
                prefs.edit().putString("soc_start", start).commit();

                prefs.fixPermissions(true);
                fixFolderPermissionsAsync();
                //enhance(start, stop);
                Toast t = Toast.makeText(context, "Efficiency Mode Values Changed just Reboot", Toast.LENGTH_SHORT);
                t.show();
            }
        });

        eff_on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prefs.edit().putBoolean("eff_on", isChecked).commit();
                setall(isChecked);
            }
        });
    }

    private void setall(boolean what){
        soc_stop.setEnabled(what);
        soc_start.setEnabled(what);
        textViewStart.setEnabled(what);
        textViewStop.setEnabled(what);
        setButton.setEnabled(what);
    }

    public void fixFolderPermissionsAsync() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // main dir
                File pkgFolder = context.getDataDir();
                if (pkgFolder.exists()) {
                    pkgFolder.setExecutable(true, false);
                    pkgFolder.setReadable(true, false);
                }
                // cache dir
                File cacheFolder = context.getCacheDir();
                if (cacheFolder.exists()) {
                    cacheFolder.setExecutable(true, false);
                    cacheFolder.setReadable(true, false);
                }
                // files dir
                File filesFolder = context.getFilesDir();
                if (filesFolder.exists()) {
                    filesFolder.setExecutable(true, false);
                    filesFolder.setReadable(true, false);
                    for (File f : filesFolder.listFiles()) {
                        f.setExecutable(true, false);
                        f.setReadable(true, false);
                    }
                }
                // app picker
                File appPickerFolder = new File(context.getFilesDir() + "/app_picker");
                if (appPickerFolder.exists()) {
                    appPickerFolder.setExecutable(true, false);
                    appPickerFolder.setReadable(true, false);
                    for (File f : appPickerFolder.listFiles()) {
                        f.setExecutable(true, false);
                        f.setReadable(true, false);
                    }
                }
            }
        });
    }

    private void registerFileObserver() {
        mFileObserver = new FileObserver(context.getDataDir() + "/shared_prefs",
                FileObserver.ATTRIB | FileObserver.CLOSE_WRITE) {
            @Override
            public void onEvent(int event, String path) {
                for (FileObserverListener l : mFileObserverListeners) {
                    if ((event & FileObserver.ATTRIB) != 0)
                        l.onFileAttributesChanged(path);
                    if ((event & FileObserver.CLOSE_WRITE) != 0)
                        l.onFileUpdated(path);
                }
            }
        };
        mFileObserver.startWatching();
    }
}
