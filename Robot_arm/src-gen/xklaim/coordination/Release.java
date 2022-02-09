package xklaim.coordination;

import com.fasterxml.jackson.databind.JsonNode;
import coordination.JointTrajectory;
import java.util.Arrays;
import java.util.List;
import klava.Locality;
import klava.Tuple;
import klava.topology.KlavaProcess;
import org.eclipse.xtext.xbase.lib.InputOutput;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;

@SuppressWarnings("all")
public class Release extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  private Locality robot2;
  
  public Release(final String rosbridgeWebsocketURI, final Locality robot2) {
    super("xklaim.coordination.Release");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
    this.robot2 = robot2;
  }
  
  @Override
  public void executeProcess() {
    final Locality local = this.self;
    final RosBridge bridge = new RosBridge();
    bridge.connect(this.rosbridgeWebsocketURI, true);
    final Publisher pub = new Publisher("/gripper_controller/command", "trajectory_msgs/JointTrajectory", bridge);
    final RosListenDelegate _function = (JsonNode data, String stringRep) -> {
      final JsonNode actual = data.get("msg").get("actual").get("positions");
      final List<Double> desire = Arrays.<Double>asList(Double.valueOf((-0.9546)), Double.valueOf((-0.0097)), Double.valueOf((-0.9513)), Double.valueOf(3.1400), Double.valueOf(1.7749), Double.valueOf((-0.0142)));
      double sum = 0.0;
      for (int i = 0; (i < 6); i = (i + 1)) {
        double _asDouble = actual.get(i).asDouble();
        Double _get = desire.get(i);
        double _minus = (_asDouble - (_get).doubleValue());
        double _pow = Math.pow(_minus, 2.0);
        double _plus = (sum + _pow);
        sum = _plus;
      }
      final double norm = Math.sqrt(sum);
      final double tol = 0.001;
      if ((norm <= tol)) {
        final JointTrajectory open = new JointTrajectory().positions(
          new double[] { 0.000, 0.0000 }).jointNames(
          new String[] { "f_joint1", "f_joint2" });
        pub.publish(open);
        bridge.unsubscribe("/arm_controller/state");
        InputOutput.<String>println(String.format("I am opening"));
        bridge.unsubscribe("/gripper_controller/state");
        out(new Tuple(new Object[] {"open", "gripper"}), this.robot2);
        out(new Tuple(new Object[] {"opened"}), local);
      }
    };
    bridge.subscribe(
      SubscriptionRequestMsg.generate("/arm_controller/state").setType("control_msgs/JointTrajectoryControllerState").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
  }
}
