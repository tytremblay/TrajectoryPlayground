package com.team254.lib.trajectory;

import com.team254.lib.trajectory.Trajectory.Segment;

/**
 * Base class for an autonomous path.
 * 
 * @author Jared341
 */
public class Path {
  protected Trajectory.Pair pair;
  protected String name_;
  protected boolean go_left_;
  
  public Path(String name, Trajectory.Pair go_left_pair) {
    name_ = name;
    pair = go_left_pair;
    go_left_ = true;
  }
  
  public Path() {
    
  }
  
  public String getName() { return name_; }
  
  public void goLeft() { 
    go_left_ = true; 
    pair.left.setInvertedY(false);
    pair.right.setInvertedY(false);
  }
  
  public void goRight() {
    go_left_ = false; 
    pair.left.setInvertedY(true);
    pair.right.setInvertedY(true);
  }
  
  public Trajectory getLeftWheelTrajectory() {
    return (go_left_ ? pair.left : pair.right);
  }
  
  public Trajectory getRightWheelTrajectory() {
    return (go_left_ ? pair.right : pair.left);
  }
  
  public Trajectory.Pair getPair() {
    return pair;
  }

  public double getEndHeading() {
    int numSegments = getLeftWheelTrajectory().getNumSegments();
    Trajectory.Segment lastSegment = getLeftWheelTrajectory().getSegment(numSegments - 1);
    return lastSegment.heading;
  }
}
