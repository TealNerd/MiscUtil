package com.biggestnerd.miscutil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;

@Mod(modid="miscutil", name="Miscellaneous Utilities", version="v1.2")
public class MiscUtil {
	@Mod.Instance("MiscUtil")
	public static MiscUtil instance;
	Minecraft mc = Minecraft.getMinecraft();
	
	static boolean renderPigmen;
	static boolean animatePortals;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		renderPigmen = config.get(config.CATEGORY_GENERAL, "renderPigmen", false).getBoolean();
		animatePortals = config.get(config.CATEGORY_GENERAL, "animatePortals", false).getBoolean();
		
		config.save();
		
		FMLCommonHandler.instance().bus().register(this);
	    MinecraftForge.EVENT_BUS.register(this);
	    
	}
	  
	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
	    if (mc.thePlayer != null && !animatePortals) {
	      mc.thePlayer.timeInPortal = 0.0F;
	    }
	}
	
	@SubscribeEvent
	public void renderLiving(RenderLivingEvent.Pre e) {
		if(e.entity instanceof EntityPigZombie && !renderPigmen) {
			e.setCanceled(true);
		}
	}
}
