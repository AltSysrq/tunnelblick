package com.servegame.abendstern.tunnelblick.game;

import java.util.*;
import javax.media.opengl.*;

/**
 * Maintains a list of GameObjects in existence, and manages forwarding calls
 * to them, etc.
 */
public final class GameField implements Iterable<GameObject> {
  private final LinkedList<GameObject> objects = new LinkedList<GameObject>(),
                                       toSpawn = new LinkedList<GameObject>();

  /**
   * Updates all objects, then adds newly-spawned objects to the list.
   *
   * @param elapsedTime The time, in seconds, since the previous call to
   * update().
   */
  public void update(float elapsedTime) {
    for (ListIterator<GameObject> it = objects.listIterator();
         it.hasNext(); ) {
      GameObject go = it.next();
      go.update(elapsedTime);
      if (!go.isAlive())
        it.remove();
    }

    objects.addAll(toSpawn);
    toSpawn.clear();
  }

  /**
   * Draws all objects in the field.
   */
  public void draw(GL2 gl) {
    for (GameObject go: objects)
      go.draw(gl);
  }

  /**
   * Enqueues the given object to be spawned after the next update.
   */
  public void add(GameObject go) {
    toSpawn.add(go);
  }

  /**
   * Translates the Z coordinate of all objects such that the given reference
   * object will have a Z coordinate of offset.
   *
   * Objects which end up with a Z coordinate greater than +1 are destroyed
   * (since this is in front of the screen).
   */
  public void translateZ(GameObject reference, float offset) {
    for (ListIterator<GameObject> it = objects.listIterator();
         it.hasNext(); ) {
      GameObject go = it.next();
      go.z -= reference.z - offset;
      if (go.z > 1)
        it.remove();
    }
  }

  public Iterator<GameObject> iterator() {
    return objects.iterator();
  }
}
