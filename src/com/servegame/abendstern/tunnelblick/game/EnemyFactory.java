package com.servegame.abendstern.tunnelblick.game;

public final class EnemyFactory {
  private static final float BASE_SPAWN_INTERVAL = 5;
  private static final float PILLAR_INTERVAL = 10;
  private final Tunnelblick game;
  private float timeUntilSpawn = 1;
  private float timeUntilPillar = PILLAR_INTERVAL;

  public EnemyFactory(Tunnelblick tb) {
    game = tb;
  }

  public void update(float et, float difficulty) {
    timeUntilSpawn -= et;
    if (timeUntilSpawn < 0) {
      timeUntilSpawn = (float)((Math.random()+0.2)*BASE_SPAWN_INTERVAL /
                               Math.sqrt(difficulty));

      double what = difficulty * Math.random();
      if (what < 2.0)
        game.field.add(new Cactus(game));
      else if (what < 3.0)
        game.field.add(new FireFern(game));
      else if (what < 3.5)
        game.field.add(new Beetle(game));
      else if (what < 4.5)
        game.field.add(new Hurtle(game));
      else if (what < 5.5)
        game.field.add(new Flea(game));
      else
        game.field.add(new Bat(game));
    }

    timeUntilPillar -= et;
    if (timeUntilPillar < 0) {
      timeUntilPillar = PILLAR_INTERVAL;

      game.field.add(new Pillar(game));
    }
  }
}
