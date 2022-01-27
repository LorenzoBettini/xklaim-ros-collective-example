package xklaim.coordination;

import coordination.JointTrajectory;
import klava.topology.KlavaProcess;
import ros.Publisher;
import ros.RosBridge;

@SuppressWarnings("all")
public class Before_pick extends KlavaProcess {
  public Before_pick() {
    super("xklaim.coordination.Before_pick");
  }
  
  @Override
  public void executeProcess() {
    final String rosbridgeWebsocketURI = "ws://0.0.0.0:9090";
    final RosBridge bridge = new RosBridge();
    bridge.connect(rosbridgeWebsocketURI, true);
    final Publisher pub = new Publisher("/arm_controller/command", "trajectory_msgs/JointTrajectory", bridge);
    final JointTrajectory beforePick = new JointTrajectory().positions(
      new double[] { (-3.14), (-0.2169), (-0.5822), 3.14, 1.66, (-0.01412) }).jointNames(
      new String[] { "joint1", "joint2", "joint3", "joint4", "joint5", "joint6" });
    pub.publish(beforePick);
  }
}
