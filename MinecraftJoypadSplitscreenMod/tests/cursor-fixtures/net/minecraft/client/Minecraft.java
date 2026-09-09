package net.minecraft.client;
import net.minecraft.util.MouseHelper;
public class Minecraft {
 public Object theWorld=new Object(),thePlayer=new Object(),currentScreen;
 public MouseHelper mouseHelper=new MouseHelper();
 public static Minecraft instance=new Minecraft();
 public static Minecraft getMinecraft(){return instance;}
}
