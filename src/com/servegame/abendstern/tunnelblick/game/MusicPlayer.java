package com.servegame.abendstern.tunnelblick.game;

import java.io.*;
import com.servegame.abendstern.tunnelblick.backend.AudioSource;

public class MusicPlayer implements AudioSource {
  private static final float MIN_CONV = 0.5f, MAX_CONV = 2.5f;
  private static final float STRENGTH_RATE = 0.5f;
  private static final File DIR = new File("sound/music");

  private FileInputStream in = null;
  private float strength = 0, newStrength;
  private byte byteBuffer[] = null;
  private Distortion distortion;

  public MusicPlayer(Distortion distortion) {
    this.distortion = distortion;
  }

  public int read(short dst[], int len) {
    //If there is no current stream, select a music file at random
    if (in == null) {
      File files[] = DIR.listFiles();
      if (files.length == 0) return -1;

      try {
        in = new FileInputStream(files[(int)(Math.random()*files.length)]);
      } catch (IOException e) {
        System.err.println("Couldn't open music stream; giving up on music: " +
                           e);
        return -1;
      }
    }

    int amtRead;
    //If the current byte buffer is too small or does not exist, make a new one
    if (byteBuffer == null || byteBuffer.length < 2*len)
      byteBuffer = new byte[2*len];

    try {
      amtRead = in.read(byteBuffer, 0, 2*len);
      amtRead /= 2; //From bytes to samples

      //Avoid div by zero by returning now if nothing was read
      if (amtRead == 0) {
        try { in.close(); } catch (IOException e) {}
        in = null;
        return 0;
      }

      //While converting to shorts, keep track of the average volume
      float sampleSum = 0;
      for (int i = 0; i < amtRead; ++i) {
        dst[i] = (short)(
          (((short)byteBuffer[2*i+0]) & 0xFF) |
          (((short)byteBuffer[2*i+1]) << 8));
        float s = (float)dst[i];
        sampleSum += Math.abs(s);
      }

      newStrength = sampleSum / amtRead / 37268.0f;

      //If amtRead is not as much as we wanted, we've hit the end of the stream
      if (amtRead != len) {
        try { in.close(); } catch (IOException e) {}
        in = null;
      }
      return amtRead;
    } catch (IOException e) {
      System.err.println("Couldn't read music stream; giving up on music: " +
                         e);
      try { in.close(); } catch (IOException e2) {}
      in = null;
      return -1;
    }
  }

  public void update(float et) {
    float delta = et*STRENGTH_RATE;
    if (strength < newStrength)
      strength = Math.min(newStrength, strength+delta);
    else
      strength = Math.max(newStrength, strength-delta);

    distortion.setConvulsionMult(strength*(MAX_CONV-MIN_CONV) + MIN_CONV);
  }
}
