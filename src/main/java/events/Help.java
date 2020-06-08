package events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
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
        info.setDescription("Simple bot that allows to set reminders.");
        info.setImage("https://www.top13.net/wp-content/uploads/2015/10/perfectly-timed-cat-photos-funny-cover.jpg");
        info.addField(new MessageEmbed.Field("!setRem \"content\" \"hh:mm dd-mm-yyyy\"", "Set reminder for specified date", false));
        info.addField(new MessageEmbed.Field("!delRem id ", "Delete reminder no. id (check !myRem before, id is not static)", false));
        info.addField(new MessageEmbed.Field("!myRem", "Print reminders with id`s.", false));
        info.addField(new MessageEmbed.Field("!help", "Show bot use guide", false));
        info.addField(new MessageEmbed.Field("!ping, !pong", "Ping pong", false));
        info.addField(new MessageEmbed.Field("!hello", "Hello boi", false));
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
