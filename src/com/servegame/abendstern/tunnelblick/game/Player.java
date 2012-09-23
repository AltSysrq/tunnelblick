package com.servegame.abendstern.tunnelblick.game;

import javax.media.opengl.*;
import com.servegame.abendstern.tunnelblick.backend.InputEvent;
import com.servegame.abendstern.tunnelblick.backend.SoundEffect;

public class Player extends ModelledObject {
  private static final float HW = 0.1f, HH = 0.075f, FZ = -0.3f, BZ = 0.05f;
  private static final float[] MODEL = {
    1,  0,  0,  0,
    0,  0,  0, BZ,
    1,  0,  0, 0.2f,
    0, HW,  0,  0,
    0,  0, HH,  0,

    0,  0, HH,  0,
    0,-HW,  0,  0,
    1,  0,  0,  0,
    0,  0,  0, BZ,

    0,  0,  0, BZ,
    1,  0,  0,0.2f,
    0,-HW,  0,  0,
    0,  0,-HH,  0,

    0,  0,-HH,  0,
    0, HW,  0,  0,
    1,  0,  0,  0,
    0,  0,  0, BZ,

    1,  1,  1,  1,
    0,  0,  0, FZ,
    1,  0,  0, 0.2f,
    0, HW,  0,  0,
    0,  0, HH,  0,

    1,  1,  1,  1,
    0,  0,  0, FZ,
    1,  0,  0, 0.2f,
    0,  0, HH,  0,
    0,-HW,  0,  0,

    1,  1,  1,  1,
    0,  0,  0, FZ,
    1,  0,  0, 0.2f,
    0,-HW,  0,  0,
    0,  0,-HH,  0,

    1,  1,  1,  1,
    0,  0,  0, FZ,
    1,  0,  0, 0.2f,
    0,  0,-HH,  0,
    0, HW,  0,  0,
  };
  static { normalise(MODEL); }

  float vy = 0, vz = -2.0f;
  private static final float GRAVITY = -5.0f, ACCEL = -0.015f;

  private final Tunnelblick game;

  private static final float SHOT_INTERVAL = 0.3333f;
  private float timeUntilShot = SHOT_INTERVAL;

  public Player(Tunnelblick tb) {
    super(tb.field, 0.5f, 0, 0, MODEL, tb.distortion);
    this.game = tb;
    y = h/2;
  }

  public void update(float et) {
    vy += GRAVITY*et;
    vz += ACCEL*et;
    moveTo(x, y+vy*et, z+vz*et, true);

    timeUntilShot -= et;
    if (timeUntilShot <= 0) {
      timeUntilShot += SHOT_INTERVAL;

      game.field.add(new Projectile(game.field, this, x, y, z, vz,
                                    -1, game.distortion));
    }
  }

  public void collideWith(GameObject that) {
    that.collideWithPlayer(this);
  }

  /**
   * Marks this Player as dead.
   */
  public void kill() {
    alive = false;

    SoundEffect.play("sound/player_death.pcm", game.man.getAudioPlayer(),
                     (short)0x5000);
  }

  public void receiveInput(InputEvent e) {
    switch (e.type) {
    case InputEvent.TYPE_BODY_MOVEMENT:
      moveTo(e.x, y, z, true);
      break;

    case InputEvent.TYPE_GESTURE:
      switch (e.index) {
      case InputEvent.GESTURE_JUMP:
        //Set vy to upward velocity if close enough to the ground
        if (y - h*2/3 < 0)
          vy = 2.0f;
        break;
      }
      break;
    }
  }

  public float getSpeed() {
    return Math.abs(vz);
  }
}
