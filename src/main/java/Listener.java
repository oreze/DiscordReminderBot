import events.*;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Listener {
    private JDA api;
    private Hello hello;
    private Help help;
    private Message message;
    private PingPong pingPong;
    private Reminder reminder;

    public Listener(JDA api) {
        this.api = api;
        help = new Help();
        reminder = new Reminder(Paths.get("C:", "dev", "java", "DiscordReminderBot", "src", "main", "resources", "reminders.txt").toString()); //C:\dev\java\DiscordReminderBot\src\main\resources
        message = new Message();
        hello = new Hello();
        pingPong = new PingPong();
    }

    public List<ListenerAdapter> getAll() {
        return Arrays.asList(hello, help, message, pingPong, reminder);
    }

    public Listener init() {
        for (ListenerAdapter el : this.getAll()) {
            api.addEventListener(el);
        }
        return this;
    }

}
