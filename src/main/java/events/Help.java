package events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import util.Util;


public class Help extends ListenerAdapter {
    EmbedBuilder info;

    public Help() {
        info = new EmbedBuilder();
        info.setTitle("Reminder Bot");
        info.setColor(0xfd83ff);
        info.setAuthor("oreze");
        info.setDescription("Simple bot that allows to set reminders");
        info.setImage("https://www.top13.net/wp-content/uploads/2015/10/perfectly-timed-cat-photos-funny-cover.jpg");
    }
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String messageSent = event.getMessage().getContentRaw();
        if (messageSent.equals("!help")) {
            event.getChannel().sendTyping().queue();
            info.setFooter("Created by oreze", event.getAuthor().getAvatarUrl());
            Util.sleep(1000);
            event.getChannel().sendMessage(info.build()).queue();
        }
    }
}
