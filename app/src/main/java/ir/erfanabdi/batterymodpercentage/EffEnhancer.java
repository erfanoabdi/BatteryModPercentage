package ir.erfanabdi.batterymodpercentage;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import static ir.erfanabdi.batterymodpercentage.Enhancer.enhance;

/**
 * Created by erfanabdi on 8/15/17.
 */

public class EffEnhancer extends AppCompatActivity {
    Switch eff_on;
    EditText soc_stop, soc_start;
    TextView textViewStart, textViewStop, effstatus;
    Button setButton;
    SharedPreferences prefs;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enhancer);
        prefs = this.getSharedPreferences("ir.erfanabdi.batterymodpercentage.eff", MODE_PRIVATE);
        context = this;

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
                SharedPreferences preferences = getSharedPreferences("ir.erfanabdi.batterymodpercentage.eff", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("soc_stop", stop);
                editor.putString("soc_start", start);
                editor.apply();

                enhance(start, stop);
                Toast t = Toast.makeText(context, "Efficiency Mode Values Changed", Toast.LENGTH_SHORT);
                t.show();
            }
        });

        eff_on.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                SharedPreferences preferences = getSharedPreferences("ir.erfanabdi.batterymodpercentage.eff", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("eff_on", isChecked);
                editor.apply();
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
}
