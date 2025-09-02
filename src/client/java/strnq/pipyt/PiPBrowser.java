package strnq.pipyt;

import com.cinemamod.mcef.MCEF;
import com.cinemamod.mcef.MCEFBrowser;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;

public class PiPBrowser extends Screen {
    private static final int BROWSER_DRAW_OFFSET = 20;

    public static MCEFBrowser browser;

    private final MinecraftClient minecraft = MinecraftClient.getInstance();

    public static boolean showBrowser = false;
    private boolean resizing = false;

    static Vec2f windowPos = new Vec2f(10, 10);
    static Vec2f windowSize = new Vec2f(200, 110);

    static Vec2f resizeStartPos = new Vec2f(0, 0);

    public PiPBrowser(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        if (browser == null) {
            String url = "about:blank";
            boolean transparent = true;
            browser = MCEF.createBrowser(url, transparent);
            resizeBrowser(windowSize.x, windowSize.y);
        }

        TextFieldWidget linkField = new TextFieldWidget(minecraft.textRenderer, this.width-110, 100,  100, 20, Text.of(""));
        linkField.setPlaceholder(Text.of("YouTube link"));
        linkField.setMaxLength(124);

        ButtonWidget buttonWidget = ButtonWidget.builder(Text.of("Play video"), (btn) -> {
            if (!linkField.getText().contains("youtu")){
                this.client.getToastManager().add(
                        SystemToast.create(this.client, SystemToast.Type.LOW_DISK_SPACE, Text.of("Bad Url"), Text.of(""))
                );
                return;
            }
            String newUrl = linkField.getText().replace("https://youtu.be/", "https://www.youtube.com/embed/").replace("youtube.com", "youtube.com/embed/").replace("/watch?v=", "");
            browser.loadURL(newUrl);
            System.out.println(newUrl);
            this.client.getToastManager().add(
                    SystemToast.create(this.client, SystemToast.Type.NARRATOR_TOGGLE, Text.of("Hello World!"), Text.of("New link:" + linkField.getText()))
            );
        }).dimensions(this.width-110, 125, 100, 20).build();

        ButtonWidget togglePlayer = ButtonWidget.builder(Text.of("Toggle player"), (btn) -> {
            showBrowser = !showBrowser;
        }).dimensions(this.width-110, 150, 100, 20).build();

        ButtonWidget resizeButton = ButtonWidget.builder(Text.of("Resize"), (btn) -> {
            showBrowser = false;
            resizing = true;
        }).dimensions(this.width-110, 175, 100, 20).build();

        this.addDrawableChild(linkField);
        this.addDrawableChild(buttonWidget);
        this.addDrawableChild(togglePlayer);
        this.addDrawableChild(resizeButton);
    }

    private int mouseX(double x) {
        return (int) ((x - windowPos.x) * minecraft.getWindow().getScaleFactor());
    }

    private int mouseY(double y) {
        return (int) ((y - windowPos.y) * minecraft.getWindow().getScaleFactor());
    }

    private int scaleX(double x) {
        return (int) ((x) * minecraft.getWindow().getScaleFactor());
    }

    private int scaleY(double y) {
        return (int) ((y) * minecraft.getWindow().getScaleFactor());
    }


    private void resizeBrowser(double w, double h) {
        browser.resize(scaleX(w), scaleY(h));
    }

    public static void renderBrowser(){
        if (browser==null || !showBrowser){return;}
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, browser.getRenderer().getTextureID());
        Tessellator t = Tessellator.getInstance();
        BufferBuilder buffer = t.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
//        buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        buffer.vertex(windowPos.x, windowPos.y+windowSize.y, 0).texture(0.0f, 1.0f).color(255, 255, 255, 255);
        buffer.vertex(windowPos.x+windowSize.x, windowPos.y+windowSize.y, 0).texture(1.0f, 1.0f).color(255, 255, 255, 255);
        buffer.vertex(windowPos.x+windowSize.x, windowPos.y, 0).texture(1.0f, 0.0f).color(255, 255, 255, 255);
        buffer.vertex(windowPos.x, windowPos.y, 0).texture(0.0f, 0.0f).color(255, 255, 255, 255);
        BufferRenderer.drawWithGlobalProgram(buffer.endNullable());
        RenderSystem.setShaderTexture(0, 0);
        RenderSystem.enableDepthTest();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderBrowser();
        if (!resizing || (resizeStartPos.x == 0 && resizeStartPos.y == 0)) {
            if (resizing)
                context.fill(mouseX - 3, mouseY - 3, mouseX + 3, mouseY + 3, 0xFFFF0000);
        } else {
            context.fill((int) resizeStartPos.x, (int) resizeStartPos.y, mouseX, mouseY, 0xFFFF0000);
        }
//        if (insideWindow(mouseX, mouseY)){
//            context.drawText(minecraft.textRenderer, String.format("%d %d", mouseX(mouseX), mouseY(mouseY)), mouseX, mouseY-10, 0xFFFF0000, false);
//            context.fill(mouseX - 3, mouseY - 3, mouseX + 3, mouseY + 3, 0xFFFF0000);
//        }
    }

    public static boolean insideWindow(double mouseX, double mouseY){
        return (mouseX >= windowPos.x && mouseX <= windowPos.x + windowSize.x &&
                mouseY >= windowPos.y && mouseY <= windowPos.y + windowSize.y);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (showBrowser && insideWindow(mouseX, mouseY)) {
            browser.sendMousePress(mouseX(mouseX), mouseY(mouseY), button);
            browser.setFocus(true);
            return true;
        }
        if (resizing) {
            resizeStartPos = new Vec2f((float) mouseX, (float) mouseY);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean btnflag2 = true;
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (showBrowser && !resizing && insideWindow(mouseX, mouseY)) {
            browser.sendMouseRelease(mouseX(mouseX), mouseY(mouseY), button);
            browser.setFocus(true);
        }
        if (resizing){
            if (btnflag2) {
                btnflag2 = false;
                return super.mouseReleased(mouseX, mouseY, button);
            } else {
                btnflag2 = true;
            }
            float newWinWidth = 0;
            float newWinStartX = 0;
            if (mouseX < resizeStartPos.x) {
                newWinWidth = (float) (resizeStartPos.x-mouseX);
                newWinStartX = (float) mouseX;
            } else {
                newWinWidth = (float) (mouseX - resizeStartPos.x);
                newWinStartX = (float) resizeStartPos.x;
            }
            if (mouseY < resizeStartPos.y) {
                windowPos = new Vec2f(newWinStartX, (float)mouseY);
                windowSize = new Vec2f(newWinWidth, (float) (resizeStartPos.y-mouseY));
            } else {
                windowPos = new Vec2f(newWinStartX, resizeStartPos.y);
                windowSize = new Vec2f(newWinWidth, (float) (mouseY-resizeStartPos.y));
            }
            resizeStartPos = new Vec2f(0,0);
            resizeBrowser(windowSize.x, windowSize.y);
            resizing = false;
            showBrowser = true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (showBrowser && insideWindow(mouseX, mouseY)) {
            browser.sendMouseMove(mouseX(mouseX), mouseY(mouseY));
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (showBrowser && insideWindow(mouseX, mouseY)) {
            browser.sendMouseWheel(mouseX(mouseX), mouseY(mouseY), verticalAmount, 0);
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (showBrowser) {
            browser.sendKeyPress(keyCode, scanCode, modifiers);
            browser.setFocus(true);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (showBrowser) {
            browser.sendKeyRelease(keyCode, scanCode, modifiers);
            browser.setFocus(true);
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (showBrowser) {
            if (chr == (char) 0) return false;
            browser.sendKeyTyped(chr, modifiers);
            browser.setFocus(true);
        }
        return super.charTyped(chr, modifiers);
    }
}