package xyz.safeproxy.listeners;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import xyz.safeproxy.Main;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CaptchaListener implements Listener {
    private final Main plugin;
    private final Map<ProxiedPlayer, String> captchaMap = new HashMap<>();
    private final Map<ProxiedPlayer, Long> cooldowns = new HashMap<>();
    private final Map<ProxiedPlayer, Boolean> captchaSolvedMap = new HashMap<>();
    private final boolean captchaEnabled = true; // Always enabled

    public CaptchaListener(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        if (!captchaEnabled) {
            return;
        }

        try {
            int codeLength = 6; // Length of captcha code
            String captchaCode = generateCaptcha(codeLength);
            captchaMap.put(player, captchaCode);
            captchaSolvedMap.put(player, false);

            player.sendMessage(colorize("&7[&bCaptcha&7] &fProszê wpisz poni¿szy kod, aby kontynuowaæ: &e" + captchaCode));

            plugin.getProxy().getScheduler().schedule(plugin, () -> {
                if (captchaMap.containsKey(player)) {
                    captchaMap.remove(player);
                    player.disconnect(colorize("&7[&bCaptcha&7] &fNie rozwi¹za³eœ(aœ) captchy w wyznaczonym czasie."));
                }
            }, 30, java.util.concurrent.TimeUnit.SECONDS); // 30 seconds timeout

            if (cooldowns.containsKey(player)) {
                long lastJoin = cooldowns.get(player);
                long currentTime = System.currentTimeMillis() / 1000;
                if (currentTime - lastJoin < 60) { // Cooldown of 60 seconds between captchas
                    cooldowns.put(player, currentTime);
                }
            } else {
                cooldowns.put(player, System.currentTimeMillis() / 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerChat(net.md_5.bungee.api.event.ChatEvent event) {
        if (!captchaEnabled || event.isCommand()) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        String message = event.getMessage();

        if (captchaMap.containsKey(player)) {
            String captchaCode = captchaMap.get(player);
            if (message.equals(captchaCode)) {
                player.sendMessage(colorize("&7[&bCaptcha&7] &fCaptcha zosta³a pomyœlnie rozwi¹zana. Mo¿esz teraz czatowaæ."));
                captchaMap.remove(player);
                captchaSolvedMap.put(player, true);
            } else {
                player.sendMessage(colorize("&7[&bCaptcha&7] &fNieprawid³owy kod captchy. Spróbuj ponownie."));
                event.setCancelled(true);
            }
        } else {
            if (!captchaSolvedMap.getOrDefault(player, false)) {
                player.sendMessage(colorize("&7[&bCaptcha&7] &fMusisz rozwi¹zaæ captchê, zanim bêdziesz móg³(a) czatowaæ."));
                event.setCancelled(true);
            }
        }
    }

    private String generateCaptcha(int length) {
        StringBuilder captchaCodeBuilder = new StringBuilder();
        Random rnd = new Random();
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        for (int i = 0; i < length; i++) {
            int index = rnd.nextInt(chars.length());
            captchaCodeBuilder.append(chars.charAt(index));
        }
        return captchaCodeBuilder.toString();
    }

    private String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
