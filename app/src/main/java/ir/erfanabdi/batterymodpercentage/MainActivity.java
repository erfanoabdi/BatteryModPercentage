package ir.erfanabdi.batterymodpercentage;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    Context context;
    public static boolean isrooted = false;
    public static Process pros;
    public static String gb_battery = "/sys/class/power_supply/gb_battery/";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    public static void rootcheck(){
        String result = "";
        try {
            pros = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(pros.getOutputStream());
            BufferedReader bf = new BufferedReader(new InputStreamReader(pros.getInputStream()));

            os.writeBytes("id -u\n");
            os.flush();

            result = bf.readLine();
            if(result == null)
                result = "-1";

            if (result.equals("0"))
                isrooted = true;
            else
                isrooted = false;
        } catch (IOException e) {
            isrooted = false;
            e.printStackTrace();
        }
    }

    String[] getfiles(String path) {
        String[] fileNames;
        String result = "";

        if (!isrooted)
            rootcheck();

        if (isrooted){
            try
            {
                DataOutputStream os = new DataOutputStream(pros.getOutputStream());
                BufferedReader bf = new BufferedReader(new InputStreamReader(pros.getInputStream()));

                os.writeBytes("[ ! -d " + path + " ] && echo -1 || echo 1\n");
                os.flush();
                result = bf.readLine();
                result = result.trim();
                if (result.equals("-1")){
                    //os.close();
                    //bf.close();
                    //pros.destroy();
                    isrooted = false;
                    fileNames = new String[1];
                    fileNames[0] = "No Mod Found";
                    return fileNames;
                }

                os.writeBytes("ls -1 " + path + " && echo NULL\n");
                os.flush();

                String test;
                int i=0, k=0, m=0;
                String[] allNames = new String[200];
                while(!(test = bf.readLine().trim()).equals("NULL"))
                {
                    allNames[i] = test;
                    i++;
                }

                for (int j=0;j<i;j++){
                    os.writeBytes("[ -f " + path + allNames[j] + " ] && echo 1 || echo -1\n");
                    os.flush();
                    result = bf.readLine();
                    result = result.trim();
                    if (result.equals("1"))
                        k++;
                }
                //FIXME: dirty hack :(

                fileNames = new String[k];
                for (int l=0;l<i;l++){
                    os.writeBytes("[ -f " + path + allNames[l] + " ] && echo 1 || echo -1\n");
                    os.flush();
                    result = bf.readLine();
                    result = result.trim();
                    if (result.equals("1")) {
                        fileNames[m] = allNames[l] + ": " + getdata(path + allNames[l]);
                        m++;
                    }
                }
                return fileNames;
            }
            catch(Exception ex)
            {
                fileNames = new String[1];
                fileNames[0] = "No Mod Found";
                return fileNames;
            }
        }
        if (!isrooted) {
            File dirFileObj = new File(path);
            File[] files = dirFileObj.listFiles();
            if (files != null) {
                int j = 0, l = 0;
                for (int i = 0; i < files.length; i++)
                    if (files[i].isFile())
                        j++;
                //FIXME: dirty hack :(
                fileNames = new String[j];
                for (int k = 0; k < files.length; k++) {
                    if (files[k].isFile()) {
                        fileNames[l] = files[k].getName() + ": " + getdata(files[k].getPath());
                        l++;
                    }
                }
                return fileNames;
            } else {
                fileNames = new String[1];
                fileNames[0] = "No Mod Found";
                return fileNames;
            }
        }
        fileNames = new String[1];
        fileNames[0] = "No Mod Found";
        return fileNames;
    }

    public static String getdata(String path) {
        String result = "";
        if (!isrooted)
            rootcheck();

        if (isrooted){
            try
            {
                DataOutputStream os = new DataOutputStream(pros.getOutputStream());
                BufferedReader bf = new BufferedReader(new InputStreamReader(pros.getInputStream()));

                os.writeBytes("[ ! -f " + path + " ] && echo -1 || echo 1\n");
                os.flush();
                result = bf.readLine();
                result = result.trim();
                if (result.equals("-1")){
                    //os.close();
                    //bf.close();
                    //pros.destroy();
                    isrooted = false;
                    return result;
                }

                os.writeBytes("cat " + path + "\n");
                os.flush();

                result = bf.readLine();
                result = result.trim();
            }
            catch(Exception ex)
            {
                result = "-1";
            }
        }

        if (!isrooted) {
            String[] args = new String[]{"/system/bin/cat", path};
            ProcessBuilder cmd;
            try {
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
        }

        return result.trim();
    }

    public static String getCapacity(){
        String result = getdata(gb_battery + "capacity");
        if (result.trim() == "")
            result = "-1";
        return result.trim();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        final ModManagerBroadcastReceiver.LongOperation lo = new ModManagerBroadcastReceiver.LongOperation();

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
                if(lo.isCancelled()){
                    lo.execute("");
                }
            }
        });

        TextView tv = (TextView) findViewById(R.id.perc);
        tv.setText(getCapacity());

        ListView list = (ListView) findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, getfiles(gb_battery));

        list.setAdapter(adapter);

        if(lo.isCancelled()){
            lo.execute("");
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onDestroy() {
        pros.destroy();
        isrooted = false;
        super.onDestroy();
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
