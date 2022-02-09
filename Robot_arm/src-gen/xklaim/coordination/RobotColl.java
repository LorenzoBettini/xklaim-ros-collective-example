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
        try {
          final String rosbridgeWebsocketURI = "ws://0.0.0.0:9090";
          GetDown _getDown = new GetDown(rosbridgeWebsocketURI);
          this.executeNodeProcess(_getDown);
          Grip _grip = new Grip(rosbridgeWebsocketURI);
          this.executeNodeProcess(_grip);
          GetUp _getUp = new GetUp(rosbridgeWebsocketURI);
          this.executeNodeProcess(_getUp);
          Rotate _rotate = new Rotate(rosbridgeWebsocketURI, RobotColl.DeliveryRobot);
          this.executeNodeProcess(_rotate);
          Lay _lay = new Lay(rosbridgeWebsocketURI, RobotColl.DeliveryRobot);
          this.executeNodeProcess(_lay);
          Release _release = new Release(rosbridgeWebsocketURI, RobotColl.DeliveryRobot);
          this.executeNodeProcess(_release);
          GoInitialPosition _goInitialPosition = new GoInitialPosition(rosbridgeWebsocketURI, RobotColl.DeliveryRobot);
          this.executeNodeProcess(_goInitialPosition);
        } catch (Throwable _e) {
          throw Exceptions.sneakyThrow(_e);
        }
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
