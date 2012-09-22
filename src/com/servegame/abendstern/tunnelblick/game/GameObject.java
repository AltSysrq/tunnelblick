package com.servegame.abendstern.tunnelblick.game;

import javax.media.opengl.*;

/**
 * Encapuslates the basic data and operations for arbitrary objects within the
 * game.
 */
public abstract class GameObject {
  //Coordinates and dimensions
  protected float x, y, z, w, h, l;
  /**
   * Whether the object is currently "alive".
   * When set to false, the object will be removed from the field after the
   * next update.
   */
  protected boolean alive = true;
  /**
   * The GameField in which this object lives.
   */
  protected final GameField field;

  /**
   * Constructs a new GameObject in the given field, at the given position, and
   * with the given dimensions.
   *
   * @param field The GameField in which the object lives
   * @param x The initial X coordinate
   * @param y The initial Y coordinate
   * @param z The initial Z coordinate
   * @param w The width of the object (X dimension)
   * @param h The height of the object (Y dimension)
   * @param l The length of the object (Z dimension)
   */
  protected GameObject(GameField field,
                       float x, float y, float z,
                       float w, float h, float l) {
    this.field = field;
    this.x = x;
    this.y = y;
    this.z = z;
    this.w = w;
    this.h = h;
    this.l = l;
  }

  /**
   * Performs any updating necessary for this object.
   *
   * @param elapsedTime The amount of time, in seconds, since the last call to
   * update().
   */
  public abstract void update(float elapsedTime);
  /**
   * Draws the object.
   */
  public abstract void draw(GL2 gl);
  /**
   * Notifies the object that it has collided with the given other GameObject.
   */
  public abstract void collideWith(GameObject other);

  /** Returns whether the object is currently alive. */
  public final boolean isAlive() { return alive; }
  /** Returns the X coordinate of this object */
  public final float getX() { return x; }
  /** Returns the Y coordinate of this object */
  public final float getY() { return y; }
  /** Returns the Z coordinate of this object */
  public final float getZ() { return z; }
  /** Returns the width (X dimension) of this object */
  public final float getW() { return w; }
  /** Returns the height (Y dimension) of this object */
  public final float getH() { return h; }
  /** Returns the length (Z dimension) of this object */
  public final float getL() { return l; }

  /**
   * Attempts to move to the given new coordinates. If force is false, the
   * movement only takes place if the new location is unoccupied. If force is
   * true, the movement will alway occur, triggering a collision with anything
   * that is already there.
   *
   * @param x The new X coordinate to which to try to move
   * @param y The new Y coordinate to which to try to move
   * @param z The new Z coordinate to which to try to move
   * @param force Whether to move even if a collision occurs
   * @return Whether any movement actually occurred.
   */
  protected final boolean moveTo(float x, float y, float z, boolean force) {
    for (GameObject that: field) {
      if (that != this) {
        if (overlap(x, w, that.x, that.w) &&
            overlap(y, h, that.y, that.h) &&
            overlap(z, l, that.z, that.l)) {
          if (force) {
            this.collideWith(that);
            that.collideWith(this);
          } else {
            return false;
          }
        }
      }
    }

    this.x = x;
    this.y = y;
    this.z = z;

    return true;
  }

  private static boolean overlap(float x0, float w0, float x1, float w1) {
    return Math.abs(x0-x1) < (w0 + w1) / 2.0f;
  }
}
