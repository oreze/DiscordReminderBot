import it.sauronsoftware.cron4j.Scheduler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import util.RemindChecker;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.util.TimerTask;

public class Main extends ListenerAdapter {
    public static void main(String[] args) throws LoginException {
        JDA api = JDABuilder.createDefault("token").build();   //
        Listener listener = new Listener(api).init();
        scheduleCheckRemindersToCall(api);
    }

    private static void scheduleCheckRemindersToCall(JDA api) {
        TimerTask task = new RemindChecker(api);
        Scheduler scheduler = new Scheduler();
        scheduler.schedule("* * * * *", task);
        scheduler.start();
    }

    public static int add(int x, int y) {
        return x + y;
    }
}
