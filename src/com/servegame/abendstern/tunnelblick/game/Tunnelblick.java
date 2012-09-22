package com.servegame.abendstern.tunnelblick.game;

import java.util.*;
import javax.media.opengl.*;

import com.servegame.abendstern.tunnelblick.backend.*;

public class Tunnelblick extends GameState {
  public final GameField field = new GameField();
  public final Tunnel tunnel = new Tunnel();
  public final Distortion distortion = new Distortion();
  private Player player;
  public final GameManager man;

  private EnemyFactory factory = new EnemyFactory(this);

  public Tunnelblick(GameManager man) {
    this.man = man;
    player = new Player(this);
    field.add(player);
  }

  protected GameState updateThis(float et) {
    factory.update(et, player.getSpeed());
    field.update(et);
    tunnel.update(et);
    float offset = -getNearClippingPlane() - player.getL()/2;
    tunnel.translateZ(player, offset);
    field.translateZ(player, offset);
    return this;
  }

  protected void drawThis(GL2 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    configureGL(gl);
    tunnel.draw(gl, distortion);
    field.draw(gl);
  }

  protected void receiveInputThis(InputEvent evt) {
    player.receiveInput(evt);
  }

  public void configureGL(GL2 gl) {
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
    gl.glFrustumf(-1, 1, -man.vheight(), man.vheight(),
                  getNearClippingPlane(), 128.0f);
    gl.glScalef(2.0f, 2.0f, 1);
    gl.glTranslatef(-0.5f, -man.vheight()/2.0f, 0);
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();
  }

  private float getNearClippingPlane() {
    return 1.5f/player.getSpeed();
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

    man.setState(new Tunnelblick(man));
    man.run();
    man.destroy();
  }
}
