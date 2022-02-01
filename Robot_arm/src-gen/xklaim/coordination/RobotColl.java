package xklaim.coordination;

import klava.LogicalLocality;
import klava.PhysicalLocality;
import klava.topology.ClientNode;
import klava.topology.KlavaNodeCoordinator;
import klava.topology.LogicalNet;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.mikado.imc.common.IMCException;
import ros.RosBridge;

@SuppressWarnings("all")
public class RobotColl extends LogicalNet {
  private static final LogicalLocality Arm = new LogicalLocality("Arm");
  
  private static final LogicalLocality Robot2 = new LogicalLocality("Robot2");
  
  public static class Arm extends ClientNode {
    private static class ArmProcess extends KlavaNodeCoordinator {
      @Override
      public void executeProcess() {
        try {
          final String rosbridgeWebsocketURI = "ws://0.0.0.0:9090";
          final RosBridge bridge = new RosBridge();
          bridge.connect(rosbridgeWebsocketURI, true);
          Before_pick _before_pick = new Before_pick(bridge);
          this.executeNodeProcess(_before_pick);
          Pick _pick = new Pick(bridge);
          this.executeNodeProcess(_pick);
          Grasp _grasp = new Grasp();
          this.executeNodeProcess(_grasp);
          After_pick _after_pick = new After_pick();
          this.executeNodeProcess(_after_pick);
          Pose _pose = new Pose();
          this.executeNodeProcess(_pose);
          Posefinal _posefinal = new Posefinal(RobotColl.Robot2);
          this.executeNodeProcess(_posefinal);
          OpenGripper _openGripper = new OpenGripper(RobotColl.Robot2);
          this.executeNodeProcess(_openGripper);
          Afterpose _afterpose = new Afterpose(RobotColl.Robot2);
          this.executeNodeProcess(_afterpose);
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
  
  public static class Robot2 extends ClientNode {
    private static class Robot2Process extends KlavaNodeCoordinator {
      @Override
      public void executeProcess() {
        try {
          Moveto _moveto = new Moveto(RobotColl.Arm);
          this.executeNodeProcess(_moveto);
          GiveObject _giveObject = new GiveObject();
          this.executeNodeProcess(_giveObject);
        } catch (Throwable _e) {
          throw Exceptions.sneakyThrow(_e);
        }
      }
    }
    
    public Robot2() {
      super(new PhysicalLocality("localhost:9999"), new LogicalLocality("Robot2"));
    }
    
    public void addMainProcess() throws IMCException {
      addNodeCoordinator(new RobotColl.Robot2.Robot2Process());
    }
  }
  
  public RobotColl() throws IMCException {
    super(new PhysicalLocality("localhost:9999"));
  }
  
  public void addNodes() throws IMCException {
    RobotColl.Arm arm = new RobotColl.Arm();
    RobotColl.Robot2 robot2 = new RobotColl.Robot2();
    arm.addMainProcess();
    robot2.addMainProcess();
  }
}
