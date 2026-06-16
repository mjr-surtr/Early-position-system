package com.besson.tutorial.block;

import com.besson.tutorial.TutorialMod;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

public class EarlyBrewingStandScreen extends AbstractContainerScreen<EarlyBrewingStandMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(TutorialMod.MOD_ID, "textures/gui/early_brewing_stand.png");

    // 气泡动画高度序列(对标原版BUBBLELENGTHS)
    private static final int[] BUBBLE_LENGTHS = {29, 24, 20, 16, 11, 6, 0};

    public EarlyBrewingStandScreen(EarlyBrewingStandMenu menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94; // = 72
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        // 1. 主背景
        graphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        // 2. 燃料条 (对标原版: x+60, y+44, 18x4水平条)
        //    图标在 u=176, v=57, 18x4
        int fuel = this.menu.getFuel();
        int fuelMax = this.menu.getFuelMax();
        if (fuelMax > 0) {
            int fuelWidth = Mth.clamp((18 * fuel + fuelMax - 1) / fuelMax, 0, 18);
            if (fuelWidth > 0) {
                graphics.blit(TEXTURE, x + 60, y + 44, 176, 57, fuelWidth, 4);
            }
        }

        // 3. 酿造进度 + 气泡动画
        int brewTime = this.menu.getBrewTime();
        int brewMax = this.menu.getBrewTimeMax();
        if (brewMax > 0 && brewTime > 0) {
            // 垂直进度条(从下往上填充)
            // 图标在 u=176, v=29, 9x28
            int progress = (int) (28.0F * (1.0F - (float) brewTime / brewMax));
            if (progress > 0) {
                // 从底部向上绘制：屏幕 y+16+(28-progress), 纹理 v=29+(28-progress)
                graphics.blit(TEXTURE,
                        x + 97, y + 16 + 28 - progress,
                        176, 29 + 28 - progress,
                        9, progress);
            }

            // 气泡动画(只在酿造进行时显示)
            int bubbleIdx = (brewTime / 2) % BUBBLE_LENGTHS.length;
            int bubbleHeight = BUBBLE_LENGTHS[bubbleIdx];
            if (bubbleHeight > 0) {
                // 图标在 u=176, v=0, 12x29
                graphics.blit(TEXTURE,
                        x + 63, y + 14 + 29 - bubbleHeight,
                        176, 29 - bubbleHeight,
                        12, bubbleHeight);
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, delta);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
