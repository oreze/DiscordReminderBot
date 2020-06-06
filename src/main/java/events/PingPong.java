package events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class PingPong extends ListenerAdapter{
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String messageSent = event.getMessage().getContentRaw();
        if (messageSent.equalsIgnoreCase("!ping"))
            event.getChannel().sendMessage("<@" + event.getAuthor().getId() + "> pong").queue();
        else if (messageSent.equalsIgnoreCase("!pong"))
            event.getChannel().sendMessage("ping").queue();
    }
}
