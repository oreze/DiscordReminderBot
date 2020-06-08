package events;


import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Hello extends ListenerAdapter {
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        String messageSent = event.getMessage().getContentRaw();
        if (messageSent.equalsIgnoreCase("*****") || messageSent.equalsIgnoreCase("\\*\\*\\*\\*\\*")) {
            event.getChannel().sendMessage("***").queue();
        }
        else if (messageSent.equalsIgnoreCase("!hello")) {
            event.getChannel().sendMessage("Hello!").queue();
        }
    }
}
