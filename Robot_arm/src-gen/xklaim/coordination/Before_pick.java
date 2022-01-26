package xklaim.coordination;

import coordination.JointTrajectory;
import coordination.JointTrajectoryPoint;
import java.util.Arrays;
import java.util.List;
import klava.topology.KlavaProcess;
import org.eclipse.xtext.xbase.lib.Conversions;
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
    final JointTrajectoryPoint jointTrajectoryPoints = new JointTrajectoryPoint();
    jointTrajectoryPoints.positions = ((double[])Conversions.unwrapArray(Arrays.<Double>asList(Double.valueOf((-3.14)), Double.valueOf((-0.2169)), Double.valueOf((-0.5822)), Double.valueOf(3.14), Double.valueOf(1.66), Double.valueOf((-0.01412))), double.class));
    jointTrajectoryPoints.time_from_start.nsecs = 0;
    jointTrajectoryPoints.time_from_start.secs = 120;
    final JointTrajectory before_pick = new JointTrajectory();
    List<JointTrajectoryPoint> list = Arrays.<JointTrajectoryPoint>asList(jointTrajectoryPoints);
    final List<JointTrajectoryPoint> _converted_list = (List<JointTrajectoryPoint>)list;
    before_pick.points = ((JointTrajectoryPoint[])Conversions.unwrapArray(_converted_list, JointTrajectoryPoint.class));
    before_pick.joint_names = ((String[])Conversions.unwrapArray(Arrays.<String>asList("joint1", "joint2", "joint3", "joint4", "joint5", "joint6"), String.class));
    before_pick.header.stamp.secs = 0;
    before_pick.header.stamp.nsecs = 0;
    before_pick.header.frame_id = "";
    pub.publish(before_pick);
  }
}
