package com.servegame.abendstern.tunnelblick.game;

import java.util.*;
import javax.media.opengl.*;

import com.servegame.abendstern.tunnelblick.backend.*;

public class Tunnelblick extends GameState {
  private GameField field = new GameField();
  private Tunnel tunnel = new Tunnel();

  protected GameState updateThis(float et) {
    field.update(et);
    tunnel.update(et);
    return this;
  }

  protected void drawThis(GL2 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT);
    tunnel.draw(gl);
    field.draw(gl);
  }

  public static void main(String[] args) {
    GameManager man = new GameManager("Tunnelblick", 800, 600);
    KeyboardGestureInputDriver kgid = new KeyboardGestureInputDriver();
    kgid.bind(java.awt.event.KeyEvent.VK_SPACE, InputEvent.GESTURE_JUMP);
    man.installInputDriver(kgid);
    MouseButtonGestureInputDriver mbgid = new MouseButtonGestureInputDriver();
    mbgid.bind(java.awt.event.MouseEvent.BUTTON1, InputEvent.GESTURE_JUMP);
    man.installInputDriver(mbgid);
    ModalMouseMotionInputDriver mmmid = new ModalMouseMotionInputDriver();
    mmmid.setPointerMode(false);
    man.installInputDriver(mmmid);

    man.setState(new Tunnelblick());
    man.run();
    man.destroy();
  }
}
