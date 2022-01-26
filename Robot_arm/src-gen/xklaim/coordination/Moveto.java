package xklaim.coordination;

import coordination.PoseStamped;
import klava.Locality;
import klava.Tuple;
import klava.topology.KlavaProcess;
import ros.Publisher;
import ros.RosBridge;
import ros.msgs.geometry_msgs.Twist;

@SuppressWarnings("all")
public class Moveto extends KlavaProcess {
  private Locality arm;
  
  public Moveto(final Locality arm) {
    super("xklaim.coordination.Moveto");
    this.arm = arm;
  }
  
  @Override
  public void executeProcess() {
    final String rosbridgeWebsocketURI = "ws://0.0.0.0:9090";
    final RosBridge bridge = new RosBridge();
    bridge.connect(rosbridgeWebsocketURI, true);
    final Publisher pub = new Publisher("/robot1/move_base_simple/goal", "geometry_msgs/PoseStamped", bridge);
    String frame_id = null;
    Double x = null;
    Double y = null;
    Double w = null;
    Tuple _Tuple = new Tuple(new Object[] {"goto1", String.class, Double.class, Double.class, Double.class});
    in(_Tuple, this.self);
    frame_id = (String) _Tuple.getItem(1);
    x = (Double) _Tuple.getItem(2);
    y = (Double) _Tuple.getItem(3);
    w = (Double) _Tuple.getItem(4);
    final PoseStamped posesta = new PoseStamped();
    posesta.header.frame_id = frame_id;
    posesta.pose.position.x = (x).doubleValue();
    posesta.pose.position.y = (y).doubleValue();
    posesta.pose.orientation.w = (w).doubleValue();
    pub.publish(posesta);
    out(new Tuple(new Object[] {"arrived", "arrived"}), this.arm);
    final Publisher pubvel = new Publisher("/robot1/cmd_vel", "geometry_msgs/Twist", bridge);
    final Twist twistMsg = new Twist();
    twistMsg.linear.x = 0.0;
    twistMsg.angular.y = 0.0;
    pubvel.publish(twistMsg);
    out(new Tuple(new Object[] {"ready", "ready"}), this.arm);
  }
}
