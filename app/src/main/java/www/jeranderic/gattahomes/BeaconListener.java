package www.jeranderic.gattahomes;

import android.content.Context;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;
import java.util.UUID;

public class BeaconListener extends BeaconManager implements BeaconManager.RangingListener, Runnable {

    public int beaconId;
    private Region region;

    public BeaconListener(Context context) {
        super(context);
        this.beaconId = 0;
        this.setBackgroundScanPeriod(1000, 500);
        this.setRangingListener(this);
        this.region = new Region("Gatta Home Showcase", UUID.fromString("0C22AC37-4957-55F7-AAF6-9579F324E008"), null, null)
    }

    private void setBeaconId(int id) {
        this.beaconId = id;
    }

    @Override
    public void onBeaconsDiscovered(Region region, List<Beacon> list) {
        if (!list.isEmpty()) {
            this.setBeaconId(list.get(0).getMajor());
        }
    }

    @Override
    public void run() {
        this.startRanging(this.region);
    }
}
