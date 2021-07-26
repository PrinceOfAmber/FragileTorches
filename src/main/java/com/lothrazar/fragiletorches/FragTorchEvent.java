package com.lothrazar.fragiletorches;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class FragTorchEvent {

  private static final ResourceLocation TAGRL = new ResourceLocation(ModFragileTorches.TAGID);
  private static final Tag.Named<Block> TAGSTATE = BlockTags.bind(TAGRL.toString());

  @SubscribeEvent
  public void onEntityUpdate(LivingUpdateEvent event) {
    Entity ent = event.getEntity();
    if (ent == null) {
      return;
    }
    if (ConfigManager.entityIsGentle(ent.getType())) {
      return;
    }
    if (ent instanceof Player) {
      //i am a player, i can avoid this
      Player p = (Player) ent;
      if (p.isCrouching()) {
        return;// ok // torches are safe from breaking as secret edge case for happiness
      }
    }
    Level world = ent.level;
    if (world.random.nextDouble() > ConfigManager.DOUBLEVALUE.get()) {
      return;
    }
    BlockPos pos = ent.blockPosition();// ent.getPosition();
    BlockState bs = world.getBlockState(pos);
    boolean breakable = bs.is(TAGSTATE);
    if (!breakable && ent.getEyeHeight() >= 1) {
      //also check above at eye level
      pos = pos.above();//so go up one 
      bs = world.getBlockState(pos);
      breakable = bs.is(TAGSTATE);
    }
    if (breakable) {
      //player? 
      //ok break the torch
      ent.level.destroyBlock(pos, true);
    }
  }
}
