package www.jeranderic.gattahomes;

/**
 * Created by ericsmacbook on 2016-05-14.
 */
public class RoomElement {

    public int id, groupID;
    public String title, description, videoURL, imageURL;

    public RoomElement() {}

    /*
    * This constructor is for Info windows
    * */
    public RoomElement(int groupID, int id, String title, String description) {
        this.id = id;
        this.groupID = groupID;
        this.title = title;
        this.description = description;
    }

    /*
    * This constructor is for Info windows
    * */
    public RoomElement(int groupID, int id, String imageURL) {
        this.id = id;
        this.groupID = groupID;
        this.imageURL = imageURL;
    }
}
