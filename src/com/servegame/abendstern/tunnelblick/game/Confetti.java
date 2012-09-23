package com.servegame.abendstern.tunnelblick.game;

import javax.media.opengl.*;

import static java.lang.Math.*;

public class Confetti extends GameObject {
  private static final float COLOUR_VAR = 0.35f;
  private static final float GRAVITY = -2.5f;
  private static final float EXPV = 1.0f;
  private static final float EXPYBIAS = 1.65f;
  private static final float FRICTION = 0.5f;
  private static final float SZ = 0.1f;
  private static final int NUM_CONFETTI = 8;
  private static final class Confettus {
    float x, y, z, vx, vy, vz, rot, vrot, rx, ry, rz, r, g, b;
  };

  private final Confettus confetti[] = new Confettus[NUM_CONFETTI];
  private final Distortion distortion;

  private static float rand() { return 2*(float)(random() - 0.5f); }

  public Confetti(GameField field, float x, float y, float z,
                  float r, float g, float b,
                  Distortion dist) {
    super(field, x, y, z, 0, 0, 0);
    this.distortion = dist;

    for (int i = 0; i < confetti.length; ++i) {
      Confettus c = new Confettus();
      confetti[i] = c;

      c.x = 0;
      c.y = 0;
      c.z = 0;
      c.vx = rand()*EXPV;
      c.vy = rand()*EXPV + EXPYBIAS;
      c.vz = rand()*EXPV;
      c.rot = rand()*360;
      c.vrot = rand()*360*20;
      c.rx = rand();
      c.ry = rand();
      c.rz = rand();
      c.r = r + rand()*COLOUR_VAR;
      c.g = g + rand()*COLOUR_VAR;
      c.b = b + rand()*COLOUR_VAR;
    }
  }

  public void update(float et) {
    boolean anyAbovePlane = false;
    for (Confettus c: confetti) {
      c.x += c.vx * et;
      c.y += c.vy * et;
      c.z += c.vz * et;
      c.vy += GRAVITY*et;
      c.vx *= (float)pow(FRICTION, et);
      c.vy *= (float)pow(FRICTION, et);
      c.vz *= (float)pow(FRICTION, et);
      c.rot += c.vrot*et;

      anyAbovePlane |= y + c.y > 0;
    }

    alive = anyAbovePlane;
  }

  public void draw(GL2 gl) {
    gl.glPushMatrix();
    distortion.t(gl, x, y, z);
    for (Confettus c: confetti) {
      gl.glPushMatrix();
      gl.glTranslatef(c.x, c.y, c.z);
      gl.glRotatef(c.rot, c.rx, c.ry, c.rz);
      gl.glColor3f(c.r, c.g, c.b);
      gl.glBegin(GL2.GL_TRIANGLES);
      gl.glVertex2f(+SZ/2, 0);
      gl.glVertex2f(-SZ/2, +SZ/2);
      gl.glVertex2f(-SZ/2, -SZ/2);
      gl.glEnd();
      gl.glPopMatrix();
    }
    gl.glPopMatrix();
  }

  public void collideWith(GameObject go) {}
}
