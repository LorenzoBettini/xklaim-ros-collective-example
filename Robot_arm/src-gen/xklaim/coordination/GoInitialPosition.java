package xklaim.coordination;

import com.fasterxml.jackson.databind.JsonNode;
import coordination.JointTrajectory;
import java.util.Collections;
import java.util.List;
import klava.Locality;
import klava.Tuple;
import klava.topology.KlavaProcess;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;
import org.eclipse.xtext.xbase.lib.InputOutput;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;

@SuppressWarnings("all")
public class GoInitialPosition extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  private Locality robot2;
  
  public GoInitialPosition(final String rosbridgeWebsocketURI, final Locality robot2) {
    super("xklaim.coordination.GoInitialPosition");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
    this.robot2 = robot2;
  }
  
  @Override
  public void executeProcess() {
    final Locality myself = this.self;
    final RosBridge bridge = new RosBridge();
    bridge.connect(this.rosbridgeWebsocketURI, true);
    in(new Tuple(new Object[] {"opened"}), myself);
    InputOutput.<String>println(String.format("ready to take the object"));
    final Publisher pub = new Publisher("/arm_controller/command", "trajectory_msgs/JointTrajectory", bridge);
    final RosListenDelegate _function = (JsonNode data, String stringRep) -> {
      final JsonNode error = data.get("msg").get("actual").get("positions");
      final List<Double> desire = Collections.<Double>unmodifiableList(CollectionLiterals.<Double>newArrayList(Double.valueOf(0.000), Double.valueOf(0.000)));
      double sum = 0.0;
      for (int i = 0; (i <= 1); i = (i + 1)) {
        double _sum = sum;
        double _asDouble = error.get(i).asDouble();
        Double _get = desire.get(i);
        double _minus = (_asDouble - (_get).doubleValue());
        double _pow = Math.pow(_minus, 2.0);
        sum = (_sum + _pow);
      }
      final double tol = 0.0009;
      final double norm = Math.sqrt(sum);
      if ((norm <= tol)) {
        final JointTrajectory afterPose = new JointTrajectory().positions(
          new double[] { 0.000, 0.000, 0.000, 0.000, 0.000, 0.000 }).jointNames(
          new String[] { "joint1", "joint2", "joint3", "joint4", "joint5", "joint6" });
        pub.publish(afterPose);
        out(new Tuple(new Object[] {"give", "world", (-6.0), (-5.0), 1.0}), this.robot2);
      }
    };
    bridge.subscribe(
      SubscriptionRequestMsg.generate("/gripper_controller/state").setType(
        "control_msgs/JointTrajectoryControllerState").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
  }
}
