package xklaim.coordination;

import coordination.JointTrajectory;
import klava.Locality;
import klava.Tuple;
import klava.topology.KlavaProcess;
import org.eclipse.xtext.xbase.lib.Exceptions;
import ros.Publisher;
import ros.RosBridge;

@SuppressWarnings("all")
public class GoToInitialPosition extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  private Locality robot2;
  
  public GoToInitialPosition(final String rosbridgeWebsocketURI, final Locality robot2) {
    super("xklaim.coordination.GoToInitialPosition");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
    this.robot2 = robot2;
  }
  
  @Override
  public void executeProcess() {
    try {
      final Locality myself = this.self;
      final RosBridge bridge = new RosBridge();
      bridge.connect(this.rosbridgeWebsocketURI, true);
      in(new Tuple(new Object[] {"releaseCompleted"}), this.self);
      Thread.sleep(1000);
      final Publisher pub = new Publisher("/arm_controller/command", "trajectory_msgs/JointTrajectory", bridge);
      final JointTrajectory afterPose = new JointTrajectory().positions(
        new double[] { 0.000, 0.000, 0.000, 0.000, 0.000, 0.000 }).jointNames(
        new String[] { "joint1", "joint2", "joint3", "joint4", "joint5", "joint6" });
      pub.publish(afterPose);
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
