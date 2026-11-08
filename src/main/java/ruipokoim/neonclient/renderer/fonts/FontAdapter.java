package ruipokoim.neonclient.renderer.fonts;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;

import java.io.InputStream;
import java.nio.ByteBuffer;

import static ruipokoim.neonclient.NeonMain.mc;

public class FontAdapter {
    private final Glyph[] glyphs = new Glyph[256];
    private Identifier textureId;
    private float fontHeight;
    private float fontAscent;
    private float guiScale;

    public void load(Identifier fontLoc, float size) {
        this.guiScale = (float) MinecraftClient.getInstance().getWindow().getScaleFactor();
        float rasterSize = size * guiScale;

        try (InputStream is = mc.getResourceManager().getResource(fontLoc).get().getInputStream()) {
            byte[] bytes = is.readAllBytes();
            ByteBuffer buffer = MemoryUtil.memAlloc(bytes.length).put(bytes).flip();

            STBTTFontinfo info = STBTTFontinfo.create();
            STBTruetype.stbtt_InitFont(info, buffer);

            float scale = STBTruetype.stbtt_ScaleForPixelHeight(info, rasterSize);

            int[] ascent = {0}, descent = {0}, lineGap = {0};
            STBTruetype.stbtt_GetFontVMetrics(info, ascent, descent, lineGap);

            this.fontAscent = ascent[0] * scale / guiScale;
            this.fontHeight = (ascent[0] - descent[0] + lineGap[0]) * scale / guiScale;

            int texSize = 1024;
            NativeImage img = new NativeImage(texSize, texSize, true);
            int x = 1, y = 1, lineHeight = 0;

            for (int i = 32; i < 128; i++) {
                int[] w = {0}, h = {0}, ox = {0}, oy = {0};
                ByteBuffer bitmap = STBTruetype.stbtt_GetCodepointBitmap(info, scale, scale, i, w, h, ox, oy);
                int[] advanceWidth = {0}, leftBearing = {0};
                STBTruetype.stbtt_GetCodepointHMetrics(info, i, advanceWidth, leftBearing);
                float advance = advanceWidth[0] * scale / guiScale;

                if (bitmap != null && w[0] > 0 && h[0] > 0) {
                    if (x + w[0] >= texSize) {
                        x = 1;
                        y += lineHeight + 1;
                        lineHeight = 0;
                    }
                    for (int gy = 0; gy < h[0]; gy++) {
                        for (int gx = 0; gx < w[0]; gx++) {
                            int alpha = bitmap.get(gx + gy * w[0]) & 0xFF;
                            img.setColor(x + gx, y + gy, (alpha << 24) | 0xFFFFFF);
                        }
                    }
                    glyphs[i] = new Glyph(x, y, w[0], h[0], ox[0] / guiScale, oy[0] / guiScale, advance);
                    x += w[0] + 1;
                    lineHeight = Math.max(lineHeight, h[0]);
                    STBTruetype.stbtt_FreeBitmap(bitmap);
                } else {
                    glyphs[i] = new Glyph(0, 0, 0, 0, 0, 0, advance);
                }
            }
            this.textureId = Identifier.of("neon-client", "fonts/outfit.ttf");
            MinecraftClient.getInstance().getTextureManager().registerTexture(
                    this.textureId,
                    new NativeImageBackedTexture(() -> "NeonClientFont", img)
            );
            MemoryUtil.memFree(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public record Glyph(int u, int v, int width, int height, float xOffset, float yOffset, float advance) {}
    public Identifier getTextureId() { return textureId; }
    public Glyph getGlyph(char c)    { return glyphs[c < 256 ? c : 63]; }
    public float getFontHeight()     { return fontHeight; }
    public float getFontAscent()     { return fontAscent; }
    public float getGuiScale()       { return guiScale; }
    public boolean isLoaded()        { return textureId != null; }
}