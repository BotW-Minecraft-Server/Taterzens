package org.samo_lego.taterzens.mixin;

import com.mojang.authlib.GameProfile;
import com.mojang.datafixers.util.Pair;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerSpawnS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.samo_lego.taterzens.mixin.accessors.EntityTrackerUpdateS2CPacketAccessor;
import org.samo_lego.taterzens.mixin.accessors.PlayerListS2CPacketAccessor;
import org.samo_lego.taterzens.mixin.accessors.PlayerSpawnS2CPacketAccessor;
import org.samo_lego.taterzens.npc.TaterzenNPC;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action.ADD_PLAYER;
import static net.minecraft.network.packet.s2c.play.PlayerListS2CPacket.Action.REMOVE_PLAYER;
import static org.samo_lego.taterzens.Taterzens.config;

/**
 * Used to "fake" the TaterzenNPC entity type.
 */
@Mixin(value = ServerPlayNetworkHandler.class, priority = 900)
public abstract class ServerPlayNetworkHandlerMixin_PacketFaker {

    @Shadow public ServerPlayerEntity player;

    @Shadow public abstract void sendPacket(Packet<?> packet);

    @Shadow @Final public ClientConnection connection;
    @Unique
    private boolean taterzens$skipCheck;
    @Unique
    private final List<Pair<GameProfile, Text>> taterzens$tablistQueue = new ArrayList<>();
    @Unique
    private int taterzens$queueTimer;

    /**
     * Changes entity type if entity is an instance of {@link TaterzenNPC}.
     *
     * @param packet packet to change
     * @param listener
     */
    @Inject(
            method = "sendPacket(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/ClientConnection;send(Lnet/minecraft/network/Packet;Lio/netty/util/concurrent/GenericFutureListener;)V"
            ),
            cancellable = true
    )
    private void changeEntityType(Packet<?> packet, GenericFutureListener<? extends Future<? super Void>> listener, CallbackInfo ci) {
        World world = player.getEntityWorld();
        if(packet instanceof PlayerSpawnS2CPacket && !this.taterzens$skipCheck) {
            Entity entity = world.getEntityById(((PlayerSpawnS2CPacketAccessor) packet).getId());

            if(!(entity instanceof TaterzenNPC npc))
                return;

            GameProfile profile = npc.getGameProfile();

            PlayerListS2CPacket playerAddPacket = new PlayerListS2CPacket(ADD_PLAYER);
            //noinspection ConstantConditions
            ((PlayerListS2CPacketAccessor) playerAddPacket).setEntries(
                    Arrays.asList(new PlayerListS2CPacket.Entry(profile, 0, GameMode.SURVIVAL, npc.getName()))
            );
            this.sendPacket(playerAddPacket);

            // Before we send this packet, we have
            // added player to tablist, otherwise client doesn't
            // show it ... :mojank:
            this.connection.send(packet, listener);

            // And now we can remove it from tablist
            // we must delay the tablist packet so as to allow
            // the client to fetch skin.
            // If player is immediately removed from the tablist,
            // client doesn't care about the skin.
            this.taterzens$queueTimer = config.taterzenTablistTimeout;
            this.taterzens$tablistQueue.add(new Pair<>(npc.getGameProfile(), npc.getName()));

            this.connection.send(new EntitySetHeadYawS2CPacket(entity, (byte)((int)(entity.getHeadYaw() * 256.0F / 360.0F))), listener);

            ci.cancel();
        } else if(packet instanceof EntityTrackerUpdateS2CPacket) {
            Entity entity = world.getEntityById(((EntityTrackerUpdateS2CPacketAccessor) packet).getEntityId());

            if(!(entity instanceof TaterzenNPC))
                return;
            PlayerEntity fakePlayer = ((TaterzenNPC) entity).getFakePlayer();
            List<DataTracker.Entry<?>> trackedValues = fakePlayer.getDataTracker().getAllEntries();
            ((EntityTrackerUpdateS2CPacketAccessor) packet).setTrackedValues(trackedValues);
        } else if(packet instanceof PlayerListS2CPacket && !this.taterzens$skipCheck) {
            this.taterzens$skipCheck = true;

            this.taterzens$queueTimer = config.taterzenTablistTimeout;
            ((PlayerListS2CPacketAccessor) packet).getEntries().forEach(entry -> {
                if(entry.getProfile().getName().equals("-" + config.defaults.name + "-")) {
                    // Fixes unloaded taterzens showing in tablist (disguiselib)
                    this.taterzens$tablistQueue.add(new Pair<>(entry.getProfile(), entry.getDisplayName()));
                }
            });

            this.taterzens$skipCheck = false;
        }
    }

    @Inject(method = "onPlayerMove(Lnet/minecraft/network/packet/c2s/play/PlayerMoveC2SPacket;)V", at = @At("RETURN"))
    private void removeTaterzenFromTablist(PlayerMoveC2SPacket packet, CallbackInfo ci) {
        if(!this.taterzens$tablistQueue.isEmpty() && --this.taterzens$queueTimer <= 0) {
            this.taterzens$skipCheck = true;

            PlayerListS2CPacket taterzensRemovePacket = new PlayerListS2CPacket(REMOVE_PLAYER);
            List<PlayerListS2CPacket.Entry> taterzenList = this.taterzens$tablistQueue
                    .stream()
                    .map(pair -> new PlayerListS2CPacket.Entry(
                                    pair.getFirst(),
                                    0,
                                    GameMode.SURVIVAL,
                                    pair.getSecond()
                            )
                    )
                    .collect(Collectors.toList());
            //noinspection ConstantConditions
            ((PlayerListS2CPacketAccessor) taterzensRemovePacket).setEntries(taterzenList);
            this.sendPacket(taterzensRemovePacket);

            this.taterzens$tablistQueue.clear();

            this.taterzens$skipCheck = false;
        }
    }
}
