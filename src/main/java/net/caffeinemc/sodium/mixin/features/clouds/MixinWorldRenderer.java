package net.caffeinemc.sodium.mixin.features.clouds;

import net.caffeinemc.sodium.SodiumClientMod;
import net.caffeinemc.sodium.render.CloudRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.resource.ResourceManager;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class MixinWorldRenderer {
    @Shadow
    private @Nullable ClientWorld world;
    @Shadow
    private int ticks;

    @Shadow
    @Final
    private MinecraftClient client;

    private CloudRenderer cloudRenderer;

    /**
     * @author jellysquid3
     * @reason Optimize cloud rendering
     */
    @Overwrite
    public void renderClouds(MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, double x, double y, double z) {
        if (this.cloudRenderer == null) {
            this.cloudRenderer = new CloudRenderer(SodiumClientMod.DEVICE);
        }

        this.cloudRenderer.render(this.world, matrices, projectionMatrix, this.ticks, tickDelta, x, y, z);
    }

    @Inject(method = "reload(Lnet/minecraft/resource/ResourceManager;)V", at = @At("RETURN"))
    private void onReload(ResourceManager manager, CallbackInfo ci) {
        if (this.cloudRenderer != null) {
            this.cloudRenderer.reloadTextures();
        }
    }

    @Inject(method = "reload()V", at = @At("RETURN"))
    private void onReload(CallbackInfo ci) {
        if (this.cloudRenderer != null) {
            this.cloudRenderer.destroy();
            this.cloudRenderer = null;
        }
    }
}
