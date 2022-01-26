package xklaim.coordination;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.Arrays;
import java.util.List;
import klava.Locality;
import klava.Tuple;
import klava.topology.KlavaProcess;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;

@SuppressWarnings("all")
public class sendOrder extends KlavaProcess {
  private Locality robot2;
  
  public sendOrder(final Locality robot2) {
    super("xklaim.coordination.sendOrder");
    this.robot2 = robot2;
  }
  
  @Override
  public void executeProcess() {
    final String rosbridgeWebsocketURI = "ws://0.0.0.0:9090";
    final Locality myself = this.self;
    final RosBridge bridge = new RosBridge();
    bridge.connect(rosbridgeWebsocketURI, true);
    final Publisher pub = new Publisher("/arm_controller/command", "trajectory_msgs/JointTrajectory", bridge);
    final RosListenDelegate _function = (JsonNode data, String stringRep) -> {
      final JsonNode error = data.get("msg").get("actual").get("positions");
      final List<Double> desire = Arrays.<Double>asList(Double.valueOf((-0.9546)), Double.valueOf((-0.2862)), Double.valueOf((-0.7241)), Double.valueOf(3.1400), Double.valueOf(1.6613), Double.valueOf((-0.0142)));
      double sum = 0.0;
      for (int i = 0; (i < 6); i = (i + 1)) {
        double _asDouble = error.get(i).asDouble();
        Double _get = desire.get(i);
        double _minus = (_asDouble - (_get).doubleValue());
        double _abs = Math.abs(_minus);
        double _plus = (sum + _abs);
        sum = _plus;
      }
      final double norm = Math.sqrt(sum);
      final double tol = 0.001;
      if ((sum <= tol)) {
        out(new Tuple(new Object[] {"goto1", "world", (-0.27), (-2.54), 1.0}), this.robot2);
      }
    };
    bridge.subscribe(
      SubscriptionRequestMsg.generate("/arm_controller/state").setType("control_msgs/JointTrajectoryControllerState").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
  }
}
