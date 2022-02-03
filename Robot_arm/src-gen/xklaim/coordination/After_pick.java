package xklaim.coordination;

import com.fasterxml.jackson.databind.JsonNode;
import coordination.JointTrajectory;
import java.util.Arrays;
import java.util.List;
import klava.topology.KlavaProcess;
import org.eclipse.xtext.xbase.lib.InputOutput;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;

@SuppressWarnings("all")
public class After_pick extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  public After_pick(final String rosbridgeWebsocketURI) {
    super("xklaim.coordination.After_pick");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
  }
  
  @Override
  public void executeProcess() {
    final RosBridge bridge = new RosBridge();
    bridge.connect(this.rosbridgeWebsocketURI, true);
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
        final JointTrajectory afterPick = new JointTrajectory().positions(
          new double[] { (-3.1415), (-0.2862), (-0.5000), 3.1400, 1.6613, (-0.0142) }).jointNames(
          new String[] { "joint1", "joint2", "joint3", "joint4", "joint5", "joint6" });
        pub.publish(afterPick);
        bridge.unsubscribe("/gripper_controller/state");
      }
    };
    bridge.subscribe(
      SubscriptionRequestMsg.generate("/gripper_controller/state").setType(
        "control_msgs/JointTrajectoryControllerState").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
  }
}
