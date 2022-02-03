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
public class Posefinal extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  private Locality robot2;
  
  public Posefinal(final String rosbridgeWebsocketURI, final Locality robot2) {
    super("xklaim.coordination.Posefinal");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
    this.robot2 = robot2;
  }
  
  @Override
  public void executeProcess() {
    final Locality myself = this.self;
    final RosBridge bridge = new RosBridge();
    bridge.connect(this.rosbridgeWebsocketURI, true);
    final Publisher pub = new Publisher("/arm_controller/command", "trajectory_msgs/JointTrajectory", bridge);
    final RosListenDelegate _function = (JsonNode data, String stringRep) -> {
      final JsonNode actual = data.get("msg").get("actual").get("positions");
      final List<Double> desire = Collections.<Double>unmodifiableList(CollectionLiterals.<Double>newArrayList(Double.valueOf((-0.9546)), Double.valueOf((-0.20)), Double.valueOf((-0.7241)), Double.valueOf(3.1400), Double.valueOf(1.6613), Double.valueOf((-0.0142))));
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
        out(new Tuple(new Object[] {"goto1", "world", (-0.25), (-2.67), 1.0}), this.robot2);
        String arrived = null;
        Tuple _Tuple = new Tuple(new Object[] {"arrived", String.class});
        in(_Tuple, myself);
        arrived = (String) _Tuple.getItem(1);
        InputOutput.<String>println(String.format("I am: %s,", arrived));
        final JointTrajectory posefinal = new JointTrajectory().positions(
          new double[] { (-0.9546), (-0.0097), (-0.9513), 3.1400, 1.7749, (-0.0142) }).jointNames(
          new String[] { "joint1", "joint2", "joint3", "joint4", "joint5", "joint6" });
        pub.publish(posefinal);
        InputOutput.<String>println(String.format("Iam posing"));
        bridge.unsubscribe("/arm_controller/state");
      }
    };
    bridge.subscribe(
      SubscriptionRequestMsg.generate("/arm_controller/state").setType("control_msgs/JointTrajectoryControllerState").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
  }
}
