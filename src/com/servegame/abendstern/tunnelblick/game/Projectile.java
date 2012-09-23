package com.servegame.abendstern.tunnelblick.game;

public class Projectile extends ModelledObject {
  private static final float R = 0.015f;
  private static final float[] MODEL = {
    1, 1, 1, 0,

    0, 0,+R, 0,
    0,+R, 0, 0,
    0, 0, 0,+R,

    0, 0,+R, 0,
    0,+R, 0, 0,
    0, 0, 0,-R,

    0, 0,+R, 0,
    0,-R, 0, 0,
    0, 0, 0,+R,

    0, 0,+R, 0,
    0,-R, 0, 0,
    0, 0, 0,-R,

    0, 0,-R, 0,
    0,+R, 0, 0,
    0, 0, 0,+R,

    0, 0,-R, 0,
    0,+R, 0, 0,
    0, 0, 0,-R,

    0, 0,-R, 0,
    0,-R, 0, 0,
    0, 0, 0,+R,

    0, 0,-R, 0,
    0,-R, 0, 0,
    0, 0, 0,-R,
  };

  private static final float SPEED = 10;

  private GameObject owner;
  private float vz;
  public Projectile(GameField field, GameObject owner,
                    float x, float y, float z,
                    float direction, Distortion distortion) {
    super(field, x, y, z, MODEL, distortion);
    this.owner = owner;
    vz = direction * SPEED;
  }

  public void update(float et) {
    moveTo(x, y, z + vz*et, true);

    if (-z > Tunnel.GRID_LENGTH*Tunnel.GSQ_LEN/2)
      alive = false;
  }

  public void collideWith(GameObject other) {
    if (!alive) return; //Don't affect more than one object
    if (other == owner) return; //Don't hit the object that launched us

    other.collideWithProjectile(this);
    alive = false;
  }
}
