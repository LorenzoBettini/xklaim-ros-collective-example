package coordination;

import ros.msgs.std_msgs.Header;

public class PoseStamped {
    public Header header = new Header();
    public Pose pose = new Pose();

    public PoseStamped(){}
    public PoseStamped(Header header, Pose pose) {
        this.header = header;
        this.pose = pose;
    }

    public PoseStamped(Pose pose) {
        this.pose = pose;
    }
}
