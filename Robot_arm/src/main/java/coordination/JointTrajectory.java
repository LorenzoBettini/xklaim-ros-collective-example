package coordination;

import ros.msgs.std_msgs.Header;

public class JointTrajectory {
	public Header header = new Header();
	public String[] joint_names;
	public JointTrajectoryPoint[] points = new JointTrajectoryPoint[1];

	public JointTrajectory() {

	}

	public JointTrajectory jointNames(String[] joint_names) {
		this.joint_names = joint_names;
		return this;
	}

	public JointTrajectory header(Header header) {
		this.header = header;
		return this;
	}

	public JointTrajectory points(JointTrajectoryPoint[] points) {
		this.points = points;
		return this;
	}
}
