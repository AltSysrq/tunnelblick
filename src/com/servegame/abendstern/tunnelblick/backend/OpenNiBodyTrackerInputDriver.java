package com.servegame.abendstern.tunnelblick.backend;

import java.util.*;
import org.OpenNI.*;

/**
 * Uses OpenNI body tracking to position the body(ies).
 *
 * It also has a jump "gesture" hacked in which triggers when the user's right
 * arm is above his torso.
 */
public class OpenNiBodyTrackerInputDriver implements InputDriver {
  private boolean ready = false;
  class NewUserObserver implements IObserver<UserEventArgs>
    {
      @Override
        public void update(IObservable<UserEventArgs> observable,
                           UserEventArgs args)
        {
          System.out.println("New user " + args.getId());
          try
            {
              if (skeletonCap.needPoseForCalibration())
                {
                  poseDetectionCap.startPoseDetection(calibPose, args.getId());
                }
              else
                {
                  skeletonCap.requestSkeletonCalibration(args.getId(), true);
                }
            } catch (StatusException e)
            {
              e.printStackTrace();
            }
        }
    }
  class LostUserObserver implements IObserver<UserEventArgs>
    {
      @Override
        public void update(IObservable<UserEventArgs> observable,
                           UserEventArgs args)
        {
          System.out.println("Lost user " + args.getId());
          joints.remove(args.getId());
        }
    }
	
  class CalibrationCompleteObserver implements IObserver<CalibrationProgressEventArgs>
    {
      @Override
        public void update(IObservable<CalibrationProgressEventArgs> observable,
                           CalibrationProgressEventArgs args)
        {
          System.out.println("Calibraion complete: " + args.getStatus());
          ready = true;
          try
            {
              if (args.getStatus() == CalibrationProgressStatus.OK)
                {
                  System.out.println("starting tracking "  +args.getUser());
                  skeletonCap.startTracking(args.getUser());
                  joints.put(new Integer(args.getUser()), new HashMap<SkeletonJoint, SkeletonJointPosition>());
                }
              else if (args.getStatus() != CalibrationProgressStatus.MANUAL_ABORT)
                {
                  if (skeletonCap.needPoseForCalibration())
                    {
                      poseDetectionCap.startPoseDetection(calibPose, args.getUser());
                    }
                  else
                    {
                      skeletonCap.requestSkeletonCalibration(args.getUser(), true);
                    }
                }
            } catch (StatusException e)
            {
              e.printStackTrace();
            }
        }
    }
  class PoseDetectedObserver implements IObserver<PoseDetectionEventArgs>
    {
      @Override
        public void update(IObservable<PoseDetectionEventArgs> observable,
                           PoseDetectionEventArgs args)
        {
          System.out.println("Pose " + args.getPose() + " detected for " + args.getUser());
          try
            {
              poseDetectionCap.stopPoseDetection(args.getUser());
              skeletonCap.requestSkeletonCalibration(args.getUser(), true);
            } catch (StatusException e)
            {
              e.printStackTrace();
            }
        }
    }

  private static final long serialVersionUID = 1L;
  private OutArg<ScriptNode> scriptNode;
  private Context context;
  private DepthGenerator depthGen;
  private UserGenerator userGen;
  private SkeletonCapability skeletonCap;
  private PoseDetectionCapability poseDetectionCap;
  private byte[] imgbytes;
  private float histogram[];
  private int width, height;
  private GameManager manager;
  String calibPose = null;
  HashMap<Integer, HashMap<SkeletonJoint, SkeletonJointPosition>> joints;

  private final String SAMPLE_XML_FILE = "openniconfig.xml";

  private boolean collapseUsersToOne;

  public OpenNiBodyTrackerInputDriver(boolean oneUser) {
    collapseUsersToOne = oneUser;
    try {
      scriptNode = new OutArg<ScriptNode>();
      context = Context.createFromXmlFile(SAMPLE_XML_FILE, scriptNode);

      depthGen = DepthGenerator.create(context);
      DepthMetaData depthMD = depthGen.getMetaData();

      histogram = new float[10000];
      width = depthMD.getFullXRes();
      height = depthMD.getFullYRes();

      imgbytes = new byte[width*height*3];

      userGen = UserGenerator.create(context);
      skeletonCap = userGen.getSkeletonCapability();
      poseDetectionCap = userGen.getPoseDetectionCapability();

      userGen.getNewUserEvent().addObserver(new NewUserObserver());
      userGen.getLostUserEvent().addObserver(new LostUserObserver());
      skeletonCap.getCalibrationCompleteEvent().addObserver(new CalibrationCompleteObserver());
      poseDetectionCap.getPoseDetectedEvent().addObserver(new PoseDetectedObserver());

      calibPose = skeletonCap.getSkeletonCalibrationPose();
      joints = new HashMap<Integer, HashMap<SkeletonJoint,SkeletonJointPosition>>();

      skeletonCap.setSkeletonProfile(SkeletonProfile.ALL);

      context.startGeneratingAll();
      while (!ready)
        context.waitAnyUpdateAll();
      while (!joints.containsKey(1) ||
             !getJoint(1, SkeletonJoint.TORSO) ||
             0 == joints.get(1).get(SkeletonJoint.TORSO).getConfidence())
        context.waitAnyUpdateAll();
    } catch (GeneralException e) {
      e.printStackTrace();
      System.exit(1);
    }
  }


  public boolean getJoint(int user, SkeletonJoint joint) throws StatusException {
    SkeletonJointPosition pos = skeletonCap.getSkeletonJointPosition(user, joint);
    if (pos.getPosition().getZ() != 0) {
      joints.get(user).put(joint,
                           new SkeletonJointPosition(
                             depthGen.convertRealWorldToProjective(
                               pos.getPosition()), pos.getConfidence()));
    } else {
      joints.get(user).put(joint, new SkeletonJointPosition(new Point3D(), 0));
    }
    return true;
  }

  public void installInto(GameManager man) {
    manager = man;
  }

  public void pumpInput(InputReceiver dst) {
    try {
      context.waitAnyUpdateAll();
    } catch (StatusException se) {
      se.printStackTrace();
    }
    for (int user: joints.keySet()) {
      try {
        getJoint(user, SkeletonJoint.TORSO);
        getJoint(user, SkeletonJoint.RIGHT_HAND);
      } catch (StatusException se) {
        se.printStackTrace();
      }
      Point3D p = joints.get(user).get(SkeletonJoint.TORSO).getPosition();
      int body = (collapseUsersToOne? 0 : user % 2);

      float x = p.getX() / (float)width;
      manager.getSharedInputStatus().bodies[body] = x;
      dst.receiveInput(new InputEvent(InputEvent.TYPE_BODY_MOVEMENT,
                                      body, x, 0));

      Point3D hp =
        joints.get(user).get(SkeletonJoint.RIGHT_HAND).getPosition();
      if (hp.getY() < p.getY())
        dst.receiveInput(new InputEvent(InputEvent.TYPE_GESTURE,
                                        InputEvent.GESTURE_JUMP, 0, 0));
    }
  }
}
