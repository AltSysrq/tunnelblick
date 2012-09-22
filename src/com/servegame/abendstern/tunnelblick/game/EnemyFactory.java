package com.servegame.abendstern.tunnelblick.game;

public final class EnemyFactory {
  private static final float BASE_SPAWN_INTERVAL = 7;
  private final Tunnelblick game;
  private float timeUntilSpawn = 1;

  public EnemyFactory(Tunnelblick tb) {
    game = tb;
  }

  public void update(float et, float difficulty) {
    timeUntilSpawn -= et;
    if (timeUntilSpawn < 0) {
      timeUntilSpawn = (float)((Math.random()+0.2)*BASE_SPAWN_INTERVAL /
                               Math.sqrt(difficulty));

      double what = difficulty * Math.random();
      if (what < 2 || true)
        game.field.add(new Cactus(game));
    }
  }
}
