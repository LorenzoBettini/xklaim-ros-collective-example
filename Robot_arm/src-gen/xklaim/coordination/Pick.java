package xklaim.coordination;

import com.fasterxml.jackson.databind.JsonNode;
import coordination.JointTrajectory;
import java.util.Arrays;
import java.util.List;
import klava.topology.KlavaProcess;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;

@SuppressWarnings("all")
public class Pick extends KlavaProcess {
  private RosBridge bridge;
  
  public Pick(final RosBridge bridge) {
    super("xklaim.coordination.Pick");
    this.bridge = bridge;
  }
  
  @Override
  public void executeProcess() {
    final Publisher pub = new Publisher("/arm_controller/command", "trajectory_msgs/JointTrajectory", this.bridge);
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
        final JointTrajectory pick = new JointTrajectory().positions(
          new double[] { (-3.1415), (-0.9975), (-0.4970), 3.1400, 1.6613, (-0.0142) }).jointNames(
          new String[] { "joint1", "joint2", "joint3", "joint4", "joint5", "joint6" });
        pub.publish(pick);
        this.bridge.unsubscribe("/arm_controller/state");
      }
    };
    this.bridge.subscribe(
      SubscriptionRequestMsg.generate("/arm_controller/state").setType("control_msgs/JointTrajectoryControllerState").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
  }
}
