package org.lwjgl.input;
public class Mouse {
 public static boolean created=true,grabbed=false;
 public static int changes,dx=40,dy=60;
 public static boolean isCreated(){return created;}
 public static boolean isGrabbed(){return grabbed;}
 public static void setGrabbed(boolean value){grabbed=value;changes++;}
 public static int getDX(){int value=dx;dx=0;return value;}
 public static int getDY(){int value=dy;dy=0;return value;}
}
