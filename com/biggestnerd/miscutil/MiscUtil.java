package com.biggestnerd.miscutil;

import java.awt.Color;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.NameFormat;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

@Mod(modid="miscutil", name="Miscellaneous Utilities", version="v1.3")
public class MiscUtil {
	@Mod.Instance("MiscUtil")
	public static MiscUtil instance;
	Minecraft mc = Minecraft.getMinecraft();
	ServerData last;
	int counter = 0;
	int tps = 20;
	int tpsloop = 400;
	Pattern tpsPattern = Pattern.compile("^TPS from last 1m, 5m, 15m: [*]?([0-9]+).*$");
	
	static boolean renderPigmen = false;
	static boolean animatePortals = false;
	static boolean autoJoin = true;
	static boolean nameDist = true;
	static boolean noInvis = true;
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
		FMLCommonHandler.instance().bus().register(this);
	    MinecraftForge.EVENT_BUS.register(this);
	    
	}
	  
	@SubscribeEvent
	public void renderOverlay(RenderGameOverlayEvent event) {
		if(event.type == RenderGameOverlayEvent.ElementType.EXPERIENCE && last.serverIP.contains("mc.civcraft.vg")) {
			int h = 6;
			if(tps >= 18)
				mc.fontRenderer.drawStringWithShadow("TPS: " + tps, 50, h, Color.GREEN.getRGB());
			if(tps >= 13 && tps < 18)
				mc.fontRenderer.drawStringWithShadow("TPS: " + tps, 50, h, Color.YELLOW.getRGB());
			if(tps < 13)
				mc.fontRenderer.drawStringWithShadow("TPS: " + tps, 50, h, Color.RED.getRGB());
		}
	}
	
	@SubscribeEvent
	public void onChat(ClientChatReceivedEvent event) {
		String msg = event.message.getUnformattedText();
		Matcher tpsMatcher = tpsPattern.matcher(msg);
		while(tpsMatcher.find()) {
			event.setCanceled(true);
			tps = Integer.parseInt(tpsMatcher.group(1));
		}
	}
	
	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
	    if (mc.thePlayer != null && !animatePortals) {
	      mc.thePlayer.timeInPortal = 0.0F;
	    }
	    if(mc.theWorld != null) {
	    	List players = mc.theWorld.loadedEntityList;
		      for(Object o : players) {
		    	if(o instanceof EntityOtherPlayerMP) {
		    		EntityOtherPlayerMP eop = (EntityOtherPlayerMP) o;
		    		eop.refreshDisplayName();
		    		if(eop.isInvisibleToPlayer(mc.thePlayer)) {
		    			eop.setInvisible(false);
		    		}
		    	}
		      }
		      if(last.serverIP.contains("mc.civcraft.vg")) {
		    	  if(tpsloop >= 420) {
		    		  mc.thePlayer.sendChatMessage("/tps");
		    		  tpsloop = 0;
		    	  } else {
		    		  tpsloop++;
		    	  }
		      }
	    }
	    if(mc.currentScreen instanceof GuiDisconnected) {
			if(counter <= 420) {
				counter++;
			}
		}
		if(mc.currentScreen instanceof GuiConnecting && mc.func_147104_D() != null) {
			last = mc.func_147104_D();
		}
		if(counter >= 420 && autoJoin) {
			mc.displayGuiScreen(new GuiConnecting(mc.currentScreen, mc, last));
			System.out.println("Connecting to " + last.serverIP);
			counter = 0;
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void nameFormat(NameFormat e) {
		if(nameDist) {
			e.displayname += " (" + (int) mc.thePlayer.getDistanceToEntity(e.entity) + "m)";
		}
	}
	
	@SubscribeEvent
	public void renderLiving(RenderLivingEvent.Pre e) {
		if(e.entity instanceof EntityPigZombie && !renderPigmen) {
			e.setCanceled(true);
		}
	}
}
