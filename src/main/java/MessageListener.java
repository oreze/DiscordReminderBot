import events.*;
import it.sauronsoftware.cron4j.Scheduler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import util.RemindChecker;

import javax.security.auth.login.LoginException;
import java.util.TimerTask;

public class MessageListener extends ListenerAdapter {
    public static void main(String[] args) throws LoginException {
        //TODO !help command
        //TODO save remind requests to file
        JDA api = JDABuilder.createDefault("NzEwNjcyODMyNzQwNDU4NTU4.Xt5STQ.irZIFy7XZiTpq-ATNkgmlAyVAPE").build();
        api.addEventListener(new Hello());
        api.addEventListener(new Message());
        api.addEventListener(new PingPong());
        api.addEventListener(new Reminder("src\\main\\resources\\reminders.txt"));
        api.addEventListener(new Help());
        scheduleCheckRemindersToCall(api);
    }

    private static void scheduleCheckRemindersToCall(JDA api) {
        TimerTask task = new RemindChecker(api);
        Scheduler scheduler = new Scheduler();
        scheduler.schedule("* * * * *", task);
        scheduler.start();
    }

}
