package xklaim.coordination;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;
import coordination.ContactsState;
import coordination.PoseStamped;
import coordination.Twist;
import klava.Locality;
import klava.Tuple;
import klava.topology.KlavaProcess;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.InputOutput;
import ros.Publisher;
import ros.RosBridge;
import ros.RosListenDelegate;
import ros.SubscriptionRequestMsg;

@SuppressWarnings("all")
public class GiveObject extends KlavaProcess {
  private String rosbridgeWebsocketURI;
  
  public GiveObject(final String rosbridgeWebsocketURI) {
    super("xklaim.coordination.GiveObject");
    this.rosbridgeWebsocketURI = rosbridgeWebsocketURI;
  }
  
  @Override
  public void executeProcess() {
    final Locality local = this.self;
    final RosBridge bridge = new RosBridge();
    bridge.connect(this.rosbridgeWebsocketURI, true);
    String gripper = null;
    Tuple _Tuple = new Tuple(new Object[] {"open", String.class});
    in(_Tuple, local);
    gripper = (String) _Tuple.getItem(1);
    InputOutput.<String>println(String.format("The %s is opening", gripper));
    final Publisher pub = new Publisher("/robot1/move_base_simple/goal", "geometry_msgs/PoseStamped", bridge);
    final RosListenDelegate _function = (JsonNode data, String stringRep) -> {
      ObjectMapper mapper = new ObjectMapper();
      JsonNode rosMsgNode = data.get("msg");
      try {
        ContactsState state = mapper.<ContactsState>treeToValue(rosMsgNode, ContactsState.class);
        boolean _equals = Objects.equal((state.states[0]).collision1_name, "unit_box_2::link::collision");
        if (_equals) {
          String frame_id = null;
          Double x = null;
          Double y = null;
          Double w = null;
          Tuple _Tuple_1 = new Tuple(new Object[] {"give", String.class, Double.class, Double.class, Double.class});
          in(_Tuple_1, local);
          frame_id = (String) _Tuple_1.getItem(1);
          x = (Double) _Tuple_1.getItem(2);
          y = (Double) _Tuple_1.getItem(3);
          w = (Double) _Tuple_1.getItem(4);
          final PoseStamped giveobject = new PoseStamped().headerFrameId(frame_id).posePositionXY((x).doubleValue(), (y).doubleValue()).poseOrientation((w).doubleValue());
          pub.publish(giveobject);
          final Publisher pubvel = new Publisher("/robot1/cmd_vel", "geometry_msgs/Twist", bridge);
          final Twist twistMsg = new Twist();
          pubvel.publish(twistMsg);
          InputOutput.<String>println(String.format("Object is given"));
        }
      } catch (final Throwable _t) {
        if (_t instanceof Exception) {
        } else {
          throw Exceptions.sneakyThrow(_t);
        }
      }
    };
    bridge.subscribe(
      SubscriptionRequestMsg.generate("/robot1/pressure_sensor_state").setType("gazebo_msgs/ContactsState").setThrottleRate(Integer.valueOf(1)).setQueueLength(Integer.valueOf(1)), _function);
  }
}
