package events;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reminder extends ListenerAdapter {
    public static ArrayList<ArrayList<String>> reminders;
    DateTimeFormatter dateFormat;

    public Reminder() {
        reminders = new ArrayList<ArrayList<String>>();
        dateFormat = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");
    }

    @Override
    public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        Pattern command = Pattern.compile("(?i)!setRem(?-i) \".*\" \"\\d\\d:\\d\\d \\d\\d-\\d\\d-\\d{4}\"");
        Matcher commandMatcher = command.matcher(message);
        Pattern startWith = Pattern.compile("(?i)!setRem(?-i) .*");
        Matcher startWithMatcher = startWith.matcher(message);

        Pattern deleteCommand = Pattern.compile("(?i)!delRem \\d*");
        Matcher deleteCommandMatcher = deleteCommand.matcher(message);

        if (commandMatcher.matches()) {                                            //Check does message fit to the pattern
            setReminder(event);
        }
        else if (!event.getAuthor().isBot() && startWithMatcher.matches()) {
            event.getChannel().sendMessage("Wrong date format (valid is !remind \"message\" \"hh:mm dd-mm-yyyy\"").queue();
        }
        else if (message.equalsIgnoreCase("!myRem")) {
            printReminders(event);
        }
        else if (deleteCommandMatcher.matches()) {
            deleteReminder(event);
        }
    }

    private void saveMessageToArray(@Nonnull GuildMessageReceivedEvent event, String[] splitMessage) {
        ArrayList<String> reminder = new ArrayList<String>();
        reminder.add(event.getAuthor().getId());        //add author
        reminder.add(splitMessage[1]);                  //add message
        reminder.add(splitMessage[2]);                  //add date to reminder

        reminders.add(reminder);
    }

    private void setReminder(GuildMessageReceivedEvent event) {
        String[] splittedMessage = event.getMessage().getContentRaw().split("\" \"| \"|\"");         //Split message into parts (command, message, time)
        LocalDateTime localTimeNow = LocalDateTime.now();
        LocalDateTime localTimeInput;

        try {
            localTimeInput = LocalDateTime.parse(splittedMessage[2], dateFormat);
            if (localTimeInput.isAfter(localTimeNow)) {
                saveMessageToArray(event, splittedMessage);
                String personalisedMessage = "<@" + event.getAuthor().getId() + "> reminder has been set \"" + splittedMessage[1] + "\" for \"" + splittedMessage[2] + "\".";
                event.getChannel().sendMessage(personalisedMessage).queue();
            }
            else {
                event.getChannel().sendMessage("You can`t set the date that has passed in the reminder").queue();
            }
        }
        catch (DateTimeParseException ex) {
            event.getChannel().sendMessage(ex.getMessage()).queue();
        }
    }

    public void printReminders(@Nonnull GuildMessageReceivedEvent event) {
        String authorId = event.getAuthor().getId();
        int numberOfReminder = 0;

        for(int i = 0, j = 1; i < reminders.size(); i++) {
            if (reminders.get(i).get(0).equals(authorId)) {
                numberOfReminder++;
                if (numberOfReminder == 1)
                    event.getChannel().sendMessage("<@" + authorId + "> your reminders:").queue();

                event.getChannel().sendMessage("ID: " + j + ", date: " + reminders.get(i).get(2) + ", content: \"" + reminders.get(i).get(1) + "\"").queue();
                j++;
            }
        }

        if (numberOfReminder == 0)
            event.getChannel().sendMessage("You have no reminders.").queue();
    }

    private boolean deleteReminder(@Nonnull GuildMessageReceivedEvent event) {
        String[] message = event.getMessage().getContentRaw().split(" ");
        String authorID = event.getAuthor().getId();

        for(int i = 0, j = 1; i < Reminder.reminders.size(); i++) {
            if (Reminder.reminders.get(i).get(0).equals(authorID)) {
                if (message[1].equals(Integer.toString(j))) {
                    event.getChannel().sendMessage("Reminder no. " + (message[1]+1) + " was deleted (content: \"" +
                            Reminder.reminders.get(i).get(1) + "\", date: \"" + Reminder.reminders.get(i).get(2) + ").").queue();
                    Reminder.reminders.remove(i);
                    return true;
                }
                j++;
            }
        }

        event.getChannel().sendMessage("There`s no reminder no. " + message[1] +  ".").queue();
        return false;
    }

}
