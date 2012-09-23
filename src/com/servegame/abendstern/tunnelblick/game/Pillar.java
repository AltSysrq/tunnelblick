package com.servegame.abendstern.tunnelblick.game;

/**
 * The Pillar is an obstacle which kills the player on contact and is
 * indestructable. It is occasionally spawned in the player's path to force the
 * player to move around.
 */
public class Pillar extends ModelledObject {
  private static final float HH = 0.5f, R = 0.1f;
  private static final float[] MODEL = {
    1, 1, 1, 1,

    0, -R, -HH, -R,
    0, -R, +HH, -R,
    0, -R, +HH, +R,

    0, -R, +HH, +R,
    0, -R, -HH, -R,
    0, -R, -HH, +R,

    0, -R, -HH, -R,
    0, -R, +HH, -R,
    0, +R, +HH, -R,

    0, +R, +HH, -R,
    0, -R, -HH, -R,
    0, +R, -HH, -R,

    0, +R, -HH, -R,
    0, +R, +HH, -R,
    0, +R, +HH, +R,

    0, +R, +HH, +R,
    0, +R, -HH, -R,
    0, +R, -HH, +R,

    0, -R, -HH, +R,
    0, -R, +HH, +R,
    0, +R, +HH, +R,

    0, +R, +HH, +R,
    0, -R, -HH, +R,
    0, +R, -HH, +R,
  };

  public Pillar(Tunnelblick tb) {
    super(tb.field, tb.getPlayerX(), HH, -tb.getSpawnDistance(),
          MODEL, tb.distortion);
  }

  public void update(float et) {
    //Avoid getting too close to the screen
    if (-z < 1.0f) alive = false;
  }
  public void collideWith(GameObject go) {}
  public void collideWithPlayer(Player p) { p.kill(); }
}
