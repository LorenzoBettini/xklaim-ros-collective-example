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
  private String rosbridgeWebsocketURI;
  
  private Locality arm;
  
  public Moveto(final String rosbridgeWebsocketURI, final Locality arm) {
    super("xklaim.coordination.Moveto");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
    this.arm = arm;
  }
  
  @Override
  public void executeProcess() {
    final RosBridge bridge = new RosBridge();
    bridge.connect(this.rosbridgeWebsocketURI, true);
    final Publisher pub = new Publisher("/robot1/move_base_simple/goal", "geometry_msgs/PoseStamped", bridge);
    Double x = null;
    Double y = null;
    Double w = null;
    Tuple _Tuple = new Tuple(new Object[] {"comeHere", Double.class, Double.class, Double.class});
    in(_Tuple, this.self);
    x = (Double) _Tuple.getItem(1);
    y = (Double) _Tuple.getItem(2);
    w = (Double) _Tuple.getItem(3);
    final PoseStamped posesta = new PoseStamped().headerFrameId("world").posePositionXY((x).doubleValue(), (y).doubleValue()).poseOrientation((w).doubleValue());
    pub.publish(posesta);
    out(new Tuple(new Object[] {"arrived", "arrived"}), this.arm);
    final Publisher pubvel = new Publisher("/robot1/cmd_vel", "geometry_msgs/Twist", bridge);
    final Twist twistMsg = new Twist();
    pubvel.publish(twistMsg);
    out(new Tuple(new Object[] {"ready", "ready"}), this.arm);
  }
}
