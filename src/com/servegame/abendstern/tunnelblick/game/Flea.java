package com.servegame.abendstern.tunnelblick.game;

public class Flea extends Enemy {
  private static final float HW = 0.15f, H = 0.275f, LH = 0.1f, L = 0.25f;
  private static final float[] MODEL = {
    1, 1, 1, 0,
    0, -HW, 0, 0,
    0, +HW, 0, 0,
    1, 1, 0.5f, 0,
    0, 0, H, 0,

    0, 0, H, 0,
    1, 1, 1, 0,
    0, -HW, 0, 0,
    0, 0, 0, -L,

    0, 0, 0, -L,
    0, -HW, 0, 0,
    1, 1, 0.5f, 0,
    0, 0, H, 0,

    1, 1, 1, 0,
    0, -HW, 0, 0,
    0, +HW, 0, 0,
    0, 0, 0, L,

    1, 0, 0, 0,
    0, -HW*4/6, 0, 0,
    0, -HW*3/6, -LH, 0,
    0, -HW*2/6, 0, 0,
    0, +HW*4/6, 0, 0,
    0, +HW*3/6, -LH, 0,
    0, +HW*2/6, 0, 0,

    0, -HW*4/6, 0, L/3,
    0, -HW*3/6, -LH, L/3,
    0, -HW*2/6, 0, L/3,
    0, +HW*4/6, 0, L/3,
    0, +HW*3/6, -LH, L/3,
    0, +HW*2/6, 0, L/3,

    0, -HW*4/6, 0, L*2/3,
    0, -HW*3/6, -LH, L*2/3,
    0, -HW*2/6, 0, L*2/3,
    0, +HW*4/6, 0, L*2/3,
    0, +HW*3/6, -LH, L*2/3,
    0, +HW*2/6, 0, L*2/3,
  };
  static { normalise(MODEL); }

  private float vz, vy;
  private static final float GRAVITY = -4.5f;
  private boolean blocked = false;
  public Flea(Tunnelblick tb) {
    super(tb, MODEL, 7);
    vz = -tb.getPlayer().getSpeed() / 2;
    vy = 0;
  }

  public void update(float et) {
    vy += GRAVITY*et;
    //Move to new location.
    //See how far we moved in the Y direction; if it is less than expected,
    //maybe jump
    float newy = y + vy*et;
    blocked = !moveTo(x, newy, blocked? z : z + vz*et, blocked);
    if (blocked || newy != y)
      vy = +2.2f * (float)Math.random();
  }

  protected float getColourR() { return 1; }
  protected float getColourG() { return 1; }
  protected float getColourB() { return -0.3f; }
  protected float getPulseSpeed() { return 22; }
  protected int getAward() { return 1000; }
}
