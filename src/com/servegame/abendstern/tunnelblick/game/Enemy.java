package com.servegame.abendstern.tunnelblick.game;

import javax.media.opengl.*;

import com.servegame.abendstern.tunnelblick.backend.SoundEffect;

/**
 * Common base class for enemy objects.
 *
 * Enemies lose one hit point every time they are hit by a projectile, and die
 * when their hit points reach zero. At this point, a pair of pulses is added
 * to the tunnel, and the player is awarded points. They kill the player on
 * contact.
 */
public abstract class Enemy extends ModelledObject {
  private int hitPoints;
  private final Tunnelblick game;

  protected Enemy(Tunnelblick tb, float[] model, int hp) {
    super(tb.field, 0, 0, -tb.getSpawnDistance(), model,
          tb.distortion);
    this.hitPoints = hp;
    this.game = tb;
    //Select an X coordinate that doesn't place us within the line of fire.
    float x = (float)Math.random();
    float px = tb.getPlayerX();
    while (x+w/2+Projectile.RADIUS > px &&
           x-w/2-Projectile.RADIUS < px)
      x = (float)Math.random();
    moveTo(x, h/2, z, true);
  }

  public void collideWith(GameObject go) {}

  public void collideWithPlayer(Player p) {
    p.kill();
  }

  public void collideWithProjectile(Projectile p) {
    --hitPoints;
    if (hitPoints <= 0) {
      //Die
      alive = false;
      //Add score
      game.addScore(getAward());
      //Play sound
      SoundEffect.play(getDeathSound(), game.man.getAudioPlayer(),
                       (short)(0x7FFF / Math.min(1, -z)));
      //Spawn confetti
      field.add(new Confetti(field, x, y, z,
                             getColourR(), getColourG(), getColourB(),
                             distortion));
      //Create pulses
      int initialCol = (int)(x / Tunnel.GSQ_SZ);
      float r = getColourR(), g = getColourG(), b = getColourB();
      float speed = getPulseSpeed();
      //Reduce colour intensity since half is already the base
      r /= 2;
      g /= 2;
      b /= 2;

      game.tunnel.pulse(z, initialCol, r, g, b, -speed, 0);
      game.tunnel.pulse(z, initialCol, -r, -g, -b, speed, 0);
      for (int i = 1; initialCol-i >= 0; ++i) {
        game.tunnel.pulse(z, initialCol-i, r, g, b, -speed, i/speed);
        game.tunnel.pulse(z, initialCol-i, -r, -g, -b, speed, i/speed);
      }
      for (int i = 1; initialCol+i < Tunnel.GRID_WIDTH; ++i) {
        game.tunnel.pulse(z, initialCol+i, r, g, b, -speed, i/speed);
        game.tunnel.pulse(z, initialCol+i, -r, -g, -b, speed, i/speed);
      }
    }
  }

  /** Returns the red component of the predominant colour of this enemy. */
  protected abstract float getColourR();
  /** Returns the green component of the predominant colour of this enemy. */
  protected abstract float getColourG();
  /** Returns the blue component of the predominant colour of this enemy. */
  protected abstract float getColourB();
  /** Returns the speed of the pulses emitted by this enemy. */
  protected abstract float getPulseSpeed();
  /**
   * Returns the number of points awarded to the player for killing this
   * enemy.
   */
  protected abstract int getAward();
  /**
   * Returns the name of the audio file to play on death.
   */
  protected abstract String getDeathSound();
}
