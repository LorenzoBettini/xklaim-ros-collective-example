package xklaim.coordination;

import coordination.JointTrajectory;
import klava.Tuple;
import klava.topology.KlavaProcess;
import ros.Publisher;
import ros.RosBridge;

@SuppressWarnings("all")
public class Grip extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  public Grip(final String rosbridgeWebsocketURI) {
    super("xklaim.coordination.Grip");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
  }
  
  @Override
  public void executeProcess() {
    final RosBridge bridge = new RosBridge();
    bridge.connect(this.rosbridgeWebsocketURI, true);
    final Publisher pub = new Publisher("/gripper_controller/command", "trajectory_msgs/JointTrajectory", bridge);
    final JointTrajectory grasp = new JointTrajectory().positions(
      new double[] { 0.019927757424255833, (-0.010904802339570573) }).jointNames(
      new String[] { "f_joint1", "f_joint2" });
    in(new Tuple(new Object[] {"getDownMovementsCompleted"}), this.self);
    pub.publish(grasp);
  }
}
