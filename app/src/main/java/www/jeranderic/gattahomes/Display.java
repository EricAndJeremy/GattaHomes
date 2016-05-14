package www.jeranderic.gattahomes;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.SystemRequirementsChecker;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Display extends Activity {

    ArrayList<RoomElement> elements;

    private BeaconListener beaconManager;
    private Region region;


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

        setUp();

        beaconManager = new BeaconListener(this);
        beaconManager.setBackgroundScanPeriod(1000, 20);

        region = new Region("Gatta Homes Showcase", UUID.fromString("0C22AC37-4957-55F7-AAF6-9579F324E008"), null, null);

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);
                    Log.d("beacon", "closest: " + nearestBeacon);
                    updateDisplay(list.get(0).getMajor());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        SystemRequirementsChecker.checkWithDefaultDialogs(this);

        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);

        super.onPause();
    }

    /**
     * this class initializes all the information and builds the map that will be associated with the beacons
     */
    public void setUp() {
        elements = new ArrayList<>();
        //master bedroom = group 1
        elements.add(new RoomElement(1, 0, "This is the title", "THIS IS THE DESCRIPTION!!!!", "/path/to/video"));
        elements.add(new RoomElement(1, 1, "amsdnfkjasd", "deasdfmndkfjsc", "/path/to/video"));
        elements.add(new RoomElement(1, 2, "jeremy sucks", "he sucks all of them", "/path/to/video"));
        //living room = group 2
        elements.add(new RoomElement(2, 0, "jeremy sucks", "doesnt discriminate, loves all shapes and sizes", "/path/to/video"));
        elements.add(new RoomElement(2, 1, "haha", "i made an app describing jeremy perfectly", "/path/to/video"));
        elements.add(new RoomElement(2, 2, "wow", "much sucking wowowowow ", "/path/to/video"));
        elements.add(new RoomElement(2, 3, "title", "desc", "/path/to/video"));
        //dining room = group 3
        elements.add(new RoomElement(3, 0, "best app ever made", "deeeeeeescripppptionnnn", "/path/to/video"));
        elements.add(new RoomElement(3, 1, "this is the best", "desc", "/path/to/video"));
        elements.add(new RoomElement(3, 2, "ohhhhh herrooo!!", "-_- i love rice", "/path/to/video"));
        elements.add(new RoomElement(3, 3, "Jeremy likes men", "tis all", "/path/to/video"));
        elements.add(new RoomElement(3, 4, "the holy hand grenade", "thou shal counteth to three, you may count to two, so long as you continue on to three", "/path/to/video"));
    }

    public void startListening() {
        int current_group_id = 100000;

        int temp;
        while (true) {
            try {
                Thread.sleep(4000);
                temp = 1;
                Toast.makeText(this, current_group_id + "",
                        Toast.LENGTH_SHORT).show();
                if (current_group_id == 100000) {
                    current_group_id = temp;
                } else {
                    if (current_group_id != temp) { //this means we picked up a new beacon
                        current_group_id = temp;
                        Toast.makeText(this, current_group_id + "",
                                Toast.LENGTH_SHORT).show();
                        //updateDisplay();
                    } else {
                        // do nothing, do not change display
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public void updateDisplay(int beaconid) {
        LinearLayout listview = (LinearLayout) findViewById(R.id.list);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final ImageView playbutton = (ImageView) findViewById(R.id.playbutton);
        final LinearLayout bigview = (LinearLayout) findViewById(R.id.bigview);

        listview.removeAllViews();
        LinearLayout group = new LinearLayout(this);
        LinearLayout.LayoutParams gparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        group.setLayoutParams(gparams);
        group.setOrientation(LinearLayout.VERTICAL);
        LinearLayout button;
        TextView text;
        ImageView img;

        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).groupID == beaconid) {
                button = new LinearLayout(this);
                text = new TextView(this);
                // specifying vertical orientation
                button.setOrientation(LinearLayout.HORIZONTAL);
                // creating LayoutParams
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
                button.setLayoutParams(params);
                button.setBackgroundResource(R.drawable.rounded);
                text.setTextSize(25);
                text.setText("id: " + elements.get(i).title);
                button.addView(text);
                final int tempid = elements.get(i).id;
                final int tempgroupid = elements.get(i).groupID;
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateBigDisplay(tempgroupid, tempid);
                        //show big view
                        bigview.setVisibility(View.VISIBLE);
                        fab.setVisibility(View.VISIBLE);
                        playbutton.setVisibility(View.VISIBLE);
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                fab.setVisibility(View.GONE);
                                bigview.setVisibility(View.GONE);
                                playbutton.setVisibility(View.GONE);
                            }
                        });
                    }
                });
                group.addView(button);
            }
        }
        listview.addView(group);
    }

    public void updateBigDisplay(int groupID, int id) {
        TextView title = (TextView) findViewById(R.id.title);
        TextView description = (TextView) findViewById(R.id.description);

        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).groupID == groupID && elements.get(i).id == id) {
                title.setText(elements.get(i).title);
                description.setText(elements.get(i).description);
            }
        }
    }
}