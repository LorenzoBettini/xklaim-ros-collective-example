package coordination;


public class JointTrajectory {
    public Header header = new Header();
    public String[] joint_names;
    public JointTrajectoryPoint[] points = new JointTrajectoryPoint[1];

    public JointTrajectory(){

    }

    public JointTrajectory(Header header, String[] joint_names, JointTrajectoryPoint[] points) {
        this.header=header;
        this.joint_names=joint_names;
        this.points=points;
    }

  //  public void setPoints(JointTrajectoryPoint points) {
    //    this.points = new JointTrajectoryPoint[]{points};
    //}

    //public void setJoint_names(String[] joint_names) {
      //  this.joint_names = joint_names;
    //}


}
