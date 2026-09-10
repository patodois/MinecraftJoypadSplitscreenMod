package com.shiny.joypadmod.minecraftExtensions;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import com.shiny.joypadmod.helpers.McObfuscationHelper;

import net.minecraft.client.Minecraft;
import com.shiny.joypadmod.gui.JoypadTheme;
import com.shiny.joypadmod.gui.PixelTheme;
import net.minecraft.util.StatCollector;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public class GuiSlider extends GuiButton
{
	/** The value of this slider control. */
	protected float sliderValue = 1.0F;
	public float minValue = 0.01F;
	public float maxValue = 1.0F;

	/** Is this slider control being dragged. */
	public boolean dragging;

	protected String baseDisplayString;

	public GuiSlider(int id, int posX, int posY, int width, int height, String displayString, float value)
	{
		super(id, posX, posY, width, height, displayString);
		this.sliderValue = value;
		this.baseDisplayString = displayString;
	}

	public void setValue(float value)
	{
		if (value < minValue)
		{
			value = minValue;
		}
		if (value > maxValue)
		{
			value = maxValue;
		}
		sliderValue = value;
	}

	public float getValue()
	{
		return sliderValue;
	}

	/**
	 * Returns 0 if the button is disabled, 1 if the mouse is NOT hovering over this button and 2 if it IS hovering over this button.
	 */
	@Override
	public int getHoverState(boolean mouseOver)
	{
		return 0;
	}

	/**
	 * Fired when the mouse button is dragged. Equivalent of MouseListener.mouseDragged(MouseEvent e).
	 */
    @Override
    protected void mouseDragged(Minecraft minecraft,int mouseX,int mouseY) {
        if(!Mouse.isButtonDown(0)) dragging=false;
        if(visible && enabled && dragging) {
            setValue((float)(mouseX-xPosition-4)/(width-8));
            updateText();
        }
    }
    @Override
    public void drawButton(Minecraft mc,int mouseX,int mouseY) {
        if(!visible) return;
        mouseDragged(mc,mouseX,mouseY);
        boolean hover=mouseX>=xPosition && mouseX<xPosition+width && mouseY>=yPosition && mouseY<yPosition+height;
        JoypadTheme.plate(xPosition,yPosition,width,height,hover||dragging,enabled);
        JoypadTheme.thumb(xPosition+(int)(getValue()*(width-8)),yPosition+2,8,height-4,hover||dragging,enabled);
        drawCenteredString(mc.fontRendererObj,mc.fontRendererObj.trimStringToWidth(displayString,width-8),
            xPosition+width/2,yPosition+(height-8)/2,enabled?PixelTheme.TEXT:PixelTheme.MUTED);
    }

	/**
	 * Returns true if the mouse has been pressed on this control. Equivalent of MouseListener.mousePressed(MouseEvent e).
	 */
	@Override
	public boolean mousePressed(Minecraft minecraft, int mouseX, int mouseY)
	{
		if (super.mousePressed(minecraft, mouseX, mouseY))
		{
			setValue((float) (mouseX - (this.xPosition + 4)) / (float) (this.width - 8));

			this.dragging = true;
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Fired when the mouse button is released. Equivalent of MouseListener.mouseReleased(MouseEvent e).
	 */
	@Override
	public void mouseReleased(int mouseX, int mouseY)
	{
		this.dragging = false;
	}

	public void updateText()
	{
		String output = "";
		if (this.baseDisplayString.equals("controlMenu.sensitivity.game"))
		{
			output = String.format("(%s) %s", McObfuscationHelper.lookupString("key.categories.gameplay"),
					McObfuscationHelper.lookupString("options.sensitivity"));
		}
		else if (this.baseDisplayString.equals("controlMenu.sensitivity.menu"))
		{
			output = String.format("(%s) %s", McObfuscationHelper.lookupString("joy.menu"),
					McObfuscationHelper.lookupString("options.sensitivity"));
		}
		if (output != "")
		{
			FontRenderer fr = Minecraft.getMinecraft().fontRendererObj;
			String value = ": " + (int) (this.sliderValue * 100.0F);
			this.displayString = fr.trimStringToWidth(output, this.width - fr.getStringWidth(value)) + value;
		}
		else
		{
			this.displayString = StatCollector.translateToLocalFormatted(this.baseDisplayString,
					(int) (this.sliderValue * 100.0F));
		}
	}
}
