package xklaim.coordination;

import com.fasterxml.jackson.databind.JsonNode;
import coordination.JointTrajectory;
import java.util.Collections;
import java.util.List;
import klava.topology.KlavaProcess;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.Conversions;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;

@SuppressWarnings("all")
public class PickItem extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  public PickItem(final String rosbridgeWebsocketURI) {
    super("xklaim.coordination.PickItem");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
  }
  
  @Override
  public void executeProcess() {
    final RosBridge bridge = new RosBridge();
    bridge.connect(this.rosbridgeWebsocketURI, true);
    final Publisher pub = new Publisher("/arm_controller/command", "trajectory_msgs/JointTrajectory", bridge);
    final List<Double> trajectoryPositions = Collections.<Double>unmodifiableList(CollectionLiterals.<Double>newArrayList(Double.valueOf((-3.14)), Double.valueOf((-0.2169)), Double.valueOf((-0.5822)), Double.valueOf(3.14), Double.valueOf(1.66), Double.valueOf((-0.01412))));
    final JointTrajectory firstMovement = new JointTrajectory().positions(((double[])Conversions.unwrapArray(trajectoryPositions, double.class))).jointNames(
      new String[] { "joint1", "joint2", "joint3", "joint4", "joint5", "joint6" });
    pub.publish(firstMovement);
    final RosListenDelegate _function = (JsonNode data, String stringRep) -> {
      final JsonNode actual = data.get("msg").get("actual").get("positions");
      double sum = 0.0;
      for (int i = 0; (i < trajectoryPositions.size()); i++) {
        double _sum = sum;
        double _asDouble = actual.get(i).asDouble();
        Double _get = trajectoryPositions.get(i);
        double _minus = (_asDouble - (_get).doubleValue());
        double _abs = Math.abs(_minus);
        sum = (_sum + _abs);
      }
      final double tol = 0.000001;
      if ((sum <= tol)) {
        final Publisher pub2 = new Publisher("/arm_controller/command", "trajectory_msgs/JointTrajectory", bridge);
        final JointTrajectory pick = new JointTrajectory().positions(
          new double[] { (-3.1415), (-0.9975), (-0.4970), 3.1400, 1.6613, (-0.0142) }).jointNames(
          new String[] { "joint1", "joint2", "joint3", "joint4", "joint5", "joint6" });
        pub2.publish(pick);
      }
    };
    bridge.subscribe(
      SubscriptionRequestMsg.generate("/arm_controller/state").setType("control_msgs/JointTrajectoryControllerState").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
  }
}
