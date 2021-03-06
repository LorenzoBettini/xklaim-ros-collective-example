package xklaim.coordination;

import coordination.ModelState;
import klava.LogicalLocality;
import klava.PhysicalLocality;
import klava.Tuple;
import klava.topology.ClientNode;
import klava.topology.KlavaNodeCoordinator;
import klava.topology.LogicalNet;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.mikado.imc.common.IMCException;
import ros.Publisher;
import ros.RosBridge;

@SuppressWarnings("all")
public class RobotColl extends LogicalNet {
  private static final LogicalLocality Arm = new LogicalLocality("Arm");
  
  private static final LogicalLocality DeliveryRobot = new LogicalLocality("DeliveryRobot");
  
  private static final LogicalLocality SimuationHandler = new LogicalLocality("SimuationHandler");
  
  public static class Arm extends ClientNode {
    private static class ArmProcess extends KlavaNodeCoordinator {
      @Override
      public void executeProcess() {
        final String rosbridgeWebsocketURI = "ws://0.0.0.0:9090";
        GetDown _getDown = new GetDown(rosbridgeWebsocketURI);
        eval(_getDown, this.self);
        Grip _grip = new Grip(rosbridgeWebsocketURI);
        eval(_grip, this.self);
        GetUp _getUp = new GetUp(rosbridgeWebsocketURI);
        eval(_getUp, this.self);
        Rotate _rotate = new Rotate(rosbridgeWebsocketURI, RobotColl.DeliveryRobot);
        eval(_rotate, this.self);
        Lay _lay = new Lay(rosbridgeWebsocketURI, RobotColl.DeliveryRobot);
        eval(_lay, this.self);
        Release _release = new Release(rosbridgeWebsocketURI, RobotColl.DeliveryRobot);
        eval(_release, this.self);
        GoToInitialPosition _goToInitialPosition = new GoToInitialPosition(rosbridgeWebsocketURI, RobotColl.DeliveryRobot);
        eval(_goToInitialPosition, this.self);
      }
    }
    
    public Arm() {
      super(new PhysicalLocality("localhost:9999"), new LogicalLocality("Arm"));
    }
    
    public void addMainProcess() throws IMCException {
      addNodeCoordinator(new RobotColl.Arm.ArmProcess());
    }
  }
  
  public static class DeliveryRobot extends ClientNode {
    private static class DeliveryRobotProcess extends KlavaNodeCoordinator {
      @Override
      public void executeProcess() {
        final String rosbridgeWebsocketURI = "ws://0.0.0.0:9090";
        MovetoArm _movetoArm = new MovetoArm(rosbridgeWebsocketURI, RobotColl.Arm);
        eval(_movetoArm, this.self);
        DeliverItem _deliverItem = new DeliverItem(rosbridgeWebsocketURI);
        eval(_deliverItem, this.self);
      }
    }
    
    public DeliveryRobot() {
      super(new PhysicalLocality("localhost:9999"), new LogicalLocality("DeliveryRobot"));
    }
    
    public void addMainProcess() throws IMCException {
      addNodeCoordinator(new RobotColl.DeliveryRobot.DeliveryRobotProcess());
    }
  }
  
  public static class SimuationHandler extends ClientNode {
    private static class SimuationHandlerProcess extends KlavaNodeCoordinator {
      @Override
      public void executeProcess() {
        try {
          final String rosbridgeWebsocketURI = "ws://0.0.0.0:9090";
          final RosBridge bridge = new RosBridge();
          bridge.connect(rosbridgeWebsocketURI, true);
          in(new Tuple(new Object[] {"itemDelivered"}), RobotColl.DeliveryRobot);
          Thread.sleep(2000);
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
          in(new Tuple(new Object[] {"initialPositionReached"}), RobotColl.Arm);
          System.exit(0);
        } catch (Throwable _e) {
          throw Exceptions.sneakyThrow(_e);
        }
      }
    }
    
    public SimuationHandler() {
      super(new PhysicalLocality("localhost:9999"), new LogicalLocality("SimuationHandler"));
    }
    
    public void addMainProcess() throws IMCException {
      addNodeCoordinator(new RobotColl.SimuationHandler.SimuationHandlerProcess());
    }
  }
  
  public RobotColl() throws IMCException {
    super(new PhysicalLocality("localhost:9999"));
  }
  
  public void addNodes() throws IMCException {
    RobotColl.Arm arm = new RobotColl.Arm();
    RobotColl.DeliveryRobot deliveryRobot = new RobotColl.DeliveryRobot();
    RobotColl.SimuationHandler simuationHandler = new RobotColl.SimuationHandler();
    arm.addMainProcess();
    deliveryRobot.addMainProcess();
    simuationHandler.addMainProcess();
  }
}
