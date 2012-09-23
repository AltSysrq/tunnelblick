package com.servegame.abendstern.tunnelblick.game;

import java.util.*;
import javax.media.opengl.*;

import static java.lang.Math.*;

/**
 * Transforms the world according to the logical z coordinate, to make the
 * tunnel more interesting.
 */
public final class Distortion {
  private static final float SECTION_LEN = Tunnel.GSQ_LEN*8;
  private static final int NUM_SECTIONS = Tunnel.GRID_LENGTH/8;
  private static final float ROLL_MULT = 3;
  private static final class Section {
    float distanceLeft;
    float roll, pitch, yaw;

    private static final float SUDDEN_RESET_PROB = 0.01f;

    Section() {
      distanceLeft = SECTION_LEN;
      roll = pitch = yaw = 0;
    }

    Section mutate(float violence) {
      float mutation = Math.min(100, violence)/400;
      float maxValue = Math.max(20, 200/(100-Math.min(99,violence)));
      Section sec = new Section();
      if (Math.random() < SUDDEN_RESET_PROB)
        return sec;

      sec.roll = roll + (float)(Math.random() - 0.5)*mutation;
      sec.pitch= pitch+ (float)(Math.random() - 0.5)*mutation;
      sec.yaw  = yaw  + (float)(Math.random() - 0.5)*mutation;

      if (sec.roll < -maxValue) sec.roll = -maxValue;
      if (sec.roll > maxValue) sec.roll = maxValue;
      if (sec.pitch < -maxValue) sec.pitch = -maxValue;
      if (sec.pitch > maxValue) sec.pitch = maxValue;
      if (sec.yaw < -maxValue) sec.yaw = -maxValue;
      if (sec.yaw > maxValue) sec.yaw = maxValue;
      return sec;
    }
  }

  private final LinkedList<Section> sections = new LinkedList<Section>();

  public Distortion() {
    sections.add(new Section());
    refill(2.0f);
  }

  /**
   * Updates the Distortion, so that the given reference object will be at the
   * given Z offset.
   */
  public void translateZ(GameObject ref, float offset, float violence) {
    float off = -(ref.getZ() - offset);
    while (sections.getFirst().distanceLeft < off)
      sections.removeFirst();

    sections.getFirst().distanceLeft -= off;
    refill(violence);
  }

  private static float cos(float f) { return (float)Math.cos(f); }
  private static float sin(float f) { return (float)Math.sin(f); }

  private void xform(float[] vec) {
    float x = vec[0], y = vec[1], z = vec[2];
    float dist = -z;
    //Move x any y into zero-centred coordinates for transformation
    x -= 0.5f;
    y -= 0.5f; //Not vheight because this must be circular

    //We need to apply the sections in reverse order;
    //start by finding the last one to apply
    ListIterator<Section> it = sections.listIterator();
    float distLeft = dist;
    while (it.hasNext() && distLeft > 0)
      distLeft -= it.next().distanceLeft;

    //Transform
    while (it.hasPrevious()) {
      Section s = it.previous();
      float ad; //applied distance
      /* If distLeft is negative, the last segment only applies to a portion of
       * the distance for that section. Otherwise, it applies to the whole
       * section.
       */
      if (distLeft <= 0) {
        ad = s.distanceLeft + distLeft;
        distLeft = 1;
      } else {
        ad = s.distanceLeft;
      }

      x = x*cos(s.yaw*ad)*cos(s.roll*ad*ROLL_MULT)
        - y*sin(s.roll*ad*ROLL_MULT)
        - z*sin(s.yaw*ad);
      y = y*cos(s.roll*ad*ROLL_MULT)*cos(s.pitch*ad)
        - z*sin(s.pitch*ad)
        + x*sin(s.roll*ad*ROLL_MULT);
      z = z*cos(s.yaw*ad)*cos(s.pitch*ad)
        + x*sin(s.yaw*ad)
        + y*sin(s.yaw*ad);
    }

    //Done
    vec[0] = x+0.5f;
    vec[1] = y+0.5f;
    vec[2] = z;
  }

  /**
   * Transforms the given vertex, then draws it using glVertex3f.
   */
  public void v(GL2 gl, float x, float y, float z) {
    float[] v = new float[] { x,y,z };
    xform(v);

    gl.glVertex3f(v[0], v[1], v[2]);
  }

  private static float len(float[] v) {
    return (float)Math.sqrt(v[0]*v[0] + v[1]*v[1] + v[2]*v[2]);
  }
  private static float dot(float[] a, float[] b) {
    return a[0]*b[0] + a[1]*b[1] + a[2]*b[2];
  }

  /**
   * Adds an appropriate translation transform to the current GL matrix.
   */
  public void t(GL2 gl, float x, float y, float z) {
    float v[] = new float[] { x,y,z };
    xform(v);
    gl.glTranslatef(v[0], v[1], v[2]);

    //We also need to rotate the object so "up" is still correct
    //Project a (0,1,z) vector to the same Z coordinate, then subtract out what
    //projecting (0,0,z) gives us. The result is the "up" vector, which must be
    //rotated wrt the expected (0,1,0).
    //The axis is the cross product between them, and the angle can be derived
    //from the dot product.
    float base[] = new float[] { 0,0,z };
    float up  [] = new float[] { 0,1,z };
    xform(base);
    xform(up);
    for (int i = 0; i < 3; ++i) up[i] -= base[i];

    float cross[] = new float[] {
      1*up[2] - 0*up[1],
      0*up[2] - 0*up[0],
      0*up[1] - 1*up[0],
    };
    float dot = up[1]; //Dotted with (0,1,0)
    float angle = (float)acos(dot /* up is a unit vector */);
    //Convert angle to degrees for OpenGL
    angle *= 360.0f / 2.0f / (float)Math.PI;
    gl.glRotatef(angle, cross[0], cross[1], cross[2]);
  }

  private void refill(float violence) {
    while (sections.size() < NUM_SECTIONS)
      sections.add(sections.getLast().mutate(violence));
  }
}
