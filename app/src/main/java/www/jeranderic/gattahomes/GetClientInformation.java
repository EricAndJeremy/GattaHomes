package www.jeranderic.gattahomes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioGroup;
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

    private static final String TAG = "GetClientInformation";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_client_information);
        Spinner movingTime = (Spinner) findViewById(R.id.move);
        Spinner houseType = (Spinner) findViewById(R.id.house_type);
        Spinner houseSizeType = (Spinner) findViewById(R.id.house_size);
        Spinner roomNumType = (Spinner) findViewById(R.id.num_rooms);
        Spinner lotSizeType = (Spinner) findViewById(R.id.lot_size);
        Spinner budgetType = (Spinner) findViewById(R.id.budget);
        ArrayAdapter<CharSequence> budgetAdapter = ArrayAdapter.createFromResource(this, R.array.budgets, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> lotSizeAdapter = ArrayAdapter.createFromResource(this, R.array.lot_sizes, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> numRoomAdapter = ArrayAdapter.createFromResource(this, R.array.num_rooms, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> houseSizeAdapter = ArrayAdapter.createFromResource(this, R.array.home_size, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> houseAdapter = ArrayAdapter.createFromResource(this, R.array.house_types, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> moveAdapter = ArrayAdapter.createFromResource(this, R.array.move_dates, android.R.layout.simple_spinner_item);
        moveAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        houseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numRoomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lotSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        budgetAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (movingTime != null) {
            movingTime.setAdapter(moveAdapter);
        }
        if (houseType != null) {
            houseType.setAdapter(houseAdapter);
        }
        if (houseSizeType != null) {
            houseSizeType.setAdapter(houseSizeAdapter);
        }
        if (roomNumType != null) {
            roomNumType.setAdapter(numRoomAdapter);
        }
        if (lotSizeType != null) {
            lotSizeType.setAdapter(lotSizeAdapter);
        }
        if (budgetType != null) {
            budgetType.setAdapter(budgetAdapter);
        }
        // Set up the login form.
    }

    /**
     * the user has pressed the continue button, save info then continue to virtual tour
     */
    public void cont(View v) {

        TextView name = (TextView) findViewById(R.id.name);
        TextView email = (TextView) findViewById(R.id.email);
        TextView phoneNumber = (TextView) findViewById(R.id.phone);
        TextView city = (TextView) findViewById(R.id.city);
        Spinner moving = (Spinner) findViewById(R.id.move);
        Spinner houseType = (Spinner) findViewById(R.id.house_type);
        Spinner houseSize = (Spinner) findViewById(R.id.house_size);
        Spinner numRooms = (Spinner) findViewById(R.id.num_rooms);
        Spinner lotSize = (Spinner) findViewById(R.id.lot_size);
        Spinner budget = (Spinner) findViewById(R.id.budget);
        RadioGroup updates = (RadioGroup) findViewById(R.id.updates);

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

        String emailString = "";
        emailString += "Name: " + name.getText() + ".\n";
        emailString += "Email: " + email.getText() + ".\n";
        emailString += "Phone Number: " + phoneNumber.getText() + ".\n";
        emailString += "City: " + city.getText() + ".\n";
        emailString += "Moving: " + moving.getSelectedItem().toString() + ".\n";
        emailString += "House Type: " + houseType.getSelectedItem().toString() + ".\n";
        emailString += "House Size: " + houseSize.getSelectedItem().toString() + ".\n";
        emailString += "Number of Rooms: " + numRooms.getSelectedItem().toString() + ".\n";
        emailString += "Lot Size: " + lotSize.getSelectedItem().toString() + ".\n";
        emailString += "Budget: " + budget.getSelectedItem().toString() + ".\n";
        emailString += "Send Updates: " + findViewById(updates.getCheckedRadioButtonId()).toString() + ".\n";

        Emailer e = new Emailer();
        try {
            e.sendMail("Virtual Tour Participant",
                    emailString,
                    "virtualtour@gattahomes.com",
                    "eric_froese2@hotmail.com");
        } catch (Exception e1) {
            //email failed to send
            e1.printStackTrace();
        }
        Intent i = new Intent();
        i.setClass(this, Display.class);
        i.putExtra("name", name.getText().toString());
        i.putExtra("email", email.getText().toString());
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

