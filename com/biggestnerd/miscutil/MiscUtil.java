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

@Mod(modid="miscutil", name="Miscellaneous Utilities", version="v1.0.0")
public class MiscUtil {
	@Mod.Instance("MiscUtil")
	public static MiscUtil instance;
	Minecraft mc = Minecraft.getMinecraft();
	
	static boolean renderPigmen;
	static boolean animatePortals;
	static boolean loginOnKick;
	static boolean infoHud;
	static boolean snitchCensor;
	
	ServerData info;
	private String dir = mc.mcDataDir + "/mods/RadarBro/";
	private File enemies = new File(dir, "EnemyList.txt");
	private File allies = new File(dir, "AllyList.txt");
	ArrayList<String> AllyList = new ArrayList();
	ArrayList<String> EnemyList = new ArrayList();
	
	Pattern snitch = Pattern.compile("^ \\* ([a-zA-Z0-9_]+) (?:entered|logged out in|logged in to) snitch at .*? \\[([-]?[0-9]+) ([-]?[0-9]+) ([-]?[0-9]+)\\]$");
	
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		
		renderPigmen = config.get(config.CATEGORY_GENERAL, "renderPigmen", false).getBoolean();
		animatePortals = config.get(config.CATEGORY_GENERAL, "animatePortals", false).getBoolean();
		loginOnKick = config.get(config.CATEGORY_GENERAL, "loginOnKick", true).getBoolean();
		infoHud = config.get(config.CATEGORY_GENERAL, "infoHud", true).getBoolean();
		snitchCensor = config.get(config.CATEGORY_GENERAL, "snitchCensor", true).getBoolean();
		
		config.save();
		
		FMLCommonHandler.instance().bus().register(this);
	    MinecraftForge.EVENT_BUS.register(this);
	    
	    initFiles();
	}
	  
	@SubscribeEvent
	public void onTick(TickEvent.ClientTickEvent event) {
	    if (mc.thePlayer != null && !animatePortals) {
	      mc.thePlayer.timeInPortal = 0.0F;
	    }

	    if(mc.theWorld != null) {
	    	for(Object o : mc.theWorld.loadedEntityList) {
		    	if(o instanceof EntityOtherPlayerMP) {
		    		EntityOtherPlayerMP e = (EntityOtherPlayerMP) o;
		    		e.refreshDisplayName();
		    	}
		    }
	    }
	}
	
	@SubscribeEvent
	public void connectToServer(ClientConnectedToServerEvent e) {
		if(mc.theWorld != null) {
	    	info = mc.func_147104_D();
	    }
	}
	
	@SubscribeEvent
	public void renderLiving(RenderLivingEvent.Pre e) {
		if(e.entity instanceof EntityPigZombie && !renderPigmen) {
			e.setCanceled(true);
		}
	}
	
	@SubscribeEvent
	public void nameFormat(PlayerEvent.NameFormat e) {
		String username = e.username;
		float dist = mc.thePlayer.getDistanceToEntity(e.entity);
		if(EnemyList.contains(username)) {
			e.displayname = EnumChatFormatting.RED + username + " {" + (int)dist + "m}";
		} else if(AllyList.contains(username)) {
			e.displayname = EnumChatFormatting.GREEN + username + " {" + (int)dist + "m}";
		} else {
			e.displayname = username + " {" + (int)dist + "m}";
		}
	}
	/*
	@SubscribeEvent
	public void onChat(ClientChatReceivedEvent e) {
		String msg = e.message.getUnformattedText();
		Matcher snitchMatcher = snitch.matcher(msg);
		while(snitchMatcher.find()) {
			e.setCanceled(true);
			String newmsg = " * " + snitchMatcher.group(1) + " " + snitchMatcher.group(2) + " snitch at [**** ** ****]";
			String s = "";
			String[] m = newmsg.split(" "); 
			for(String t : m){
			s += EnumChatFormatting.AQUA;
			s += t;
			s += " ";
			}
			mc.thePlayer.addChatMessage(new ChatComponentText(s));
		}
	}
	*/
	public void initFiles() {
		if(!enemies.exists()){
    		new File(dir).mkdirs();
    		try {
				enemies.createNewFile();
				try {
					BufferedReader br = new BufferedReader(new FileReader(enemies));
					String s;
					while((s = br.readLine()) != null) {
						EnemyList.add(s);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
		
		if(!allies.exists()){
    		new File(dir).mkdirs();
    		try {
				allies.createNewFile();
				try {
					BufferedReader br = new BufferedReader(new FileReader(allies));
					String s;
					while((s = br.readLine()) != null) {
						AllyList.add(s);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
	}
}
