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
public class Grasp extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  public Grasp(final String rosbridgeWebsocketURI) {
    super("xklaim.coordination.Grasp");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
  }
  
  @Override
  public void executeProcess() {
    final RosBridge bridge = new RosBridge();
    bridge.connect(this.rosbridgeWebsocketURI, true);
    final Publisher pub = new Publisher("/gripper_controller/command", "trajectory_msgs/JointTrajectory", bridge);
    final RosListenDelegate _function = (JsonNode data, String stringRep) -> {
      final JsonNode actual = data.get("msg").get("actual").get("positions");
      final List<Double> desire = Arrays.<Double>asList(Double.valueOf((-3.1415)), Double.valueOf((-0.9975)), Double.valueOf((-0.4970)), Double.valueOf(3.1400), Double.valueOf(1.6613), Double.valueOf((-0.0142)));
      double sum = 0.0;
      for (int i = 0; (i <= 5); i = (i + 1)) {
        double _asDouble = actual.get(i).asDouble();
        Double _get = desire.get(i);
        double _minus = (_asDouble - (_get).doubleValue());
        double _pow = Math.pow(_minus, 2.0);
        double _plus = (sum + _pow);
        sum = _plus;
      }
      final double norm = Math.sqrt(sum);
      final double tol = 0.00001;
      if ((norm <= tol)) {
        final JointTrajectory grasp = new JointTrajectory().positions(
          new double[] { 0.019927757424255833, (-0.010904802339570573) }).jointNames(
          new String[] { "f_joint1", "f_joint2" });
        pub.publish(grasp);
        bridge.unsubscribe("/arm_controller/state");
      }
    };
    bridge.subscribe(
      SubscriptionRequestMsg.generate("/arm_controller/state").setType("control_msgs/JointTrajectoryControllerState").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
  }
}
