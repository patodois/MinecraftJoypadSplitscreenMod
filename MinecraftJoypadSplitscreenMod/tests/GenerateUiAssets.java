import com.shiny.joypadmod.gui.GlyphArt;
import com.shiny.joypadmod.gui.GlyphArt.Icon;
import com.shiny.joypadmod.gui.PixelTheme;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/** Creates the actual runtime sprite sheet and a clearly labeled design preview. */
public class GenerateUiAssets {
    static Graphics2D g;
    static GlyphArt.Painter painter;
    static BufferedImage atlas;
    static void label(String text,int x,int y,int color) {
        g.setColor(new Color(color|0xFF000000,true)); g.drawString(text,x,y);
    }
    static void icon(Icon icon,int x,int y,int size) {
        int sx=(icon.ordinal()%GlyphArt.COLUMNS)*16,sy=(icon.ordinal()/GlyphArt.COLUMNS)*16;
        g.drawImage(atlas,x,y,x+size,y+size,sx,sy,sx+16,sy+16,null);
    }
    static void hint(Icon icon,String action,int x,int y,int width) {
        painter.rect(x,y,width,23,0xFF25291E);
        painter.rect(x,y+22,width,1,0xFF847653);
        icon(icon,x+3,y+3,16); label(action,x+25,y+16,PixelTheme.TEXT);
    }
    static void button(String text,int x,int y,int w,boolean hover) {
        PixelTheme.plate(painter,x,y,w,23,hover,true);
        label(text,x+(w-g.getFontMetrics().stringWidth(text))/2,y+16,hover?0xF2EAA9:PixelTheme.TEXT);
    }
    public static void main(String[] args) throws Exception {
        File root=new File(args.length==0?".":args[0]);
        atlas=new BufferedImage(GlyphArt.WIDTH,GlyphArt.HEIGHT,BufferedImage.TYPE_INT_ARGB);
        for(final Icon icon:Icon.values()) {
            final int ox=(icon.ordinal()%GlyphArt.COLUMNS)*16,oy=(icon.ordinal()/GlyphArt.COLUMNS)*16;
            GlyphArt.paint(new GlyphArt.Painter(){public void rect(int x,int y,int w,int h,int color){
                if(x<ox || y<oy || x+w>ox+16 || y+h>oy+16) throw new AssertionError("Glyph out of bounds: "+icon);
                for(int a=x;a<x+w;a++)for(int b=y;b<y+h;b++)atlas.setRGB(a,b,color);
            }},icon,ox,oy);
        }
        File output=new File(root,"src/main/resources/assets/joypadmod/textures/gui/controller_glyphs.png");
        output.getParentFile().mkdirs(); ImageIO.write(atlas,"png",output);
        BufferedImage preview=new BufferedImage(1040,640,BufferedImage.TYPE_INT_RGB);
        g=preview.createGraphics(); g.scale(2,2);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        painter=new GlyphArt.Painter(){public void rect(int x,int y,int w,int h,int color){g.setColor(new Color(color,true));g.fillRect(x,y,w,h);}};
        painter.rect(0,0,520,320,0xFF20241A);
        // Subtle stone tiles emphasize the same neutral palette used by the mod.
        for(int y=0;y<320;y+=16)for(int x=0;x<520;x+=16)
            painter.rect(x+1,y+1,14,14,((x/16+y/16)%2==0)?0xFF24271F:0xFF22261C);
        painter.rect(0,0,520,3,0xFF9EAC68); painter.rect(0,3,520,2,0xFF647D39);
        g.setFont(new Font(Font.MONOSPACED,Font.BOLD,16));
        label("JOYPAD ENHANCED",18,29,PixelTheme.TEXT);
        g.setFont(new Font(Font.MONOSPACED,Font.PLAIN,10));
        label("0.3.0  |  PIXEL GLYPHS + CONSOLE DEFAULTS",18,47,PixelTheme.ACCENT);
        g.setFont(new Font(Font.MONOSPACED,Font.BOLD,10));
        button("Bindings",18,61,150,false); button("Test controller",174,61,150,true); button("Capture mouse: ON",330,61,172,false);
        label("GAMEPLAY",18,107,PixelTheme.ACCENT); label("INVENTORY / MENUS",270,107,PixelTheme.ACCENT);
        hint(Icon.Y,"Inventory",18,117,232); hint(Icon.Y,"Back / close",270,117,232);
        hint(Icon.A,"Jump",18,144,232); hint(Icon.A,"Take / place stack",270,144,232);
        hint(Icon.RT,"Attack / mine",18,171,232); hint(Icon.X,"Split / place one",270,171,232);
        hint(Icon.LT,"Use / place block",18,198,232); hint(Icon.B,"Quick move",270,198,232);
        label("MORE CONTROLS",18,245,PixelTheme.ACCENT);
        Icon[] extras={Icon.LB,Icon.RB,Icon.LS,Icon.RS,Icon.VIEW,Icon.MENU,Icon.DPAD_UP,Icon.DPAD_DOWN,Icon.DPAD_LEFT,Icon.DPAD_RIGHT};
        for(int i=0;i<extras.length;i++) {
            PixelTheme.plate(painter,18+i*49,254,43,30,false,true);
            icon(extras[i],29+i*49,259,20);
        }
        g.setFont(new Font(Font.MONOSPACED,Font.PLAIN,9));
        label("Design preview: production sprites and colors, sample bindings. Not an in-game screenshot.",18,308,PixelTheme.MUTED);
        g.dispose();
        File image=new File(root,"build/previews/controller-ui-preview.png");image.getParentFile().mkdirs();ImageIO.write(preview,"png",image);
        System.out.println("PASS: "+Icon.values().length+" bounded pixel glyphs; generated atlas and UI design preview.");
    }
}
