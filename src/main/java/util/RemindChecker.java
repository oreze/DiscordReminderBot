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

        LocalDateTime reminderTimer;

        for(int i = 0; i < Reminder.reminders.size(); i++) {
            reminderTimer = LocalDateTime.parse(Reminder.reminders.get(i).get(3), dateTimeFormat);
            if (reminderTimer.isBefore(timer)) {
                sendMessage(Reminder.reminders.get(i).get(1), i);
                Reminder.deleteReminder(Reminder.reminders.get(i).get(0));
            }
        }
    }

    private void sendMessage(String userID, int messageNumber) {
        User user = api.getUserById(userID);

        try {
            if (!user.equals(null)) {
                String content = Reminder.reminders.get(messageNumber).get(2);
                String date = Reminder.reminders.get(messageNumber).get(3);

                user.openPrivateChannel().queue((channel) -> channel.sendMessage("<@" + userID + "> " + content + " " + date).queue());
                ;
            }
        }
        catch (NullPointerException ex) {
            System.out.println("There`s no user with that id.");
        }

    }
}
