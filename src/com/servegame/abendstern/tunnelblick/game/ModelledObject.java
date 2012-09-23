package com.servegame.abendstern.tunnelblick.game;

import javax.media.opengl.*;

/**
 * Encapsulates a GameObject which is drawn according to a non-changing
 * model. Collision boundaries are automatically calculated, and this class
 * handles drawing.
 *
 * A model is simply an array of floats whose length is a multiple of four. The
 * first float in each quadruple is zero if that quadruple is a vertex, or
 * a colour otherwise. The other three are (x,y,z) coordinates or an (r,g,b)
 * colour, as determined by the first.
 */
public abstract class ModelledObject extends GameObject {
  private float[] model;
  protected final Distortion distortion;
  /**
   * The around-centre-Z rotation of the model, in degrees.
   */
  protected float rotation = 0;

  protected ModelledObject(GameField field, float x, float y, float z,
                           float[] model, Distortion distortion) {
    super(field, x, y, z, 0,0,0);

    setModel(model);
    this.distortion = distortion;
  }

  /**
   * Changes the model being used.
   *
   * This includes recalculating collision boundaries.
   */
  protected void setModel(float[] model) {
    float maxx = -1.0f/0.0f,
          maxy = -1.0f/0.0f,
          maxz = -1.0f/0.0f;

    for (int i = 0; i < model.length; i += 4) {
      if (model[i] == 0) {
        maxx = Math.max(maxx, Math.abs(model[i+1]));
        maxy = Math.max(maxy, Math.abs(model[i+2]));
        maxz = Math.max(maxz, Math.abs(model[i+3]));
      }
    }

    w = maxx*2;
    h = maxy*2;
    l = maxz*2;
    this.model = model;
  }

  public void draw(GL2 gl) {
    gl.glPushMatrix();
    distortion.t(gl, x, y, z);
    gl.glRotatef(rotation, 0, 0, 1);
    gl.glBegin(GL.GL_TRIANGLES);
    for (int i = 0; i < model.length; i += 4) {
      if (model[i] == 0)
        gl.glVertex3f(model[i+1], model[i+2], model[i+3]);
      else
        gl.glColor3f(model[i+1], model[i+2], model[i+3]);
    }
    gl.glEnd();
    gl.glPopMatrix();
  }

  /**
   * Normalises the given model so that it fits in its bounding box correctly.
   */
  public static void normalise(float[] model) {
    float minima[] = new float[] { Float.POSITIVE_INFINITY,
                                   Float.POSITIVE_INFINITY,
                                   Float.POSITIVE_INFINITY };
    float maxima[] = new float[] { Float.NEGATIVE_INFINITY,
                                   Float.NEGATIVE_INFINITY,
                                   Float.NEGATIVE_INFINITY };

    for (int i = 0; i < model.length; i += 4) {
      if (model[i] == 0) {
        for (int j = 0; j < 3; ++j) {
          minima[j] = Math.min(minima[j], model[i+1+j]);
          maxima[j] = Math.max(maxima[j], model[i+1+j]);
        }
      }
    }

    for (int i = 0; i < model.length; i += 4) {
      if (model[i] == 0) {
        for (int j = 0; j < 3; ++j) {
          model[i+1+j] -= (maxima[j] + minima[j])/2;
        }
      }
    }
  }
}
