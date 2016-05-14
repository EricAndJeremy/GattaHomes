package www.jeranderic.gattahomes;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class Display extends Activity {

    ArrayList<RoomElement> elements;
    BeaconListener b;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);

        final int mUIFlag = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getWindow().getDecorView().setSystemUiVisibility(mUIFlag);

        b = new BeaconListener();

        setUp();

        startListening();
    }

    /**
     * this class initializes all the information and builds the map that will be associated with the beacons
     */
    public void setUp() {
        elements = new ArrayList<>();
        //living room = group 0
        elements.add(new RoomElement(0, 0, "/path/to/image0"));
        elements.add(new RoomElement(0, 1, "/path/to/image1"));
        elements.add(new RoomElement(0, 2, "/path/to/image2"));
        elements.add(new RoomElement(0, 3, "/path/to/image3"));
        //master bedroom = group 1
        elements.add(new RoomElement(1, 0, "/path/to/image0"));
        elements.add(new RoomElement(1, 1, "/path/to/image1"));
        elements.add(new RoomElement(1, 2, "/path/to/image2"));
        elements.add(new RoomElement(1, 3, "/path/to/image3"));
        //living room = group 2
        elements.add(new RoomElement(2, 0, "/path/to/image0"));
        elements.add(new RoomElement(2, 1, "/path/to/image1"));
        elements.add(new RoomElement(2, 2, "/path/to/image2"));
        elements.add(new RoomElement(2, 3, "/path/to/image3"));
        //dining room = group 3
        elements.add(new RoomElement(3, 0, "/path/to/image0"));
        elements.add(new RoomElement(3, 1, "/path/to/image1"));
        elements.add(new RoomElement(3, 2, "/path/to/image2"));
        elements.add(new RoomElement(3, 3, "/path/to/image3"));
        elements.add(new RoomElement(3, 4, "/path/to/image3"));
    }

    public void startListening() {
        int current_group_id = 100000;
        int temp;
        while (true) {
            try {
                Thread.sleep(4000);
                temp = b.getID();
                if (current_group_id == 100000) {
                    current_group_id = temp;
                } else {
                    if (current_group_id != temp) { //this means we picked up a new beacon
                        current_group_id = temp;
                        updateDisplay();
                    } else {
                        // do nothing, do not change display
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public void updateDisplay() {
        View listview =  findViewById(R.id.listView);

        LinearLayout button = new LinearLayout(this);
        // specifying vertical orientation
        button.setOrientation(LinearLayout.HORIZONTAL);
        // creating LayoutParams
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        button.setLayoutParams(params);
    }
}
