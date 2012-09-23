package com.servegame.abendstern.tunnelblick.game;

public class Bat extends Enemy {
  private static final float BHW = 0.025f, BHH = 0.015f, BHL = 0.035f;
  private static final float HWS = 0.325f;
  private static final float[] FLYING_MODEL = {
    1, 1, 0, 1,
    0, -BHW, -BHH, -BHL,
    0, +BHW, -BHH, -BHL,
    0, +BHW, +BHH, -BHL,

    0, -BHW, -BHH, -BHL,
    0, +BHW, +BHH, -BHL,
    0, -BHW, +BHH, -BHL,

    0, -BHW, -BHH, +BHL,
    0, +BHW, -BHH, +BHL,
    0, +BHW, +BHH, +BHL,

    0, -BHW, -BHH, +BHL,
    0, +BHW, +BHH, +BHL,
    0, -BHW, +BHH, +BHL,

    0, -BHW, -BHH, -BHL,
    0, -BHW, +BHH, -BHL,
    0, -BHW, +BHH, +BHL,

    0, -BHW, -BHH, -BHL,
    0, -BHW, +BHH, +BHL,
    0, -BHW, -BHH, +BHL,

    0, +BHW, -BHH, -BHL,
    0, +BHW, +BHH, -BHL,
    0, +BHW, +BHH, +BHL,

    0, +BHW, -BHH, -BHL,
    0, +BHW, +BHH, +BHL,
    0, +BHW, -BHH, +BHL,

    0, -BHW, -BHH, -BHL,
    0, +BHW, -BHH, -BHL,
    0, +BHW, -BHH, +BHL,

    0, -BHW, -BHH, -BHL,
    0, +BHW, -BHH, +BHL,
    0, -BHL, -BHH, +BHL,

    0, -BHW, +BHH, -BHL,
    0, +BHW, +BHH, -BHL,
    0, +BHW, +BHH, +BHL,

    0, -BHW, +BHH, -BHL,
    0, +BHW, +BHH, +BHL,
    0, -BHL, +BHH, +BHL,

    0, 0, -BHH, +BHL,
    0, 0, +BHH, -BHL,
    1, 0, 0, 0,
    0, -HWS, 0, 1.2f*BHL,

    0, +HWS, 0, 1.2f*BHL,
    1, 1, 0, 1,
    0, 0, +BHH, -BHL,
    0, 0, -BHH, +BHL,
  };

  private static final float FHH = 0.1f;
  private static final float FHW = 0.075f;
  private static final float[] FALLING_MODEL = {
    1, 0, 0, 0,
    0, -FHW, 0, -FHW,
    0, 0, +FHH, 0,
    0, +FHW, 0, -FHW,

    0, -FHW, 0, -FHW,
    0, 0, +FHH, 0,
    0, -FHW, 0, +FHW,

    0, +FHW, 0, -FHW,
    0, 0, +FHH, 0,
    0, +FHW, 0, +FHW,

    0, -FHW, 0, +FHW,
    0, 0, +FHH, 0,
    0, +FHW, 0, +FHW,

    0, -FHW, 0, -FHW,
    0, 0, -FHH, 0,
    0, +FHW, 0, -FHW,

    0, -FHW, 0, -FHW,
    0, 0, -FHH, 0,
    0, -FHW, 0, +FHW,

    0, +FHW, 0, -FHW,
    0, 0, -FHH, 0,
    0, +FHW, 0, +FHW,

    0, -FHW, 0, +FHW,
    0, 0, -FHH, 0,
    0, +FHW, 0, +FHW,
  };

  static {
    normalise(FLYING_MODEL);
    normalise(FALLING_MODEL);
  }

  private boolean isFlying = true;
  private Player player;
  private float vy, vz;
  private static final float GRAVITY = -10.0f;
  private static final float XSPEED = 0.4f;
  private static final float DROP_THRESH = 4;

  public Bat(Tunnelblick tb) {
    super(tb, FLYING_MODEL, 1);

    player = tb.getPlayer();
    vz = -tb.getPlayer().getSpeed()/3;
    y = 0.4f + h/2;
  }

  public void update(float et) {
    if (isFlying) {
      //Fall to the ground if close enough to the player
      if (-z < DROP_THRESH) {
        isFlying = false;
        setModel(FALLING_MODEL);
      } else {
        //Stay in the air and try to follow the player's X
        float newx;
        if (Math.abs(x - player.getX()) < XSPEED*et)
          newx = player.getX();
        else if (x < player.getX())
          newx = x + XSPEED*et;
        else
          newx = x - XSPEED*et;

        moveTo(newx, y, z + vz*et, false);
      }
    } else {
      //Fall to the ground
      vy += GRAVITY*et;
      moveTo(x, y + vy*et, z, true);
    }
  }

  public void collideWithProjectile(Projectile p) {
    //Only vulnerable if not flying
    if (isFlying) super.collideWithProjectile(p);
  }

  protected float getColourR() { return 1; }
  protected float getColourG() { return -1; }
  protected float getColourB() { return 1; }
  protected float getPulseSpeed() { return 20; }
  protected int getAward() { return 750; }
}
