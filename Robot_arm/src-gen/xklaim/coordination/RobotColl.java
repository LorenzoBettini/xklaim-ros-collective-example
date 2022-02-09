package xklaim.coordination;

import klava.LogicalLocality;
import klava.PhysicalLocality;
import klava.topology.ClientNode;
import klava.topology.KlavaNodeCoordinator;
import klava.topology.LogicalNet;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.mikado.imc.common.IMCException;

@SuppressWarnings("all")
public class RobotColl extends LogicalNet {
  private static final LogicalLocality Arm = new LogicalLocality("Arm");
  
  private static final LogicalLocality DeliveryRobot = new LogicalLocality("DeliveryRobot");
  
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
        try {
          final String rosbridgeWebsocketURI = "ws://0.0.0.0:9090";
          Moveto _moveto = new Moveto(rosbridgeWebsocketURI, RobotColl.Arm);
          this.executeNodeProcess(_moveto);
          GiveObject _giveObject = new GiveObject(rosbridgeWebsocketURI);
          this.executeNodeProcess(_giveObject);
        } catch (Throwable _e) {
          throw Exceptions.sneakyThrow(_e);
        }
      }
    }
    
    public DeliveryRobot() {
      super(new PhysicalLocality("localhost:9999"), new LogicalLocality("DeliveryRobot"));
    }
    
    public void addMainProcess() throws IMCException {
      addNodeCoordinator(new RobotColl.DeliveryRobot.DeliveryRobotProcess());
    }
  }
  
  public RobotColl() throws IMCException {
    super(new PhysicalLocality("localhost:9999"));
  }
  
  public void addNodes() throws IMCException {
    RobotColl.Arm arm = new RobotColl.Arm();
    RobotColl.DeliveryRobot deliveryRobot = new RobotColl.DeliveryRobot();
    arm.addMainProcess();
    deliveryRobot.addMainProcess();
  }
}
