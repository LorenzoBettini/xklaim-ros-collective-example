package coordination;



public class JointTrajectoryPoint {

    public double[] positions;
    public double[] velocities;
    public double[] accelerations;
    public double[] effort;
    public Duration time_from_start=new Duration();


    public JointTrajectoryPoint(){
            this.velocities = new double[0];
            this.accelerations= new double[0];
            this.effort= new double[0];
        }

        public JointTrajectoryPoint(double[] positions, double[] velocities, double[] accelerations, double[] effort, Duration time_from_start) {
            this.positions = positions;
            this.velocities = velocities;
            this.accelerations = accelerations;
            this.effort = effort;
            this.time_from_start = time_from_start;
        }

      //  public void setPositions(double[] positions) {
        //    this.positions = positions;
        //}

    //public void setTime_from_start(long time_from_start_second) {
      //  this.time_from_start = Duration.ofSeconds(time_from_start_second);
    //}
}