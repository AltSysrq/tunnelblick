package com.servegame.abendstern.tunnelblick.game;

public class Hurtle extends ModelledObject {
  private static final float H = 0.125f, T = 0.1f;
  private static final float[] MODEL = {
    1, 0, 0, 0,

    0, 0*T, 0, 0,
    0, 1*T, H, 0,
    0, 2*T, 0, 0,

    0, 2*T, 0, 0,
    0, 3*T, H, 0,
    0, 4*T, 0, 0,

    0, 4*T, 0, 0,
    0, 5*T, H, 0,
    0, 6*T, 0, 0,

    0, 6*T, 0, 0,
    0, 7*T, H, 0,
    0, 8*T, 0, 0,

    0, 8*T, 0, 0,
    0, 9*T, H, 0,
    0, 10*T, 0, 0,

    1, 1, 0, 0,
    0, 0, H, 0,
    0, 1, H, 0,
    0, 0, H, -0.05f,

    0, 1, H, 0,
    0, 0, H, -0.05f,
    0, 1, H, -0.05f,
  };
  static { normalise(MODEL); }

  public Hurtle(Tunnelblick tb) {
    super(tb.field, 0.5f, H/2, -tb.getSpawnDistance(),
          MODEL, tb.distortion);
  }

  public void update(float et) {}
  public void collideWith(GameObject o) {}
  public void collideWithPlayer(Player p) { p.kill(); }
}
