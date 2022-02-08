package xklaim.coordination;

import coordination.JointTrajectory;
import java.util.Collections;
import java.util.List;
import klava.Tuple;
import klava.topology.KlavaProcess;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import ros.Publisher;
import ros.RosBridge;

@SuppressWarnings("all")
public class Rotate extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  public Rotate(final String rosbridgeWebsocketURI) {
    super("xklaim.coordination.Rotate");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
  }
  
  @Override
  public void executeProcess() {
    final RosBridge bridge = new RosBridge();
    bridge.connect(this.rosbridgeWebsocketURI, true);
    final Publisher pub = new Publisher("/arm_controller/command", "trajectory_msgs/JointTrajectory", bridge);
    final List<Double> jointPositions = Collections.<Double>unmodifiableList(CollectionLiterals.<Double>newArrayList(Double.valueOf((-0.9546)), Double.valueOf((-0.20)), Double.valueOf((-0.7241)), Double.valueOf(3.1400), Double.valueOf(1.6613), Double.valueOf((-0.0142))));
    final JointTrajectory rotateTrajectory = new JointTrajectory().positions(((double[])Conversions.unwrapArray(jointPositions, double.class))).jointNames(
      new String[] { "joint1", "joint2", "joint3", "joint4", "joint5", "joint6" });
    in(new Tuple(new Object[] {"getUpCompleted"}), this.self);
    pub.publish(rotateTrajectory);
  }
}
