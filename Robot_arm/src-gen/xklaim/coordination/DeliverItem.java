package xklaim.coordination;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;
import coordination.ContactState;
import coordination.ContactsState;
import coordination.ModelState;
import coordination.PoseStamped;
import coordination.PoseWithCovarianceStamped;
import coordination.Twist;
import java.util.List;
import klava.Locality;
import klava.Tuple;
import klava.topology.KlavaProcess;
import org.eclipse.xtext.xbase.lib.Conversions;
import org.eclipse.xtext.xbase.lib.Exceptions;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;

@SuppressWarnings("all")
public class DeliverItem extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  public DeliverItem(final String rosbridgeWebsocketURI) {
    super("xklaim.coordination.DeliverItem");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
  }
  
  @Override
  public void executeProcess() {
    final Locality local = this.self;
    final RosBridge bridge = new RosBridge();
    bridge.connect(this.rosbridgeWebsocketURI, true);
    in(new Tuple(new Object[] {"gripperOpening"}), this.self);
    Double x = null;
    Double y = null;
    Double w = null;
    Tuple _Tuple = new Tuple(new Object[] {"destination", Double.class, Double.class, Double.class});
    in(_Tuple, local);
    x = (Double) _Tuple.getItem(1);
    y = (Double) _Tuple.getItem(2);
    w = (Double) _Tuple.getItem(3);
    final PoseStamped deliveryDestination = new PoseStamped().headerFrameId("world").posePositionXY((x).doubleValue(), (y).doubleValue()).poseOrientation((w).doubleValue());
    final Publisher pub = new Publisher("/robot1/move_base_simple/goal", "geometry_msgs/PoseStamped", bridge);
    final RosListenDelegate _function = (JsonNode data, String stringRep) -> {
      try {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rosMsgNode = data.get("msg");
        ContactsState state = mapper.<ContactsState>treeToValue(rosMsgNode, ContactsState.class);
        if (((!((List<ContactState>)Conversions.doWrapArray(state.states)).isEmpty()) && Objects.equal((state.states[0]).collision1_name, "unit_box_2::link::collision"))) {
          pub.publish(deliveryDestination);
          bridge.unsubscribe("/robot1/pressure_sensor_state");
        }
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    bridge.subscribe(
      SubscriptionRequestMsg.generate("/robot1/pressure_sensor_state").setType("gazebo_msgs/ContactsState").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
    final RosListenDelegate _function_1 = (JsonNode data, String stringRep) -> {
      try {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rosMsgNode = data.get("msg");
        PoseWithCovarianceStamped current_position = mapper.<PoseWithCovarianceStamped>treeToValue(rosMsgNode, PoseWithCovarianceStamped.class);
        final double tolerance = 0.16;
        double deltaX = (current_position.pose.pose.position.x - deliveryDestination.pose.position.x);
        double deltaY = (current_position.pose.pose.position.y - deliveryDestination.pose.position.y);
        if (((deltaX <= tolerance) && (deltaY <= tolerance))) {
          final Publisher pubvel = new Publisher("/robot1/cmd_vel", "geometry_msgs/Twist", bridge);
          final Twist twistMsg = new Twist();
          pubvel.publish(twistMsg);
          out(new Tuple(new Object[] {"itemDelivered"}), local);
          final Publisher gazebo = new Publisher("/gazebo/set_model_state", "gazebo_msgs/ModelState", bridge);
          final ModelState modelstate = new ModelState();
          modelstate.twist.linear.x = 3.0;
          modelstate.twist.angular.z = 1.0;
          modelstate.pose.position.x = (-46.0);
          modelstate.pose.position.y = 46.0;
          modelstate.pose.position.z = 0.0;
          modelstate.model_name = "unit_box_2";
          modelstate.reference_frame = "world";
          gazebo.publish(modelstate);
          bridge.unsubscribe("/robot1/amcl_pose");
        }
      } catch (Throwable _e) {
        throw Exceptions.sneakyThrow(_e);
      }
    };
    bridge.subscribe(
      SubscriptionRequestMsg.generate("/robot1/amcl_pose").setType("geometry_msgs/PoseWithCovarianceStamped").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function_1);
  }
}
