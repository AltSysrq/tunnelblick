package com.servegame.abendstern.tunnelblick.backend;

/**
 * Interface for clases which can process input events.
 */
public interface InputReceiver {
  /**
   * Processes the given InputEvent.
   */
  public void receiveInput(InputEvent e);
}
