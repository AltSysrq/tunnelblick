package com.servegame.abendstern.tunnelblick.game;

import java.util.*;
import static java.lang.Math.*;

public class FireFern extends Enemy {
  private static final float[] MODEL = generateModel();

  private static final float ROT_RATE = 90;

  public FireFern(Tunnelblick tb) {
    super(tb, MODEL, 4);
  }

  public void update(float et) {
    rotation += ROT_RATE * et;
    if (rotation > 360) rotation -= 360;
  }

  private static final float HEIGHT = 0.35f;
  private static final float IHEIGHT = HEIGHT/3;
  private static final float RADIUS = 0.15f;
  private static final float IRADIUS = RADIUS/6;
  private static final int DIVS = 5;
  private static float[] generateModel() {
    final ArrayList<Float> list = new ArrayList<Float>();
    class V {
      //These methods take doubles and cast them back to floats because Java
      //for some reason doesn't have float versions of cos() and family like
      //even C99 does.
      public void c(double r, double g, double b) {
        list.add((float)1);
        list.add((float)r);
        list.add((float)g);
        list.add((float)b);
      }
      public void v(double x, double y, double z) {
        list.add((float)0);
        list.add((float)x);
        list.add((float)y);
        list.add((float)z);
      }
    }
    V v = new V();
    for (int i = 0; i < DIVS; ++i) {
      float th0 = (float)(i*2*PI/DIVS), th1 = (float)((i+1)*2*PI/DIVS);
      float thm = (th1+th0)/2;

      v.c(0.8f,0,0);
      v.v(cos(th0)*IRADIUS, 0, sin(th0)*IRADIUS);
      v.v(0, IHEIGHT, 0);
      v.c(1,0.5f,0);
      v.v(cos(thm)*RADIUS, HEIGHT, sin(thm)*RADIUS);

      v.c(0.8f,0,0);
      v.v(cos(th1)*IRADIUS, 0, sin(th1)*IRADIUS);
      v.v(0, IHEIGHT, 0);
      v.c(1,0.5f,0);
      v.v(cos(thm)*RADIUS, HEIGHT, sin(thm)*RADIUS);

      v.c(0.8f,0,0);
      v.v(cos(th1)*IRADIUS, 0, sin(th1)*IRADIUS);
      v.v(cos(th0)*IRADIUS, 0, sin(th0)*IRADIUS);
      v.c(1,0.5f,0);
      v.v(cos(thm)*RADIUS, HEIGHT, sin(thm)*RADIUS);
    }

    //Convert into normalised array
    float[] ret = new float[list.size()];
    for (int i = 0; i < ret.length; ++i)
      ret[i] = list.get(i);
    normalise(ret);
    return ret;
  }

  protected float getColourR() { return 0.8f; }
  protected float getColourG() { return 0.1f; }
  protected float getColourB() { return -0.5f; }
  protected float getPulseSpeed() { return 12; }
  protected int getAward() { return 250; }
}
