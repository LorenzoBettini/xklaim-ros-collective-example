package Coordination;

import com.fasterxml.jackson.databind.JsonNode;
import coordination.JointTrajectory;
import coordination.JointTrajectoryPoint;
import java.util.Arrays;
import java.util.List;
import klava.topology.KlavaProcess;
import org.eclipse.xtext.xbase.lib.Conversions;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;

@SuppressWarnings("all")
public class Pick extends KlavaProcess {
  public Pick() {
    super("Coordination.Pick");
  }
  
  @Override
  public void executeProcess() {
    final String rosbridgeWebsocketURI = "ws://0.0.0.0:9090";
    final RosBridge bridge = new RosBridge();
    bridge.connect(rosbridgeWebsocketURI, true);
    final Publisher pub = new Publisher("/arm_controller/command", "trajectory_msgs/JointTrajectory", bridge);
    final RosListenDelegate _function = (JsonNode data, String stringRep) -> {
      final JsonNode actual = data.get("msg").get("actual").get("positions");
      final List<Double> desire = Arrays.<Double>asList(Double.valueOf((-3.14)), Double.valueOf((-0.2169)), Double.valueOf((-0.5822)), Double.valueOf(3.14), Double.valueOf(1.66), Double.valueOf((-0.01412)));
      double sum = 0.0;
      for (int i = 0; (i < 6); i = (i + 1)) {
        double _asDouble = actual.get(i).asDouble();
        Double _get = desire.get(i);
        double _minus = (_asDouble - (_get).doubleValue());
        double _abs = Math.abs(_minus);
        double _plus = (sum + _abs);
        sum = _plus;
      }
      final double tol = 0.000001;
      if ((sum <= tol)) {
        final JointTrajectoryPoint jointTrajectoryPoints = new JointTrajectoryPoint();
        jointTrajectoryPoints.positions = ((double[])Conversions.unwrapArray(Arrays.<Double>asList(Double.valueOf((-3.1415)), Double.valueOf((-0.9975)), Double.valueOf((-0.4970)), Double.valueOf(3.1400), Double.valueOf(1.6613), Double.valueOf((-0.0142))), double.class));
        jointTrajectoryPoints.time_from_start.nsecs = 0;
        jointTrajectoryPoints.time_from_start.secs = 120;
        final JointTrajectory pick = new JointTrajectory();
        List<JointTrajectoryPoint> list = Arrays.<JointTrajectoryPoint>asList(jointTrajectoryPoints);
        final List<JointTrajectoryPoint> _converted_list = (List<JointTrajectoryPoint>)list;
        pick.points = ((JointTrajectoryPoint[])Conversions.unwrapArray(_converted_list, JointTrajectoryPoint.class));
        pick.joint_names = ((String[])Conversions.unwrapArray(Arrays.<String>asList("joint1", "joint2", "joint3", "joint4", "joint5", "joint6"), String.class));
        pick.header.stamp.secs = 0;
        pick.header.stamp.nsecs = 0;
        pick.header.frame_id = "";
        pub.publish(pick);
        bridge.unsubscribe("/arm_controller/state");
      }
    };
    bridge.subscribe(
      SubscriptionRequestMsg.generate("/arm_controller/state").setType("control_msgs/JointTrajectoryControllerState").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
  }
}
