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
public class Afterpose extends KlavaProcess {
  private RosBridge bridge;
  
  private Locality robot2;
  
  public Afterpose(final RosBridge bridge, final Locality robot2) {
    super("xklaim.coordination.Afterpose");
    this.bridge = bridge;
    this.robot2 = robot2;
  }
  
  @Override
  public void executeProcess() {
    final Locality myself = this.self;
    String ready = null;
    Tuple _Tuple = new Tuple(new Object[] {"ready", String.class});
    in(_Tuple, myself);
    ready = (String) _Tuple.getItem(1);
    InputOutput.<String>println(String.format("%s to take the object", ready));
    final Publisher pub = new Publisher("/arm_controller/command", "trajectory_msgs/JointTrajectory", this.bridge);
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
    this.bridge.subscribe(
      SubscriptionRequestMsg.generate("/gripper_controller/state").setType(
        "control_msgs/JointTrajectoryControllerState").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
  }
}
