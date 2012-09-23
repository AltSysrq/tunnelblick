package com.servegame.abendstern.tunnelblick.game;

import java.util.*;
import static java.lang.Math.*;

public class Cactus extends Enemy {
  private static final float[] MODEL = generateModel();

  public Cactus(Tunnelblick tb) {
    super(tb, MODEL, 1);
  }

  public void update(float et) {}

  private static final float RADIUS = 0.08f;
  private static final float BASE_RADIUS = 0.85f*RADIUS;
  private static final float HEIGHT = 0.3f;
  private static final int DIVS = 6;
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
      float th0 = (float)(i*2*PI/DIVS), th1 = (float)((i+1)%DIVS*2*PI/DIVS);

      //Generate the base (lower 1/3)
      v.c(0,0.5f,0);

      v.v(BASE_RADIUS*cos(th0), 0, BASE_RADIUS*sin(th0));
      v.v(BASE_RADIUS*cos(th1), 0, BASE_RADIUS*sin(th1));
      v.c(0,0.8f,0);
      v.v(RADIUS*cos(th0), HEIGHT/3, RADIUS*sin(th0));

      v.v(RADIUS*cos(th0), HEIGHT/3, RADIUS*sin(th0));
      v.v(RADIUS*cos(th1), HEIGHT/3, RADIUS*sin(th1));
      v.c(0,0.5f,0);
      v.v(BASE_RADIUS*cos(th1), 0, BASE_RADIUS*sin(th1));

      //Generate the middle
      v.c(0,0.8f,0);
      v.v(RADIUS*cos(th0), 1*HEIGHT/3, RADIUS*sin(th0));
      v.v(RADIUS*cos(th1), 1*HEIGHT/3, RADIUS*sin(th1));
      v.v(RADIUS*cos(th0), 2*HEIGHT/3, RADIUS*sin(th0));

      v.v(RADIUS*cos(th0), 2*HEIGHT/3, RADIUS*sin(th0));
      v.v(RADIUS*cos(th1), 2*HEIGHT/3, RADIUS*sin(th1));
      v.v(RADIUS*cos(th1), 1*HEIGHT/3, RADIUS*sin(th1));

      //Generate the top cone
      v.v(RADIUS*cos(th0), 2*HEIGHT/3, RADIUS*sin(th0));
      v.v(RADIUS*cos(th1), 2*HEIGHT/3, RADIUS*sin(th1));
      v.c(0.5f, 0.95f, 0.2f);
      v.v(0, HEIGHT, 0);
    }

    //Convert into normalised array
    float[] ret = new float[list.size()];
    for (int i = 0; i < ret.length; ++i)
      ret[i] = list.get(i);
    normalise(ret);
    return ret;
  }

  protected float getColourR() { return 0; }
  protected float getColourG() { return 0.8f; }
  protected float getColourB() { return 0; }
  protected float getPulseSpeed() { return 15.0f; }
  protected int getAward() { return 100; }
  protected String getDeathSound() { return "sound/cactus_death.pcm"; }
}
