package www.jeranderic.gattahomes;

import java.util.Random;

/**
 * Created by ericsmacbook on 2016-05-14.
 */
public class BeaconListener {
    //this class is just for testing

    public BeaconListener(){}

    public int getID(){
        Random randy = new Random();
        return randy.nextInt(4);
    }
}
