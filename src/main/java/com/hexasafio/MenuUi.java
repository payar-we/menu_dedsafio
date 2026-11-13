package com.hexasafio;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

/** Dibujo comun del menu principal y del menu de pausa (cabecera + logo + botones). */
final class MenuUi {
    private MenuUi() {}

    static final Identifier LOGO   = Identifier.of("hexasafio", "textures/gui/logo.png");
    static final Identifier HEADER = Identifier.of("hexasafio", "textures/gui/estudio.png");

    // tamano real en pixeles de cada PNG
    static final int LOGO_W = 1100, LOGO_H = 611;
    static final int HEADER_W = 613, HEADER_H = 101;

    static final class Btn {
        final Identifier tex;
        final int tw, th;
        final Runnable action;
        float hover = 0f;
        int x, y, w, h;

        Btn(String png, int tw, int th, Runnable action) {
            this.tex = Identifier.of("hexasafio", "textures/gui/" + png + ".png");
            this.tw = tw; this.th = th; this.action = action;
        }

        boolean contains(double mx, double my) {
            return mx >= x && mx <= x + w && my >= y && my <= y + h;
        }
    }

    /**
     * @param cx         centro horizontal (fraccion de la pantalla)
     * @param headerY    centro vertical de la cabecera (fraccion)
     * @param logoTop    borde superior del logo (fraccion)
     * @param logoWFrac  ancho del logo (fraccion del ancho de pantalla)
     * @param btnY       centro vertical de cada boton (fraccion)
     * @param btnRefW    ancho en pixeles del PNG de referencia (el que se mide con refFrac)
     * @param refFrac    fraccion del ancho de pantalla que ocupa ese PNG de referencia
     */
    static void draw(DrawContext ctx, int width, int height, int mouseX, int mouseY, long now, float fade,
                     float cx, float headerY, float logoTop, float logoWFrac,
                     float[] btnY, int btnRefW, float refFrac, Btn[] buttons) {
        float cxPx = width * cx;
        ctx.setShaderColor(1f, 1f, 1f, fade);

        // cabecera hexagon.studio + cubo
        float hh = height * 0.046f;
        float hw = hh * HEADER_W / HEADER_H;
        ctx.drawTexture(HEADER, Math.round(cxPx - hw / 2f), Math.round(height * headerY - hh / 2f),
                Math.round(hw), Math.round(hh), 0, 0, HEADER_W, HEADER_H, HEADER_W, HEADER_H);

        // logo
        float logoW = width * logoWFrac;
        float logoH = logoW * LOGO_H / LOGO_W;
        float maxH = height * 0.40f;
        if (logoH > maxH) {
            logoH = maxH;
            logoW = logoH * LOGO_W / LOGO_H;
        }
        float bob = (float) Math.sin(now / 900.0) * 2.5f;
        ctx.drawTexture(LOGO, Math.round(cxPx - logoW / 2f), Math.round(height * logoTop + bob),
                Math.round(logoW), Math.round(logoH), 0, 0, LOGO_W, LOGO_H, LOGO_W, LOGO_H);

        // botones (misma escala para todos)
        float k = (width * refFrac) / btnRefW;
        for (int i = 0; i < buttons.length; i++) {
            Btn b = buttons[i];
            b.w = Math.round(b.tw * k);
            b.h = Math.round(b.th * k);
            b.x = Math.round(cxPx - b.w / 2f);
            b.y = Math.round(height * btnY[i] - b.h / 2f);

            boolean over = b.contains(mouseX, mouseY);
            b.hover += ((over ? 1f : 0f) - b.hover) * 0.25f;
            float s = 1f + 0.10f * b.hover;

            ctx.getMatrices().push();
            ctx.getMatrices().translate(b.x + b.w / 2f, b.y + b.h / 2f, 0);
            ctx.getMatrices().scale(s, s, 1f);
            ctx.getMatrices().translate(-b.w / 2f, -b.h / 2f, 0);

            if (b.hover > 0.02f) {   // brillo al pasar el mouse
                ctx.setShaderColor(1f, 1f, 1f, fade * 0.45f * b.hover);
                ctx.getMatrices().push();
                ctx.getMatrices().translate(b.w / 2f, b.h / 2f, 0);
                ctx.getMatrices().scale(1.06f, 1.06f, 1f);
                ctx.getMatrices().translate(-b.w / 2f, -b.h / 2f, 0);
                ctx.drawTexture(b.tex, 0, 0, b.w, b.h, 0, 0, b.tw, b.th, b.tw, b.th);
                ctx.getMatrices().pop();
            }
            ctx.setShaderColor(1f, 1f, 1f, fade);
            ctx.drawTexture(b.tex, 0, 0, b.w, b.h, 0, 0, b.tw, b.th, b.tw, b.th);
            ctx.getMatrices().pop();
        }
        ctx.setShaderColor(1f, 1f, 1f, 1f);
    }

    static Btn hit(Btn[] buttons, double mx, double my) {
        if (buttons == null) return null;
        for (Btn b : buttons) if (b.contains(mx, my)) return b;
        return null;
    }
}
