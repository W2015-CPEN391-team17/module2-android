package ca.ubc.cpen391team17;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HintsActivity extends AppCompatActivity {

    String message = " ";
    /* Opens MapActivity*/
    public void openMap1(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("mapName", message);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hints);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab1);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMap1(view);
            }
        });

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        message = extras.getString("title_key");
        String message1 = extras.getString("clue1_key");
        String message2 = extras.getString("clue2_key");
        String message3 = extras.getString("clue3_key");
        String message4 = extras.getString("clue4_key");
        String message5 = extras.getString("clue5_key");

        TextView title = (TextView) findViewById(R.id.title_text);
        title.setTextSize(25);
        title.setText(message);

        TextView clue1 = (TextView) findViewById(R.id.clue1_text);
        clue1.setTextSize(15);
        clue1.setText(message1);

        TextView clue2 = (TextView) findViewById(R.id.clue2_text);
        clue2.setTextSize(15);
        clue2.setText(message2);

        TextView clue3 = (TextView) findViewById(R.id.clue3_text);
        clue3.setTextSize(15);
        clue3.setText(message3);

        TextView clue4 = (TextView) findViewById(R.id.clue4_text);
        clue4.setTextSize(15);
        clue4.setText(message4);

        TextView clue5 = (TextView) findViewById(R.id.clue5_text);
        clue5.setTextSize(15);
        clue5.setText(message5);

    }

    /**
     * Called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        // Finish the activity
        finish();
    }

}
