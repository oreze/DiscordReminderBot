package util;

import events.Reminder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimerTask;

public class RemindChecker extends TimerTask {
    private JDA api;

    public RemindChecker(JDA api) {
        this.api = api;
    }
    @Override
    public void run() {
        LocalDateTime timer = LocalDateTime.now();
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");
        String actualFormatedTime = timer.format(dateTimeFormat);
        for(int i = 0; i < Reminder.reminders.size(); i++) {
            if (Reminder.reminders.get(i).get(2).equals(actualFormatedTime)) {
                sendMessage(Reminder.reminders.get(i).get(0), i);
            }
        }
    }

    private void sendMessage(String userID, int messageNumber) {
        User user = api.getUserById(userID);

        if (!user.equals(null)) {
            String content = Reminder.reminders.get(messageNumber).get(1);
            String date = Reminder.reminders.get(messageNumber).get(2);

            user.openPrivateChannel().queue((channel) -> channel.sendMessage("<@" + userID + "> " + content + " " + date).queue());;
        }

    }
}
