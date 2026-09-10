import com.shiny.joypadmod.gui.PixelLabSkinLayout;
public class SkinLayoutTest {
    public static void main(String[] args) {
        int checks=0;
        for(int state=0;state<3;state++)for(int width:new int[]{1,8,15,22,56,90,150,310,560})for(int height:new int[]{1,14,18,20,32}) {
            int[][] pixels=new int[height][width];
            for(int[] p:PixelLabSkinLayout.slices(width,height,state)) {
                if(p[0]<0||p[1]<0||p[2]>width||p[3]>height||p[2]<=p[0]||p[3]<=p[1])throw new AssertionError("Destination bounds");
                if(p[4]<0||p[6]>64||p[5]<state*32||p[7]>(state+1)*32)throw new AssertionError("Atlas state bounds");
                for(int y=p[1];y<p[3];y++)for(int x=p[0];x<p[2];x++)pixels[y][x]++;
            }
            for(int[] row:pixels)for(int covered:row)if(covered!=1)throw new AssertionError("Gap/overlap in button skin");
            checks++;
        }
        if(PixelLabSkinLayout.slices(0,20,0).length!=0)throw new AssertionError("Zero width");
        System.out.println("PASS: "+checks+" button sizes/states have complete, non-overlapping skin coverage.");
    }
}
