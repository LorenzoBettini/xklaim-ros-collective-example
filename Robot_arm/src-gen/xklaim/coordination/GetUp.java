package xklaim.coordination;

import coordination.JointTrajectory;
import klava.Tuple;
import klava.topology.KlavaProcess;
import ros.Publisher;
import ros.RosBridge;

@SuppressWarnings("all")
public class GetUp extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  public GetUp(final String rosbridgeWebsocketURI) {
    super("xklaim.coordination.GetUp");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
  }
  
  @Override
  public void executeProcess() {
    final RosBridge bridge = new RosBridge();
    bridge.connect(this.rosbridgeWebsocketURI, true);
    final Publisher pub = new Publisher("/arm_controller/command", "trajectory_msgs/JointTrajectory", bridge);
    in(new Tuple(new Object[] {"gripCompleted"}), this.self);
    final JointTrajectory afterPick = new JointTrajectory().positions(
      new double[] { (-3.1415), (-0.2862), (-0.5000), 3.1400, 1.6613, (-0.0142) }).jointNames(
      new String[] { "joint1", "joint2", "joint3", "joint4", "joint5", "joint6" });
    pub.publish(afterPick);
  }
}
