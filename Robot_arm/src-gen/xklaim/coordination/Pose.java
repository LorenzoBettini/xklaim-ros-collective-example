package xklaim.coordination;

import com.fasterxml.jackson.databind.JsonNode;
import coordination.JointTrajectory;
import java.util.Collections;
import java.util.List;
import klava.topology.KlavaProcess;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;

@SuppressWarnings("all")
public class Pose extends KlavaProcess {
  public Pose() {
    super("xklaim.coordination.Pose");
  }
  
  @Override
  public void executeProcess() {
    final String rosbridgeWebsocketURI = "ws://0.0.0.0:9090";
    final RosBridge bridge = new RosBridge();
    bridge.connect(rosbridgeWebsocketURI, true);
    final Publisher pub = new Publisher("/arm_controller/command", "trajectory_msgs/JointTrajectory", bridge);
    final RosListenDelegate _function = (JsonNode data, String stringRep) -> {
      final JsonNode actual = data.get("msg").get("actual").get("positions");
      final List<Double> desire = Collections.<Double>unmodifiableList(CollectionLiterals.<Double>newArrayList(Double.valueOf((-3.1417061706596003)), Double.valueOf((-0.28618833559546175)), Double.valueOf((-0.49998813405672404)), Double.valueOf(3.1396898889426783), Double.valueOf(1.6612913247682046), Double.valueOf((-0.0142))));
      double sum = 0.0;
      for (int i = 0; (i < 6); i = (i + 1)) {
        double _sum = sum;
        double _asDouble = actual.get(i).asDouble();
        Double _get = desire.get(i);
        double _minus = (_asDouble - (_get).doubleValue());
        double _pow = Math.pow(_minus, 2.0);
        sum = (_sum + _pow);
      }
      final double norm = Math.sqrt(sum);
      final double tol = 0.008;
      if ((norm <= tol)) {
        final JointTrajectory pose = new JointTrajectory().positions(
          new double[] { (-0.9546), (-0.20), (-0.7241), 3.1400, 1.6613, (-0.0142) }).jointNames(
          new String[] { "joint1", "joint2", "joint3", "joint4", "joint5", "joint6" });
        pub.publish(pose);
        bridge.unsubscribe("/arm_controller/state");
      }
    };
    bridge.subscribe(
      SubscriptionRequestMsg.generate("/arm_controller/state").setType("control_msgs/JointTrajectoryControllerState").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
  }
}
