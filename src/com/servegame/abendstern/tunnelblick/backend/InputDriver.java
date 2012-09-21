package com.servegame.abendstern.tunnelblick.backend;

/**
 * Describes an object which can convert external stimuli into InputEvents.
 */
public interface InputDriver {
  /**
   * Updates the driver and pumps all available input events into the given
   * receiver.
   */
  public void pumpInput(InputReceiver dst);
}
