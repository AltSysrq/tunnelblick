package com.servegame.abendstern.tunnelblick.game;

import javax.media.opengl.*;

/**
 * Transforms the world according to the logical z coordinate, to make the
 * tunnel more interesting.
 */
public final class Distortion {
  /**
   * Transforms the given vertex, then draws it using glVertex3f.
   */
  public void v(GL2 gl, float x, float y, float z) {
    gl.glVertex3f(x, y, z);
  }
}
