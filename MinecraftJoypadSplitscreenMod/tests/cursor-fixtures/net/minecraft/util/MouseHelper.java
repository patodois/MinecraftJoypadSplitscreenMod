package net.minecraft.util;
import org.lwjgl.input.Mouse;
public class MouseHelper {
 public int deltaX,deltaY;
 public void grabMouseCursor(){Mouse.setGrabbed(true);}
 public void ungrabMouseCursor(){Mouse.setGrabbed(false);}
}
