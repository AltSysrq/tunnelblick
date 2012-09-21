package com.servegame.abendstern.tunnelblick.backend;

import javax.media.opengl.*;

/**
 * Encapsulates a "state" of the game.
 *
 * A state defines how status is updated, how graphics are drawn, and how input
 * is handled. A state may have substates, which are (by default) run instead
 * of the upper state when present; these subStates must use the same view
 * matrices as the upper ones.
 *
 * Subclasses do not need to be thread-safe; the game manager will ensure that
 * no more than one thread operates upon the root state at any given time. Note
 * that there is NO thread affinity --- there is no guarantee that any
 * particular set of invocations will occur on any particular threads.
 */
public abstract class GameState implements InputReceiver {
  /**
   * The current subordinate state, or null if none is present.
   */
  protected GameState subState = null;

  /**
   * Updates this state or its subordinate, given the time elapsed, in seconds.
   *
   * Subclasses will generally want to override updateThis() instead.
   *
   * @param elapsedTime The time that has passed (or that is to be assumed to
   * have passed) since the last call to update(), in seconds
   * @return The state to continue running, or null to terminate (ie, this to
   * keep running, another state to switch, null to terminate)
   */
  public GameState update(float elapsedTime) {
    if (subState != null) {
      subState = subState.update(elapsedTime);
      return this;
    } else {
      return this.updateThis(elapsedTime);
    }
  }

  /**
   * Performs updates specific to this state, when it has not subordinate.
   *
   * @param elapsedTime The time elapsed since the last call to update(), in
   * seconds (NOT since the last call to updateThis()!)
   * @return The state to continue running, or null to terminate.
   */
  protected abstract GameState updateThis(float elapsedTime);

  /**
   * Draws this state or its subordinate using the given OpenGL object.
   *
   * Most subclasses will want to implement drawThis() instead.
   *
   * The method MUST keep matrix pushes/pops balanced; the model matrix can be
   * assumed to be identity; the method MUST NOT leave the view matrix in a
   * modified state upon returning.
   */
  public void draw(GL2 gl) {
    if (subState != null)
      subState.draw(gl);
    else
      this.drawThis(gl);
  }

  /**
   * Performs drawing for this particular state.
   *
   * The method MUST keep matrix pushes/pops balanced; the model matrix can be
   * assumed to be identity; the method MUST NOT leave the view matrix in a
   * modified state upon returning.
   */
  protected abstract void drawThis(GL2 gl);

  /**
   * Configures the view matrix (and any other needed parms) to be appropriate
   * for this state. When called, the view matrix is an orthogonal projection
   * where X spans 0..1 left-to-right and Y spans 0..VHEIGHT, bottom-to-top.
   *
   * Default does nothing.
   */
  public void configureGL(GL2 gl) {}

  @Override
  public void receiveInput(InputEvent e) {
    if (subState != null)
      subState.receiveInput(e);
    else
      this.receiveInputThis(e);
  }

  /**
   * Processes an input event which occurred while this state had no
   * subordinate.
   *
   * This is generally preferable to overriding receiveInput() since it does
   * not need to worry about the hierarchy.
   *
   * Default does nothing.
   */
  protected void receiveInputThis(InputEvent e) {}
}
