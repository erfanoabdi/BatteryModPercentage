package ir.erfanabdi.batterymodpercentage;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    Context context;
    public static String gb_battery = "/sys/class/power_supply/gb_battery/";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    String[] getfiles(String path) {
        File dirFileObj = new File(path);
        File[] files = dirFileObj.listFiles();

        String[] fileNames;
        if (files != null) {
            fileNames = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                fileNames[i] = files[i].getName() + ": " + getdata(files[i].getPath());
            }
        } else {
            fileNames = new String[1];
            fileNames[0] = "No Mod Found";
        }
        return fileNames;
    }

    static String getdata(String path) {
        ProcessBuilder cmd;
        String result = "";

        try {
            String[] args = {"/system/bin/cat", path};
            cmd = new ProcessBuilder(args);

            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[32768];
            int read = 0;
            while ((read = in.read(re, 0, 32768)) != -1) {
                String string = new String(re, 0, read);
                result += string;
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result.trim();
    }

    public static String getCapacity(){
        String result = getdata(gb_battery + "capacity");
        if (result.trim() == "")
            result = "0";
        return result.trim();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        try {
            Runtime.getRuntime().exec("su");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Button clickButton = (Button) findViewById(R.id.ref);
        clickButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                TextView tv = (TextView) findViewById(R.id.perc);
                tv.setText(getCapacity());

                ListView list = (ListView) findViewById(R.id.list);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                        android.R.layout.simple_list_item_1, android.R.id.text1, getfiles(gb_battery));

                list.setAdapter(adapter);
            }
        });

        TextView tv = (TextView) findViewById(R.id.perc);
        tv.setText(getCapacity());

        ListView list = (ListView) findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, getfiles(gb_battery));

        list.setAdapter(adapter);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://ir.erfanabdi.batterymodpercentage/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://ir.erfanabdi.batterymodpercentage/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
