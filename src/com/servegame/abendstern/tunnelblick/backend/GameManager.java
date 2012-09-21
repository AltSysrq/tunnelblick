package com.servegame.abendstern.tunnelblick.backend;

import java.awt.*;
import java.awt.event.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;

import java.io.Closeable;
import java.util.LinkedList;

/**
 * The GameManager manages resources and system compatibility, and orchestrates
 * interactions between different components.
 */
public final class GameManager implements Destroyable {
  private Frame frame;
  private GLProfile glProfile;
  private GLCapabilities glCapabilities;
  private GLCanvas glCanvas;
  private final LinkedList<Destroyable> resources =
    new LinkedList<Destroyable>();
  private GameState state;
  private final InputStatus inputStatus = new InputStatus();

  private final LinkedList<InputDriver> inputDrivers =
    new LinkedList<InputDriver>();

  //This is "deprecated", but documentation indicates it nonetheless fixes
  //stability problems on *NIX systems.
  static { GLProfile.initSingleton(true); }

  /**
   * Creates a GameManager using a window with the given title and dimensions.
   */
  public GameManager(String title, int width, int height) {
    glProfile = GLProfile.getDefault();
    glCapabilities = new GLCapabilities(glProfile);
    glCanvas = new GLCanvas(glCapabilities);

    frame = new Frame(title);
    frame.setSize(width, height);
    frame.setResizeable(false);
    frame.add(glCanvas);
    frame.setVisible(true);
  }

  public static void main(String[] argv) {
    new GameManager("Test", Integer.parseInt(argv[0]), Integer.parseInt(argv[1]));
  }
}
