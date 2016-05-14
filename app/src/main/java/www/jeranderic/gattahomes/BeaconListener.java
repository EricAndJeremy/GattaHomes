package www.jeranderic.gattahomes;

import android.content.Context;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.List;
import java.util.UUID;

public class BeaconListener extends BeaconManager implements BeaconManager.RangingListener {

    public int beaconId;

    public BeaconListener(Context context) {
        super(context);
        this.beaconId = 0;
        this.setBackgroundScanPeriod(1000, 500);
        this.setRangingListener(this);
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
}
