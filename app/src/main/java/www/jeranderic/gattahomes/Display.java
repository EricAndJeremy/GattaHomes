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

    /**
     * this class initializes all the information and builds the map that will be associated with the beacons
     */
    public void setUp() {
        elements = new ArrayList<>();
        //GROUP 1
        elements.add(new RoomElement(1, 0, "45-year Shingles" ,"We offer a wide variety of architectural shingles in many different textures, colours and shapes to choose from. The multi layered design gives them a thicker, richer appearance. Being multi layered, they are typically heavier in weight and have an improved warranty protection. In addition to our typical roof vents, we provide a solar powered attic fan which ensures that your attic space is always dry and cool. The extra venting and the solar reflectivity of the shingles will ensure that your roof lasts longer, reducing energy costs of the home throughout the year."));
        elements.add(new RoomElement(1, 1, "Exterior Cladding", "Our exteriors at Homes of elegance are a unique signature to our designs. Natural stones are used combined with stucco or Hardie finishes to create a  timeless feel in every home. Our stones are out-sourced from different quarries all over North America. We custom blend stone colours and textures to create a never before used stone look. Hardie is a fireproof cement based composite which comes in many colours and finishes such as, plank siding, board & batton, shakes or smooth sheeting. Today's stucco is a flexible polymer based coating that goes over a cement base coat over a layer of Styrofoam. This stucco is flexible enough to bridge a crack of up to 1/16th of an inch. Samples are typically made up for the clients, prior to signing off, to make sure the colours match the exterior in true daylight on site. Our bricks come in a vast choice of handmade bricks or reclaimed bricks from Old factories in Chicago."));
        elements.add(new RoomElement(1, 2, "Porches", "Beautiful large porches are always part of our designs here in Niagara On the Lake. Porch ceilings are dressed with clear pine or cedar planks to make the outdoor space seem like a part of the indoor living space throughout the year.  Screened rear porches are a great addition to turn the rear covered porch into an outdoor dining area. All of our porches have full foundations under them to make sure they never move. We also sneak a wine cellar under the porch to store all your wine, vegetables and cold meats. The rear covered porches make a smooth transition to a rear courtyard or patio area from your interior living space."));
        elements.add(new RoomElement(1, 3, "soffit, fascia, eaves trough ", "Aluminum soffit, fascia & eaves trough's comes in many colours bringing all the exterior finishes together. A heavier gauge aluminum will ensure that you receive  less warping and shrinkage.  Details are added to the exteriors such as dormers, cupolas, chimneys, arches, intricate moldings, and corbels which all create a feel of uniqueness to each home."));
        elements.add(new RoomElement(1, 4, "windows", "These windows are solid vinyl with no wood in them to warp, twist or decay. There are many bold colours to choose from that are available as a standard choice. We add SDL grills which are larger with additional internal spacers, not the typical internal grills. This actually simulates the true divided glass without compromising efficiency. Our glass is made of Low E glass with argon gas in between to create a better thermal result between the two seasons. With the exterior walls starting at 10' wall height, all our doors can now be 8' tall and they come with multi point hardware, which grabs the whole door for locking straightness and better security. Even our patio doors are 8 foot tall sliders, if you don't want French doors. There is also the option of double sliding doors to have the large openings. Transoms are often added to extend that opening taller to the outdoors. There are many window styles to choose from, but we try to fit the window style, size and shape to the scenery it sees through. For a home on the lake, we would go with wall to wall glass horizontally to capture that linear view. For those homes that back onto tall tree lots, we would go with tall, high windows to bring the greenery inside. We also try and slip in feature windows like ovals, circles, trapezoids, and many more shapes you can envision."));
        elements.add(new RoomElement(1, 5, "Multi-point hardware", "Multi point hardware gives you the Peace of mind that you have a secure and functional door hardware system. Operating as one, all three latches engage when the door is closed. One movement of the thumb or a key activates all three latches. Each latch is a deadbolt and cannot be compromised.\n" +
                "Bolts and roller strikes draw the door panel into perfect alignment with the jamb for a tight seal with no deflection.\n" +
                "There is also a Panic Release. A downward turn of the interior handle releases all three deadbolts.\n" +
                "This system is three times more resistant to break-ins and has a much better sealing mechanism. "));
        elements.add(new RoomElement(1, 6, "Solar paneled roof vent", "We offer a wide variety of architectural shingles in many different textures, colours and shapes to choose from. The multi layered design gives them a thicker, richer appearance. Being multi layered, they are typically heavier in weight and have an improved warranty protection. In addition to our typical roof vents, we provide a solar powered attic fan which ensures that your attic space is always dry and cool. The extra venting and the solar reflectivity of the shingles will ensure that your roof lasts longer, reducing energy costs of the home throughout the year."));
        elements.add(new RoomElement(1, 7, "Gas line for BBQ and range &...", "All our homes come GAS ready homes. Every range location is roughed in with a gas line for a gas range, we also add a gas line to the rear porch for a gas BBQ at the proper location where we have pre designed a spot for it in our landscape plan. We also leave an extra spot open at the main gas tree in the basement for any future additional gas lines needed."));

        //GROUP 2
        elements.add(new RoomElement(2, 0, "Carriage Style Sectional Doors", "Our carriage style garage doors are foam core insulated and come in many different profiles and styles. Along with many factory painted colors, we can now get them in numerous stain and wood colors. They are much thicker and have more pronounced styles and profiles then the steel pressed doors. Carriage doors also come with hardware like hinges, lock handles and other hardware to create your own distinct look. Along with the carriage doors, we always try and fit a jack shaft garage door opener which is much stronger and quieter than a belt or a chain driven opener. So, the extra money we spend on the garage doors and openers is well worth the look, feel, strength and silence we obtain from this system."));
        elements.add(new RoomElement(2, 1, "Finished garage", "All of our garages are totally finished just like the interior of the home. The walls are painted, with baseboard, casings and trim are just like the interior. Our standard two car garage starts with 20 by 20. Where more spaces are wanted, we can go tandem deep parking or even higher ceilings to accommodate car lifts."));
        elements.add(new RoomElement(2, 2, "Jack shaft garage door opener", "Along with the carriage doors, we always try and fit a jack shaft garage door opener which is much stronger and quieter than a belt or a chain driven opener. This unit has less moving parts which reduces noise and wear and tear on your motors. So, the extra money we spend on the garage doors and openers is well worth the look, feel, strength and quietness we get out of this system"));

        //GROUP 3
        elements.add(new RoomElement(3, 0, "Custom pantries", "A basic design principal of all our designs try's to fit in a variety of closet spaces. Walk in closets, built in pantries, linen closets, coats closets, broom closets, all become an important part of the design process. Every closet is custom designed to suit the client's needs prior to any construction starting. Every clients needs are different, from tall hanging spaces to three quarter to short double hanging, to drawers to shelves or shoe racks to accessory baskets. Broom closets are designed with shelving for cleaning supplies or even a backer for vacuum hoses or brooms and mops. Linen spaces are important to have deeper adjustable shelves. Walk in pantries are getting quite popular next to kitchens with customized spaces for appliances, trays, cereal boxes, storage boxes, wine racks, or small work spaces. Closet doors are fitted with auto lights that come on as you open the door, so you are not fumbling for switches with your hands full. Butlers pantries are also added to keep that messy serving area out of sight."));

        //GROUP 4
        elements.add(new RoomElement(4, 0, "Central vac kick plates", "Central vacuum pipes are roughed in to every home, just in case you decide to get a CV unit in the future. Kick plates are also roughed in and installed, usually in the cabinet kick or in a wall baseboard location.  More and more people are now not going with central vacs due to the wood floors throughout. But just in case you change your mind, the pipes are already behind the walls."));
        elements.add(new RoomElement(4, 1, "Under counter lighting", "All under counter lighting is roughed in for the clients to accessorize with what kind of lighting they prefer, weather it is a RAB low voltage or strip or neon to LED. There is also options of installing puck lights which can also be installed in glass feature cabinets. Or throw in some flood lighting which can be installed over the crown moldings in the freeze sections of the cabinets. "));
        elements.add(new RoomElement(4, 2, "Kitchen and bathrooms", "Kitchen and bathroom designs are drawn up by the designers in 3 - D so that you get a full understanding of what you are getting. Heavy duty construction with self closing hardware on all drawers and doors are included. Many accessory features are offered to accommodate any work habit you have in the kitchen. Take a visit to the luxurious kitchen and bath showroom to choose your style whether it be modern, classic, contemporary or in between. Light valances to hide the under counter lighting are created to blend into the cabinetry. Smooth crown moldings are added to tie into the same design elements of the home. Creative back splashes are added to splash some colour into the design."));
        elements.add(new RoomElement(4, 3, "Hardwood flooring", "All our homes have solid hardwood throughout main floor. There are also a lot of high end engineered floors today that allow you a lot more styles, sizes, and wood species to choose from. The engineered woods today are a lot stronger than the solid woods as there is a few good layers of ply materials as a sub base. This also allows the planks to be quite wide without worrying about cupping and shrinking."));

        //GROUP 5
        elements.add(new RoomElement(5, 0, "Built in Mantles", "All our fireplaces are custom designed and signed off by the client prior to any work being done. Every mantle, and surround is different. There are built ins around the fireplaces for book shelves, Audio / video equipments, decorative shelves, TV space, feature windows, all to give every unit its own personality. Some fireplaces might have rustic wood beams, or stone surrounds, or clean modern steel look. The fireplace mantle feature should flow into the home seamlessly without conflicting with other design features around it, but should still make a statement on its own."));
        elements.add(new RoomElement(5, 1, "Crown moldings", "Trim details are an important part of any home. We have many different profiles, shapes and sizes of trim from pine to poplar. With 10' high walls, you need a large 7 1/2 inch baseboard throughout. Our casings also match the baseboard profiles to marry the same style woodwork throughout. Sometimes we have contemporary casing or a step casing or a colonial casing with or without a back band attached. Depending on the room flavour, we may add sills under windows or even headers to accent certain areas, or panelling to picture framing. All of our homes are finished with large poplar 7\" crown moldings on every ceiling where it is flat. Even the closets and the toilet room get beautiful painted crown moldings.  We carry the same finishes of baseboards, casings, crowns right into the basements when they are finished.  But we scale down the size a bit from 7 inch to 5 inch crowns and trim. Custom profiles are carefully chosen to add elegant lines to all our built-ins and custom wood working."));
        elements.add(new RoomElement(5, 2, "Thermapan Wall System", "We are truly proud of our building envelope. You could have the best of mechanical systems, but if your envelope is sub standard, then you are wasting all of your energy out the walls. We first perfected the exterior shell with our Thermapan wall system. These walls clock out at R28 insulation value when measured on an infrared scanner compared to a R-15 wall system with your typical two by six walls.  Our ceilings have a full R-60 blown in the attics, and the joist headers are doubled up to R-28 to finish off a great envelope. Thermapan exterior walls have no studs in them what so ever to reduce any heat loss that happens in a typical two by six wall every 16 inches. The upfront cost of this wall gives you a payback in less than four years of occupancy.  This wall panel system is 10 times stronger than the typical system and has no moisture content in it. This, along with our steel stud interior walls, make sure that we don't have to deal with any screw pops in our drywall. We won't get any bowing or twisting of studs and these walls are perfectly straight from the lack of solid wood. "));

        //GROUP 6
        elements.add(new RoomElement(6, 0, "Custom Showers", "Large walk in showers are now a popular feature in our homes. Every shower is designed unique to its occupant. The flooring designer will co ordinate the wall tiles along with the flooring and wall colours and cabinetry. Features such as lestellos, crowns, or trims and borders are introduced to give that design flare you are looking for. A seat is added at an appropriate location close to the shower spray and wand. Niches are added to make for practical soaps and shampoo storage spaces. Low slope sills are a common element for a smooth transition to the bath floor. We can also drop the floor structure and create a no sill shower if needed. Under all the shower tiles, we install a Shluter System, which is a German made system guaranteed to never leak. It is a complete wrap of the plastic layer adhered to the water board and connects to its own drain system which ensures full drainage at all times."));
        elements.add(new RoomElement(6, 1, "Tubs", "We use Kohler or Azura as our line of preferred tubs, as they are acrylic tubs and not fiber glass, where the colour in the skin goes a lot deeper. They also have a large assortment of styles and shapes to choose from. If it is not a free standing tub, we customize the tile decking or surrounding area. Our designers will use lestelos, trim and moldings to dress up the tub deck."));
        elements.add(new RoomElement(6, 2, "In-floor heating", "Every ensuite is equipped with an in floor heater, which is the best heat source you could have coming from your feet up. This is a very efficient way of heating a bathroom floor which costs pennies a day to operate. You can set the timer to coming on to about 80 degrees in the morning and it can shut down when you don't use it. The heat cables bake the cement and the tiles and creates a great even warm floor at your feet."));
        elements.add(new RoomElement(6, 3, "Custom Closets", "A basic design principal of all our designs try's to fit in a variety of closet spaces. Walk in closets, built in pantries, linen closets, coats closets, broom closets, all become an important part of the design process. Every closet is custom designed to suit the client's needs prior to any construction starting. Every clients needs are different, from tall hanging spaces to three quarter to short double hanging, to drawers to shelves or shoe racks to accessory baskets. Broom closets are designed with shelving for cleaning supplies or even a backer for vacuum hoses or brooms and mops. Linen spaces are important to have deeper adjustable shelves. Walk in pantries are getting quite popular next to kitchens with customized spaces for appliances, trays, cereal boxes, storage boxes, wine racks, or small work spaces. Closet doors are fitted with auto lights that come on as you open the door, so you are not fumbling for switches with your hands full. Butlers pantries are also added to keep that messy serving area out of sight."));

        //GROUP 7
        elements.add(new RoomElement(7, 0, "NEST wireless  thermostat", "Many of our clients are snow birds, so they're gone in the winter. These thermostats are programmable through wireless control. You can be anywhere in the world and be able to see and control all your HVAC needs in the home. This thermostat learns your living habits and can then take over the control of your HVAC system. It will make your system much more efficient. It also sends you monthly reports to inform you if you were green or not.  Either the Nest or the Honeywell are both interconnected to the Control 4 system which also controls many other items in the home such as security, audio, video, lighting, cameras,  kitchen appliances, and much more. You could be in Florida and be able to pull up your app and take a look at any of these items from the convenience of your tablet or phone."));
        elements.add(new RoomElement(7, 1, "Pre-mechanical walk through", "Every client does a full mechanical walk through with our mechanical partners prior to any mechanical work starting. This way you are sure to have discussed in advance about all your furniture layouts, wall paintings locations, traffic flows, visual impact of items like thermostats, cold air returns, hot air vents, central vacuum out lets, switches and even floor plugs around your furniture layouts. Right down to where the towel bars will go to wall hung mirrors, we prepare for all things behind the wall before drywall. Backers are put in walls anywhere a heavy picture is predicted in the future. All accessories are installed over a solid backer which is placed in the walls prior to drywall. This mechanical walk through gives everyone a chance to prepare the walls for a true clean organized finish, before any drywall goes up on the walls."));
        elements.add(new RoomElement(7, 2, "Trim details", "Trim details are an important part of any home. We have many different profiles, shapes and sizes of trim from pine to poplar. With 10' high walls, you need a large 7 1/2 inch baseboard throughout. Our casings also match the baseboard profiles to marry the same style woodwork throughout. Sometimes we have contemporary casing or a step casing or a colonial casing with or without a back band attached. Depending on the room flavour, we may add sills under windows or even headers to accent certain areas, or panelling to picture framing. All of our homes are finished with large poplar 7\" crown moldings on every ceiling where it is flat. Even the closets and the toilet room get beautiful painted crown moldings.  We carry the same finishes of baseboards, casings, crowns right into the basements when they are finished.  But we scale down the size a bit from 7 inch to 5 inch crowns and trim. Custom profiles are carefully chosen to add elegant lines to all our built-ins and custom wood working."));

        //GROUP 8
        elements.add(new RoomElement(8, 0, "Thermapan Wall System", "We are truly proud of our building envelope. You could have the best of mechanical systems, but if your envelope is sub standard, then you are wasting all of your energy out the walls. We first perfected the exterior shell with our Thermapan wall system. These walls clock out at R28 insulation value when measured on an infrared scanner compared to a R-15 wall system with your typical two by six walls.  Our ceilings have a full R-60 blown in the attics, and the joist headers are doubled up to R-28 to finish off a great envelope. Thermapan exterior walls have no studs in them what so ever to reduce any heat loss that happens in a typical two by six wall every 16 inches. The upfront cost of this wall gives you a payback in less than four years of occupancy.  This wall panel system is 10 times stronger than the typical system and has no moisture content in it. This, along with our steel stud interior walls, make sure that we don't have to deal with any screw pops in our drywall. We won't get any bowing or twisting of studs and these walls are perfectly straight from the lack of solid wood. "));
        elements.add(new RoomElement(8, 1, "Interactive 3D home designs", "Every home, whether it is small or large, starts with a long conversation on taste, style, lot scenery, sun direction, exterior features, furniture sizes, and many more things. We learn about your life now, tomorrow and long after, as you go through the different stages in life. As 99% of our clients are retiring , we understand the needs now and tomorrow. With almost 30 years in business, we have grown with our target market and learned to understand what they demand. We build an entire 3-D model of your home on the lot with our 3-D software. Our designers will get pictures, colours, sizes of your furniture and also build your furniture and put it in the home to make sure we have proper flows. We also do sun studies on the lot to make sure we reduce any shadowing. Features such as large covered front and rear porches, large glass areas to bring the outside in, Screened in porches to enjoy those sunsets in the evenings without worrying about bugs. High ceilings to open up the spaces with an airy feel to it, are just some of the standard features we try and include in every design. Downsizing does not have to feel smaller sized. We look at different ceiling features, such as coffered ceilings, or vaulted or round or high , all are explored to get the best open airy effect possible. We get the clients to have a hands on approach to the design portion, and this way they totally understand what they are getting and we understand what is expected of us. We work very close with ACK Architects to ensure that all elements of design are dealt with properly up front. Exterior features are added to make a spectacular streetscape, no matter where the home is built. Clients from out of town get a live fly through of the 3-D design via our on line system so revisions are made easy and convenient. At the same time we are also keeping budget current and in line."));
        elements.add(new RoomElement(8, 2, "USB charging station", "As phones and tablets become a common place in the home, we install USB charging outlets in convenient locations. Den's, night table at bed side, or desk in the kitchen design, or near a drawer. You tell us and we install it there."));
        elements.add(new RoomElement(8, 3, "Client On line account", "Every client gets their own online account, where everything about their home is located. Once we have discussed some perimeters of pricing and designs, and the client feels that we can move forward in exploring a possible build, we can set you up with your own online account. All communications goes through that system under their own section. All the scheduling, selections, moneys, trades are all logged in to it. The system keeps track of any changes and conveys messages at the correct time to the trades or partners. Every job get their own project manager who then takes care of all items from start to finish, so you are dealing with one person. You also get your own site super who manages the job site with your communications through the project. With all the different hands that touch the build, this system keeps the project as a whole right back to the designer who makes sure that the intent and the flavour of the design is maintained. Clients can be anywhere in the world, and still be involved with the projects, watching the progress of every stage and still be able to keep making selections along the way."));
        elements.add(new RoomElement(8, 4, "Extended Warranty", "Our homes comes with a typical Tarion warranty with excellent rating ever since 1989. But we go well beyond a standard Tarion Warranty. We have served and helped clients that go back over twenty years as we are not here to walk away from any of our Gatta clients. We stand behind you to be of assistance for as long as we are here. Our online system logs any deficiencies you might have and are addressed by our full time service personal. "));
        elements.add(new RoomElement(8, 5, "Kohler Fixtures", "All of our plumbing fixtures are high end Kohler line up with many styles and shapes to choose from. They are much more efficient and comes with great service from Kohler. There is also a great line up of elongated bowl and comfort height toilets with self close lids and elegant designs. Your choice of free standing tubs or built in deck mounted tubs come in a variety of shapes and sizes."));

        //GROUP 9
        elements.add(new RoomElement(9, 0, "Interior heights", "As most of our clientele is getting ready to retire or is retiring, they are downsizing from large 3, 4, 5 thousand square foot homes. But we don't want them to feel down sized. So, we have gone to 10 foot exterior main floor heights and the ceilings can explode up to large open spaces. You want that WOW feeling when you first enter into a home. Along with higher ceilings, we can add transom windows over doors, large glass areas to bring the exterior in to the interior and to transfer light from bright areas to darker areas of the home. Doors can go to 8 foot heights instead of the typical 80 inch doors. We add dormers, high window features or glass areas to always make sure you have a bright open aired feel to every home. Ceiling heights can vary from 10 feet to all the way up to 20 feet tall. Other ceiling features like coffered ceilings in master bedrooms are common to give a cosier, elegant look. Barrel ceilings, arched ceilings, cathedral ceilings are just a few of the many options available to our clients."));
        elements.add(new RoomElement(9, 1, "Interior designer team", "We have our full team of designers that work with you in every stage of the build. Right from the kitchen designers, flooring designers, paint designers, to window coverings. Each designer works well with each other making sure that the final product is a stunning one which boasts individuality and the end result is well flowing and exactly the feel that the client wanted. Each designers job is to understand your taste and look that you are trying to achieve and guide you down that path."));
        elements.add(new RoomElement(9, 2, "Paints", "Our painters only use high quality Benjamin Moore paints for all the walls and trim. Every wall and trim is sanded before every coat applied. Walls receive a coat of high covering primer and two coats of washable durable VOC free paint. Trims and doors are painted with a high strength paint to give it a furniture finish look. "));
        elements.add(new RoomElement(9, 3, "Interior door styles ", "Premdoor makes many different door styles to choose from. Baseboards, casings, crown moldings, trim profiles for built ins are all carefully selected by the clients with assistance from our staff at all times. Along with the elegant doors, we have a large selection of lever style handles and hardware from Schlage which comes in many finishes, and styles. These handles are also matched to the hinges  throughout the home. "));
        elements.add(new RoomElement(9, 4, "Large oversize glass doors and windows", "These windows are solid vinyl with no wood in them to warp, twist or decay. There are many bold colours to choose from that are available as a standard choice. We add SDL grills which are larger with additional internal spacers, not the typical internal grills. This actually simulates the true divided glass without compromising efficiency. Our glass is made of Low E glass with argon gas in between to create a better thermal result between the two seasons. With the exterior walls starting at 10' wall height, all our doors can now be 8' tall and they come with multi point hardware, which grabs the whole door for locking straightness and better security. Even our patio doors are 8 foot tall sliders, if you don't want French doors. There is also the option of double sliding doors to have the large openings. Transoms are often added to extend that opening taller to the outdoors. There are many window styles to choose from, but we try to fit the window style, size and shape to the scenery it sees through. For a home on the lake, we would go with wall to wall glass horizontally to capture that linear view. For those homes that back onto tall tree lots, we would go with tall, high windows to bring the greenery inside. We also try and slip in feature windows like ovals, circles, trapezoids, and many more shapes you can envision."));

        //GROUP 10
        elements.add(new RoomElement(10, 0, "Front Doors", "The front door of any home should make an individual statement from the street. Our front doors are either solid wood or insulated fibreglass stainable doors. With our solid doors, the carpenters will build any style we draw up to suit the design choosing form Spanish cedar, to mahogany to American oak. With sun facing fronts, we go with the stainable or paintable fiber glass doors. This allows for a less maintenance system, while still achieving that great glow for a front door. All the 8 foot doors come with multi point hardware with elegant styles of levers to choose form."));
        elements.add(new RoomElement(10, 1, "Metal Roofs", "Metal roofs are getting quite common, not only for porches and dormers, but also for entire homes. The choice of colours and styles has grown, to give a lot of options these days. Metal roofs give the front facade a bang when used with details and bump outs. They come in 16\" wide rib patterns or even 20\" rib patterns. When the design calls for curved or copper roofs, we have our coppersmiths custom make any shape or profile we need."));

        //GROUP 11
        elements.add(new RoomElement(11, 0, "Stairs & Railings", "Elegant stairs are made open or closed depending on the design of the main floor. As many basements are getting finished, we are matching the stairs to the hard wood floors beside it. There is a choice of many different spindles, rail types, post styles and shapes to choose from. Bottom of stairs are flared with balloon treads and curves to open up that bright feel to finished basements. Our painters magically match the stairs to the hard wood floors chosen."));
        elements.add(new RoomElement(11, 1, "Occupancy", "Prior to occupancy, our homes go through a rigorous check list system by 5 different inspections. Every detail is inspected for finish, quality, workmanship, completion and accuracy. The entire home is cleaned professionally and the entire duct system is drilled and cleaned prior to client move in. A final walk through with the clients ensures that the client understands how our building system operates and how it is to be maintained. We would like to ensure that you enjoy and take advantage of all the systems we have so thoroughly created for your home. "));
        elements.add(new RoomElement(11, 2, "Insulation package", "Our Thermapan exterior walls are basically a blanket of Polystyrene foam with a true R value of R-28. There are no studs in our outside walls. These panels come in 6 inches of foam sandwiched between two sheets of OSB sheathing which gives it the amazing structural strength. Studies showed no heat loss happening on our Panel walls compared to red streaks of heat loss showing every 16 inches on the typical 2 by 6 walls. Additional to this superior wall system, we add extra thick layer of blown insulation in the attic bringing it up to a R-60 value. The floor joist headers are stuffed with thick R - 28 batts connecting it to the R-20 insulation in the exterior walls of the basement walls. These walls in turn connect to the R-10 Ayrfoil wrap which is blanketed under the entire basement floor. Now our envelope is complete."));
        elements.add(new RoomElement(11, 3, "Basement windows", "With the taller basement walls, we have gone to the larger 4 foot by 3 foot basement windows. This calls for deeper metal window wells with brighter and safer egress from all basement areas. Or we can build a tiered wood window well if there is sufficient room to allow for them.  All basement windows have sub drains tied in to the main drainage system to make sure the water or ice drains away from any basement window under grade."));
        elements.add(new RoomElement(11, 4, "Foundations", "As our clients down size from large homes into the next phase of their lives, they still need space to house kids, grand kids, visitors for those stays in Niagara On The Lake. We have modified our basement designs to make them more spacious and bright. Our basement walls start with an 8 foot 4 inch wall as a standard. The basement finishes are the same high quality finishes as the main floor. Large 4 foot by 3 foot windows with deep window wells give the basement a truly bright open feel. The entire foundation wall is wrapped with a plastic drainage layer keeping any possible water away from the wall in case of any cracks.  We also wrap the basement concrete slab with a blanket of Ayrfoil throughout the slab. This gives a R value of R - 10 and also keeps the floor warm and dry. This layer of Ayrfoil also reduces the chance of potential Radon gases from migrating in to the house. Wine cellars are added under one of the porches which give true natural temperatures for your cold storage. Flared stairs at the bottom few treads give the illusion that the basement is truly an extension of the main floor. A typical basement finish usually consists of two bed rooms, a rec room , a bath room and a possible hobby room or a theater room. To tie this all in, we can always add an elevator rough in for future use."));
        elements.add(new RoomElement(11, 5, "A/ V & Automation", "All our homes are roughed in with A/V wires in the great room areas for surround sound as a basic. You can spread the sound system throughout the home at pre drywall stage. Our A/V consultants go through your wish list and come up with the ideal equipment and system for your home and needs. Today's automation includes individual control of entry to personal lighting plan to personal music to temperature to kitchen appliances. Each user can control their own path to follow when they come home all starting with the key pad touch at entry."));

        //GROUP 12
        elements.add(new RoomElement(12, 0, "Tank-less water heater & air handler", "We have got rid of the 70 gallon hot water tanks and replaced it with a tank-less water heater. Why heat 70 gallons of water 24 hours a day? With the tank-less system, your heater wakes up when you call for hot water and will never run out of hot water.  This on demand unit is much more efficient and takes up much less space.  Along with tank-less system, we have also got rid of the furnace, and replaced it with an air handler. There is no more burner or an exchanger in this unit. When you call for heat, your tank-less unit wakes up and flows hot water to the air handler as the heat source. The fan in the air handler blows through the hot water lines pushing hot air through the duct work of the home. This allows us to not need an extra flame, a heat exchanger or a vent fan adding to more energy savings mechanical system. Another big advantage of this system is that in a conventional furnace, when the flame goes out, the heat stops. But in this system, when the on demand stops firing, the hot water stays hot longer, giving us heat much longer without any energy being used. And with less components to this system, there is less chance of breakdown in the future."));
        elements.add(new RoomElement(12, 1, "Ayr Foil system under entire basement slab ", "We have wrapped the concrete slab with a Ayrfoil layer which gives the floor a moisture free warm feel. This layer gives you a R- 10 insulating value under your basement floor. This layer of Ayrfoil also seals the basement slab to reduce the chances of potential radon gases that might be in the soils below your basement. We then cut every basement concrete slab to tell the concrete where to crack. Every concrete floor cracks, because they are only floating slabs. But when you cut the floor in strategic areas and shapes, you control where the cracks happen and you don't have a basement with ugly cracks all over the floor. When the concrete is totally dried and cured, we wash and seal the concrete floor with a concrete sealer and this will keep the concrete dust to a minimum in the home."));
        elements.add(new RoomElement(12, 2, "Elevator rough", "Any time we build two storey homes, we always try and incorporate an elevator rough in. This way we can extend the life in the home for the client an extra 10 to 20 years. We pour the elevator pit at time of our concrete slab and install a drain with it. We also frame removable floor systems where the elevator chase will go, so when it is time, we just unscrew the floor area and in goes the elevator. Even the doors are placed in the correct locations on every floor. At design stage we ensure that these chases line up over each other and are smoothly incorporated a s closets for the current time. Elevators these days are quite in expensive and very common in many condo developments that we have done as a standard."));
        elements.add(new RoomElement(12, 3, "Engineered Floor System", "We have bumped up our floor sheathing from 5/8 inch to 3/4 inch tongue and groove, which is glued and nailed over larger engineered floor joist systems. We then come back after the house has settled and screw the sheathing to the joists every 8\" apart. This will reduce the chances of squeaks drastically and also gives it a much stronger and less bouncy floor system. You will notice that our joists are taller and thus span a lot more giving the basements a bigger beam free ceiling. Our steel beams are also bumped up to go taller and thicker to allow for longer spans and less deflection. Basement rec rooms and bedrooms are kept in mind when designing our structural system to make sure that you have no beams or bulk heads in your main entertaining rooms."));
        elements.add(new RoomElement(12, 4, "Heat energy recovery circulation unit", "This heat recovery system supplies continuous fresh air from outside in the house. The heat recovery core of the unit transfers a portion of heat in the stale air being exhausted to the fresh incoming air from outside before being distributed throughout the house.  \n" +
                "The energy recovery ventilator system fits into our colder climates, in the homes where there is no excess moisture in heating season, as well as for homes located in warmer climates where the outside humidity level is high.\n" +
                "The ERV system recovers heat, but it also recuperates the energy trapped in moisture, which greatly improves the overall recovery efficiency. Our ERV's will control the humidity's much better in the winter and the summer. \n" +
                "In air conditioned homes, when it is more humid outside than inside, the ERV system limits the amount of moisture coming into your home. In humidified homes, when the humidity level is low in winter, the ERV system limits the amount of moisture expelled from your home. The result is a continuous supply of fresh air, without unpleasant drafts, and increased comfort for the home occupants."));

        //GROUP 13
        elements.add(new RoomElement(13, 0, "Drain water recovery system", "This Drain water heat recovery system is a copper pipe with coils of water wrapped around it. This simple pipe recovers the heat from the waste water before it goes down the main plumbing drain. Every time you take a shower, or wash clothes or do dishes, all the heat from the used hot water is wasted by just going down the drain. But this copper pipe captures the heat and transfers the heat to the new water going to the on demand unit. This new water getting to the on demand heater is now about 20 degrees warmer and so will use less energy heating it up to hot temperature."));
        elements.add(new RoomElement(13, 1, "Water leak detection System", "As most of our clients are not here in the winter time, we have added a new feature on the water line coming into the house. This leak detection system monitors your normal water usage in real time and creates a history of water consumption for you to view from anywhere in the world. It will help you set consumption targets and limits to aid with reducing water consumption. It is fully programmable to suit each property owner. There is also an AWAY mode, where if it senses irregular water activity, it will shut the water main off and send you a message to check things out. It even comes with optional pucks which can be placed in various locations throughout the basement, where if it detects water at any of the pucks, you are notified instantly, no matter where you are in the world."));
        elements.add(new RoomElement(13, 2, "Electrical", "Every home comes with 200 AMP breaker panels system with ample extra circuits for the future. Our electricians make sure that you have many outlet locations throughout the home in every room. Every client does a full mechanical walk through with our mechanical partners prior to any mechanical construction. This way you are sure to have discussed in advance about all your furniture layouts, wall paintings locations, traffic flows, visual impact of items like thermostats, cold air returns, hot air vents, central vacuum outlets, switches and even floor plugs around your furniture layouts. Right down to where your bed will go, to what size night tables, to where your night lamps will be and where you will control them and the bedroom light switch from is all discussed up front. Things like plugs for Christmas lights with a switch in the foyer, to wiring for TV locations and audio video locations in your bedroom, den, TV room, kitchen, are all thought of. "));
        elements.add(new RoomElement(13, 3, "Triple safe sump pump system", "With almost every home needing sump pumps, and with all these crazy heavy rains that we have been having the last few years, we have switched our standard sump pump to a top of the line Triple safe pump system. PUMP 1 is a heavy-duty, 1/3 horse power Zoeller pump that easily handles a pumping volume  of up to 2600 gallons. per hour.\n" +
                "PUMP 2 is even more powerful with a 1/2 horsepower Zoeler pump. It comes on automatically if PUMP 1 fails or if high water flow calls for greater pumping volume up to 6,200 gallons per hour.\n" +
                "PUMP 3 is a battery-powered pump that will run automatically if there's a power outage over 11,000 gallons on a fully charged battery. There is also a built in alarm if either of these fails, that notifies you of pump failure."));
        elements.add(new RoomElement(13, 4, "Ipex Water Lines ", "All of our water supply lines are direct dedicated lines from the distribution system all the way to the fixtures, allowing you to receive full pressure at all times. We have individual shutoffs at the manabloc as well as at the fixture. There are no T's or elbows in this system. This is the same plastic lines that are used in concrete driveways to heat them in the winter, so they are very durable. Clean and corrosion free."));

    }
}