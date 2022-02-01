package xklaim.coordination;

import coordination.JointTrajectory;
import klava.topology.KlavaProcess;
import ros.Publisher;
import ros.RosBridge;

@SuppressWarnings("all")
public class Before_pick extends KlavaProcess {
  private RosBridge bridge;
  
  public Before_pick(final RosBridge bridge) {
    super("xklaim.coordination.Before_pick");
    this.bridge = bridge;
  }
  
  @Override
  public void executeProcess() {
    final Publisher pub = new Publisher("/arm_controller/command", "trajectory_msgs/JointTrajectory", this.bridge);
    final JointTrajectory beforePick = new JointTrajectory().positions(
      new double[] { (-3.14), (-0.2169), (-0.5822), 3.14, 1.66, (-0.01412) }).jointNames(
      new String[] { "joint1", "joint2", "joint3", "joint4", "joint5", "joint6" });
    pub.publish(beforePick);
  }
}
