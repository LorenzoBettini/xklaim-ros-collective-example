package xklaim.coordination;

import coordination.JointTrajectory;
import klava.Locality;
import klava.Tuple;
import klava.topology.KlavaProcess;
import org.eclipse.xtext.xbase.lib.InputOutput;
import ros.Publisher;
import ros.RosBridge;

@SuppressWarnings("all")
public class Lay extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  private Locality robot2;
  
  public Lay(final String rosbridgeWebsocketURI, final Locality robot2) {
    super("xklaim.coordination.Lay");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
    this.robot2 = robot2;
  }
  
  @Override
  public void executeProcess() {
    final Locality myself = this.self;
    final RosBridge bridge = new RosBridge();
    bridge.connect(this.rosbridgeWebsocketURI, true);
    final Publisher pub = new Publisher("/arm_controller/command", "trajectory_msgs/JointTrajectory", bridge);
    String arrived = null;
    Tuple _Tuple = new Tuple(new Object[] {"arrived", String.class});
    in(_Tuple, myself);
    arrived = (String) _Tuple.getItem(1);
    InputOutput.<String>println(String.format("I am: %s,", arrived));
    final JointTrajectory posefinal = new JointTrajectory().positions(
      new double[] { (-0.9546), (-0.0097), (-0.9513), 3.1400, 1.7749, (-0.0142) }).jointNames(
      new String[] { "joint1", "joint2", "joint3", "joint4", "joint5", "joint6" });
    pub.publish(posefinal);
    InputOutput.<String>println(String.format("Iam posing"));
  }
}
