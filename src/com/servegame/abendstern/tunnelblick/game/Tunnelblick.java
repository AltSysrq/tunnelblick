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
  private final MusicPlayer music;

  private int score = 0;
  private float totalDistance = 0;

  private EnemyFactory factory = new EnemyFactory(this);

  //Inverse portion faded out after the player is dead; fades out at
  //FADE_OUT_RATE
  private float fadeOut = 1;
  private static final float FADE_OUT_RATE = 0.3f;

  public Tunnelblick(GameManager man) {
    this.man = man;
    player = new Player(this);
    field.add(player);
    music = new MusicPlayer(distortion);
    man.getAudioPlayer().addSource(music, (short)0x7FFF);
  }

  protected GameState updateThis(float et) {
    music.update(et);
    factory.update(et, player.getSpeed());
    field.update(et);
    tunnel.update(et);
    float offset = -getNearClippingPlane() - player.getL()/2;
    totalDistance += -player.getZ() + offset;
    distortion.translateZ(player, offset, player.getSpeed());
    tunnel.translateZ(player, offset);
    field.translateZ(player, offset);

    if (player.isAlive()) {
      return this;
    } else if (fadeOut > 0) {
      fadeOut -= FADE_OUT_RATE*et;
      return this;
    } else {
      //TODO: Better end-of-game
      System.out.println("Game over!");
      System.out.println("Your score: " + score);
      System.out.println("Your speed: " + player.getSpeed());
      System.out.println("Your distance: " + totalDistance);
      return null;
    }
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
    gl.glEnable(GL2.GL_FOG);
    gl.glFogi(GL2.GL_FOG_MODE, GL2.GL_LINEAR);
    gl.glFogf(GL2.GL_FOG_DENSITY, 0.25f);
    gl.glFogf(GL2.GL_FOG_START, Math.max(getNearClippingPlane(), fadeOut*1.0f));
    gl.glFogf(GL2.GL_FOG_END, Math.max(1.1f, fadeOut*1.1f*getSpawnDistance()));
    gl.glMatrixMode(GL2.GL_PROJECTION);
    gl.glLoadIdentity();
    gl.glFrustumf(-1, 1, -man.vheight(), man.vheight(),
                  getNearClippingPlane(), 128.0f);
    gl.glScalef(2.0f, 2.0f, 1);
    gl.glTranslatef(-0.5f, -man.vheight()/2.0f, 0);
    gl.glMatrixMode(GL2.GL_MODELVIEW);
    gl.glLoadIdentity();
  }

  /**
   * Returns the distance at which objects should spawn.
   */
  public float getSpawnDistance() {
    return Math.min(8*player.getSpeed(), Tunnel.GRID_LENGTH*Tunnel.GSQ_LEN/2);
  }

  /**
   * Returns the player object itself.
   */
  public Player getPlayer() { return player; }

  /**
   * Returns the X coordinate of the player (so enemies won't spawn in the line
   * of fire).
   */
  public float getPlayerX() { return player.getX(); }

  private float getNearClippingPlane() {
    return 1.5f/player.getSpeed();
  }

  /**
   * Adds the given amount to the player's score.
   */
  public void addScore(int amt) {
    score += amt;
  }

  public static void main(String[] args) {
    OpenNiBodyTrackerInputDriver onbtid = new OpenNiBodyTrackerInputDriver(true);
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
    man.installInputDriver(onbtid);

    man.setState(new Tunnelblick(man));
    man.run();
    man.destroy();
  }
}
