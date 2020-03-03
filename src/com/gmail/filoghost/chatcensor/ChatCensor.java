/*
 * Copyright (c) 2020, Wild Adventure
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 * 4. Redistribution of this software in source or binary forms shall be free
 *    of all charges or fees to the recipient of this software.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gmail.filoghost.chatcensor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class ChatCensor extends Plugin implements Listener {
	
	public static final String BUKKIT_CHANNEL = "ChatCensorConfig";
	public static ChatCensor instance;
	public static byte[] configFileBytes;
	
	@Override
	public void onEnable() {
		instance = this;
		
		getProxy().getPluginManager().registerListener(this, this);
		getProxy().getPluginManager().registerCommand(this, new CommandHandler(this, "chatcensor"));
		try {
			load();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void load() throws IOException {
		if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
		}
		
		File configFile = new File(getDataFolder(), "config.yml");
		if (!configFile.isFile()) {
			 try (InputStream in = getResourceAsStream("config.yml")) {
                 Files.copy(in, configFile.toPath());
             } catch (IOException e) {
                 e.printStackTrace();
                 return;
             }
		}
		configFileBytes = Files.readAllBytes(configFile.toPath());
		BukkitCommunicator.reset();
	}
	
	@EventHandler
	public void onMessage(PluginMessageEvent event) {
		if (event.getTag().equals(BUKKIT_CHANNEL)) {
			event.setCancelled(true); // Solo questo plugin può usare questo channel
		}
	}

	@EventHandler
	public void onJoin(ServerConnectedEvent event) {
		BukkitCommunicator.onServerJoin(event.getServer(), event.getPlayer());
	}
	
}
