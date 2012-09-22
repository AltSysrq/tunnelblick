package com.servegame.abendstern.tunnelblick.game;

import javax.media.opengl.*;
import com.servegame.abendstern.tunnelblick.backend.InputEvent;

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
  private static final float GRAVITY = -1.0f, ACCEL = -0.01f;

  public Player(GameField field, Distortion distortion) {
    super(field, 0.5f, 0, 0, MODEL, distortion);
    y = h/2;
  }

  public void update(float et) {
    vy += GRAVITY*et;
    vz += ACCEL*et;
    moveTo(x, y+vy*et, z+vz*et, true);
  }

  public void collideWith(GameObject that) {
    alive = false;
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
          vy = 0.5f;
        break;
      }
      break;
    }
  }

  public float getSpeed() {
    return Math.abs(vz);
  }
}
