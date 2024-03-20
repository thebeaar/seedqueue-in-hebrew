package me.contaria.seedqueue.mixin.compat.worldpreview;

import com.bawnorton.mixinsquared.TargetHandler;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.contaria.seedqueue.SeedQueue;
import me.contaria.seedqueue.SeedQueueEntry;
import me.contaria.seedqueue.compat.WorldPreviewProperties;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.Packet;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;
import java.util.Queue;

@Mixin(value = ServerChunkManager.class, priority = 1500)
public abstract class ServerChunkManagerMixin {

    @Shadow
    @Final
    private ServerWorld world;

    @Dynamic
    @TargetHandler(
            mixin = "me.voidxwalker.worldpreview.mixin.server.ServerChunkManagerMixin",
            name = "getChunks"
    )
    @ModifyExpressionValue(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "FIELD",
                    target = "Lme/voidxwalker/worldpreview/WorldPreview;world:Lnet/minecraft/client/world/ClientWorld;"
            )
    )
    private ClientWorld sendChunksToCorrectWorldPreview_inQueue(ClientWorld world) {
        return this.getWorldPreviewProperties().map(WorldPreviewProperties::getWorld).orElse(this.world.getServer() == MinecraftClient.getInstance().getServer() ? world : null);
    }

    @Dynamic
    @TargetHandler(
            mixin = "me.voidxwalker.worldpreview.mixin.server.ServerChunkManagerMixin",
            name = "getChunks"
    )
    @ModifyExpressionValue(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "FIELD",
                    target = "Lme/voidxwalker/worldpreview/WorldPreview;player:Lnet/minecraft/client/network/ClientPlayerEntity;"
            )
    )
    private ClientPlayerEntity sendChunksToCorrectWorldPreview_inQueue(ClientPlayerEntity player) {
        return this.getWorldPreviewProperties().map(WorldPreviewProperties::getPlayer).orElse(this.world.getServer() == MinecraftClient.getInstance().getServer() ? player : null);
    }

    @Dynamic
    @TargetHandler(
            mixin = "me.voidxwalker.worldpreview.mixin.server.ServerChunkManagerMixin",
            name = "getChunks"
    )
    @ModifyExpressionValue(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "FIELD",
                    target = "Lme/voidxwalker/worldpreview/WorldPreview;camera:Lnet/minecraft/client/render/Camera;"
            )
    )
    private Camera sendChunksToCorrectWorldPreview_inQueue(Camera camera) {
        return this.getWorldPreviewProperties().map(WorldPreviewProperties::getCamera).orElse(this.world.getServer() == MinecraftClient.getInstance().getServer() ? camera : null);
    }

    @Dynamic
    @TargetHandler(
            mixin = "me.voidxwalker.worldpreview.mixin.server.ServerChunkManagerMixin",
            name = "getChunks"
    )
    @ModifyExpressionValue(
            method = "@MixinSquared:Handler",
            at = @At(
                    value = "FIELD",
                    target = "Lme/voidxwalker/worldpreview/WorldPreview;packetQueue:Ljava/util/Queue;"
            ),
            remap = false
    )
    private Queue<Packet<?>> sendChunksToCorrectWorldPreview_inQueue(Queue<Packet<?>> packetQueue) {
        return this.getWorldPreviewProperties().map(WorldPreviewProperties::getPacketQueue).orElse(this.world.getServer() == MinecraftClient.getInstance().getServer() ? packetQueue : null);
    }

    @Unique
    private Optional<WorldPreviewProperties> getWorldPreviewProperties() {
        return Optional.ofNullable(SeedQueue.getEntry(this.world.getServer())).map(SeedQueueEntry::getWorldPreviewProperties);
    }
}
