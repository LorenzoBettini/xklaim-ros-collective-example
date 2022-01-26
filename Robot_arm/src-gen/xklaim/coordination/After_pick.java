package xklaim.coordination;

import com.fasterxml.jackson.databind.JsonNode;
import coordination.JointTrajectory;
import coordination.JointTrajectoryPoint;
import java.util.Arrays;
import java.util.List;
import klava.topology.KlavaProcess;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.InputOutput;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;

@SuppressWarnings("all")
public class After_pick extends KlavaProcess {
  public After_pick() {
    super("xklaim.coordination.After_pick");
  }
  
  @Override
  public void executeProcess() {
    final String rosbridgeWebsocketURI = "ws://0.0.0.0:9090";
    final RosBridge bridge = new RosBridge();
    bridge.connect(rosbridgeWebsocketURI, true);
    final Publisher pub = new Publisher("/arm_controller/command", "trajectory_msgs/JointTrajectory", bridge);
    final RosListenDelegate _function = (JsonNode data, String stringRep) -> {
      final JsonNode error = data.get("msg").get("actual").get("positions");
      final List<Double> desire = Arrays.<Double>asList(Double.valueOf(0.019927757424255833), Double.valueOf((-0.010904802339570573)));
      double sum = 0.0;
      for (int i = 0; (i <= 1); i = (i + 1)) {
        double _asDouble = error.get(i).asDouble();
        Double _get = desire.get(i);
        double _minus = (_asDouble - (_get).doubleValue());
        double _pow = Math.pow(_minus, 2.0);
        double _plus = (sum + _pow);
        sum = _plus;
      }
      final double norm = Math.sqrt(sum);
      double tol = 0.007;
      if ((norm <= tol)) {
        InputOutput.<String>println(String.format("I picked the object"));
        final JointTrajectoryPoint jointTrajectoryPoints = new JointTrajectoryPoint();
        jointTrajectoryPoints.positions = ((double[])Conversions.unwrapArray(Arrays.<Double>asList(Double.valueOf((-3.1415)), Double.valueOf((-0.2862)), Double.valueOf((-0.5000)), Double.valueOf(3.1400), Double.valueOf(1.6613), Double.valueOf((-0.0142))), double.class));
        jointTrajectoryPoints.time_from_start.nsecs = 0;
        jointTrajectoryPoints.time_from_start.secs = 120;
        final JointTrajectory after_pick = new JointTrajectory();
        List<JointTrajectoryPoint> list = Arrays.<JointTrajectoryPoint>asList(jointTrajectoryPoints);
        final List<JointTrajectoryPoint> _converted_list = (List<JointTrajectoryPoint>)list;
        after_pick.points = ((JointTrajectoryPoint[])Conversions.unwrapArray(_converted_list, JointTrajectoryPoint.class));
        after_pick.joint_names = ((String[])Conversions.unwrapArray(Arrays.<String>asList("joint1", "joint2", "joint3", "joint4", "joint5", "joint6"), String.class));
        after_pick.header.stamp.secs = 0;
        after_pick.header.stamp.nsecs = 0;
        after_pick.header.frame_id = "";
        pub.publish(after_pick);
        bridge.unsubscribe("/gripper_controller/state");
      }
    };
    bridge.subscribe(
      SubscriptionRequestMsg.generate("/gripper_controller/state").setType("control_msgs/JointTrajectoryControllerState").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
  }
}
