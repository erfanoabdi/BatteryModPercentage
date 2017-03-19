package ir.erfanabdi.batterymodpercentage;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    String getCapicity(){
        ProcessBuilder cmd;
        String result = "";

        try {
            String[] args = { "/system/bin/cat", "/sys/class/power_supply/gb_battery/capacity" };
            cmd = new ProcessBuilder(args);

            Runtime.getRuntime().exec("su");
            Process process = cmd.start();
            InputStream in = process.getInputStream();
            byte[] re = new byte[32768];
            int read = 0;
            while ( (read = in.read(re, 0, 32768)) != -1) {
                String string = new String(re, 0, read);
                Log.e(getClass().getSimpleName(), string);
                result += string;
            }
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return result.trim();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        String result = getCapicity();
        TextView tv = (TextView)findViewById(R.id.perc);
        tv.setText(result.trim());


        Button clickButton = (Button) findViewById(R.id.ref);
        clickButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String result = getCapicity();
                TextView tv = (TextView)findViewById(R.id.perc);
                tv.setText(result.trim());

            }
        });
    }
}
