package coordination;

import ros.msgs.std_msgs.Header;

public class JointTrajectoryControllerState {

    public Header header = new Header();
    public String[] joint_names;
    public JointTrajectoryPoint[] desired = new JointTrajectoryPoint[1];
    public JointTrajectoryPoint[] actual = new JointTrajectoryPoint[1];
    public JointTrajectoryPoint[] error = new JointTrajectoryPoint[1];



    public JointTrajectoryControllerState(){

    }


    public JointTrajectoryControllerState(Header header, String[] joint_names, JointTrajectoryPoint[] desired, JointTrajectoryPoint[] actual,JointTrajectoryPoint[] error) {
        this.header=header;
        this.joint_names=joint_names;
        this.desired=desired;
        this.actual=actual;
        this.error=error;
    }



}
