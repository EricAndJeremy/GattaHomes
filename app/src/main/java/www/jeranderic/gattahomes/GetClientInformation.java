package www.jeranderic.gattahomes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A screen that accepts user information and writes it to a log file before continuing on to
 * virtual tour.
 */
public class GetClientInformation extends AppCompatActivity {

    private String name;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_client_information);
        Spinner spin = (Spinner) findViewById(R.id.move);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.move_dates, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (spin != null) {
            spin.setAdapter(adapter);
        }
        // Set up the login form.
    }

    /**
     * the user has pressed the continue button, save info then continue to virtual tour
     */
    public void cont(View v) {
        TextView nameview = (TextView) findViewById(R.id.name);
        TextView emailview = (TextView) findViewById(R.id.email);

        name = nameview.getText() + "";
        email = emailview.getText() + "";

        File file = getFileStreamPath("test.txt");

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file);
            writer.append("here");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Emailer e = new Emailer();
        try {
            e.sendMail("Virtual Tour Participant",
                    "Name: " + name + "\nE-mail: " + email,
                    "virtualtour@gattahomes.com",
                    "eric_froese2@hotmail.com");
        } catch (Exception e1) {
            //email failed to send
            e1.printStackTrace();
        }
        Intent i = new Intent();
        i.setClass(this, Display.class);
        i.putExtra("name", name);
        i.putExtra("email", email);
        startActivity(i);
    }

    /**
     * the user has pressed the skip button, continue to virtual tour
     */
    public void skip() {
        Intent i = new Intent();
        i.setClass(this, Display.class);
        startActivity(i);
    }
}

