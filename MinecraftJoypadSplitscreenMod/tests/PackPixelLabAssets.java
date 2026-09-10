import com.shiny.joypadmod.gui.GlyphArt;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/** Offline packing only. This never generates artwork or calls any external service. */
public class PackPixelLabAssets {
    private static BufferedImage read(File root,String name) throws Exception {
        BufferedImage image=ImageIO.read(new File(root,"art/pixellab/"+name+".png"));
        if(image==null || !image.getColorModel().hasAlpha()) throw new AssertionError("Invalid transparent PNG: "+name);
        return image;
    }
    private static BufferedImage trim(BufferedImage image) {
        int minX=image.getWidth(),minY=image.getHeight(),maxX=-1,maxY=-1;
        for(int y=0;y<image.getHeight();y++)for(int x=0;x<image.getWidth();x++)
            if((image.getRGB(x,y)>>>24)>8) { minX=Math.min(minX,x);minY=Math.min(minY,y);maxX=Math.max(maxX,x);maxY=Math.max(maxY,y); }
        if(maxX<0)throw new AssertionError("Empty sprite");
        return image.getSubimage(minX,minY,maxX-minX+1,maxY-minY+1);
    }
    private static Graphics2D painter(BufferedImage image) {
        Graphics2D g=image.createGraphics();g.setComposite(AlphaComposite.Src);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        return g;
    }
    public static void main(String[] args) throws Exception {
        File root=new File(args[0]);
        BufferedImage atlas=new BufferedImage(GlyphArt.WIDTH,GlyphArt.HEIGHT,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g=painter(atlas);
        for(GlyphArt.Icon icon:GlyphArt.Icon.values()) {
            BufferedImage image=trim(read(root,icon.name()));
            double scale=Math.min(1.0,28.0/Math.max(image.getWidth(),image.getHeight()));
            int w=Math.max(1,(int)Math.round(image.getWidth()*scale)),h=Math.max(1,(int)Math.round(image.getHeight()*scale));
            int x=icon.ordinal()%GlyphArt.COLUMNS*GlyphArt.SIZE+(GlyphArt.SIZE-w)/2;
            int y=icon.ordinal()/GlyphArt.COLUMNS*GlyphArt.SIZE+(GlyphArt.SIZE-h)/2;
            g.drawImage(image,x,y,w,h,null);
        }
        g.dispose();
        File textures=new File(root,"src/main/resources/assets/joypadmod/textures/gui");textures.mkdirs();
        ImageIO.write(atlas,"png",new File(textures,"controller_glyphs.png"));
        BufferedImage widgets=new BufferedImage(128,128,BufferedImage.TYPE_INT_ARGB);g=painter(widgets);
        String[] states={"NORMAL","HOVER","DISABLED"};
        for(int i=0;i<states.length;i++) {
            BufferedImage button=trim(read(root,"BUTTON_"+states[i]));
            g.drawImage(button,0,i*32,64,32,null);
            BufferedImage thumb=trim(read(root,"THUMB_"+states[i]));
            g.drawImage(thumb,64,i*32,16,32,null);
        }
        g.dispose();ImageIO.write(widgets,"png",new File(textures,"pixellab_widgets.png"));
        System.out.println("PASS: packed all 27 PixelLab glyphs and 6 PixelLab widget states.");
    }
}
