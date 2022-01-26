package xklaim.coordination;

import com.fasterxml.jackson.databind.JsonNode;
import coordination.JointTrajectory;
import coordination.JointTrajectoryPoint;
import java.util.Arrays;
import java.util.List;
import klava.Locality;
import klava.Tuple;
import klava.topology.KlavaProcess;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.InputOutput;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;

@SuppressWarnings("all")
public class Afterpose extends KlavaProcess {
  private Locality robot2;
  
  public Afterpose(final Locality robot2) {
    super("xklaim.coordination.Afterpose");
    this.robot2 = robot2;
  }
  
  @Override
  public void executeProcess() {
    final String rosbridgeWebsocketURI = "ws://0.0.0.0:9090";
    final Locality myself = this.self;
    final RosBridge bridge = new RosBridge();
    bridge.connect(rosbridgeWebsocketURI, true);
    String ready = null;
    Tuple _Tuple = new Tuple(new Object[] {"ready", String.class});
    in(_Tuple, myself);
    ready = (String) _Tuple.getItem(1);
    InputOutput.<String>println(String.format("%s to take the object", ready));
    final Publisher pub = new Publisher("/arm_controller/command", "trajectory_msgs/JointTrajectory", bridge);
    final RosListenDelegate _function = (JsonNode data, String stringRep) -> {
      final JsonNode error = data.get("msg").get("actual").get("positions");
      final List<Double> desire = Arrays.<Double>asList(Double.valueOf(0.000), Double.valueOf(0.000));
      double sum = 0.0;
      for (int i = 0; (i <= 1); i = (i + 1)) {
        double _asDouble = error.get(i).asDouble();
        Double _get = desire.get(i);
        double _minus = (_asDouble - (_get).doubleValue());
        double _pow = Math.pow(_minus, 2.0);
        double _plus = (sum + _pow);
        sum = _plus;
      }
      final double tol = 0.0009;
      final double norm = Math.sqrt(sum);
      if ((norm <= tol)) {
        final JointTrajectoryPoint jointTrajectoryPoints = new JointTrajectoryPoint();
        jointTrajectoryPoints.positions = ((double[])Conversions.unwrapArray(Arrays.<Double>asList(Double.valueOf(0.000), Double.valueOf(0.000), Double.valueOf(0.000), Double.valueOf(0.000), Double.valueOf(0.000), Double.valueOf(0.000)), double.class));
        jointTrajectoryPoints.time_from_start.nsecs = 0;
        jointTrajectoryPoints.time_from_start.secs = 120;
        final JointTrajectory after_pose = new JointTrajectory();
        List<JointTrajectoryPoint> list = Arrays.<JointTrajectoryPoint>asList(jointTrajectoryPoints);
        final List<JointTrajectoryPoint> _converted_list = (List<JointTrajectoryPoint>)list;
        after_pose.points = ((JointTrajectoryPoint[])Conversions.unwrapArray(_converted_list, JointTrajectoryPoint.class));
        after_pose.joint_names = ((String[])Conversions.unwrapArray(Arrays.<String>asList("joint1", "joint2", "joint3", "joint4", "joint5", "joint6"), String.class));
        after_pose.header.stamp.secs = 0;
        after_pose.header.stamp.nsecs = 0;
        after_pose.header.frame_id = "";
        pub.publish(after_pose);
        out(new Tuple(new Object[] {"give", "world", (-6.0), (-5.0), 1.0}), this.robot2);
      }
    };
    bridge.subscribe(
      SubscriptionRequestMsg.generate("/gripper_controller/state").setType("control_msgs/JointTrajectoryControllerState").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
  }
}
