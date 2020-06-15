package events;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;
import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Reminder extends ListenerAdapter {
    public static ArrayList<ArrayList<String>> reminders;
    public static File container;
    private static long messageId;
    private final DateTimeFormatter dateFormat;

    public Reminder(String containerPath) {
        reminders = new ArrayList<>();
        dateFormat = DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy");
        container = new File(containerPath);
    }

    @Override
    public void onReady(@Nonnull ReadyEvent event) {
        readFromFile();
        System.out.println("Bot has been restarted.");
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
            String[] splittedMessage = event.getMessage().getContentRaw().split(" ");
            deleteReminder(event, splittedMessage[1]);          //pass message id to the function
        }
    }

    private void saveReminderToArray(@Nonnull GuildMessageReceivedEvent event, String[] splitMessage) {
        ArrayList<String> reminder = new ArrayList<>();
        reminder.add(String.valueOf(messageId));        //add message ID
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
                saveReminderToArray(event, splittedMessage);
                saveToFile(event, splittedMessage);
                String personalisedMessage = "<@" + event.getAuthor().getId() + "> reminder no. " + messageId + " has been set, content:\"" + splittedMessage[1] + "\", date: \"" + splittedMessage[2] + "\".";
                event.getChannel().sendMessage(personalisedMessage).queue();
                messageId++;
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

        for (ArrayList<String> reminder : reminders) {
            if (reminder.get(1).equals(authorId)) {
                numberOfReminder++;
                if (numberOfReminder == 1) {
                    event.getChannel().sendMessage("<@" + authorId + "> your reminders:").queue();
                }

                event.getChannel().sendMessage("ID: " + reminder.get(0) + ", date: " + reminder.get(3) + ", content: \"" + reminder.get(2) + "\"").queue();
            }
        }

        if (numberOfReminder == 0)
            event.getChannel().sendMessage("You have no reminders.").queue();
        else
            event.getChannel().sendMessage("That`s all.").queue();

    }

    private void deleteReminder(@Nonnull GuildMessageReceivedEvent event, String id) {
        String authorID = event.getAuthor().getId();
        boolean wasRemoved = false;

        for(int i = 0; i < reminders.size(); i++) {
            if (reminders.get(i).get(0).equals(id) && reminders.get(i).get(1).equals(authorID)) {
                event.getChannel().sendMessage("<@" + authorID + ">Reminder no. " + id + " was deleted (content: \"" +
                        reminders.get(i).get(2) + "\", date: \"" + reminders.get(i).get(3) + ").").queue();
                reminders.remove(i);
                removeFromFile(event, id);
                wasRemoved = true;
            }
        }

        if (!wasRemoved)
            event.getChannel().sendMessage("There`s no reminder no. " + id +  ".").queue();
    }

    public static void deleteReminder(String id) {
        for (int i = 0; i < reminders.size(); i++) {
            if (reminders.get(i).get(0).equals(id)) {
                reminders.remove(i);
                removeFromFile(id);
            }
        }
    }

    private void saveToFile(GuildMessageReceivedEvent event, String[] splittedMessage) {
        try (FileWriter output = new FileWriter(container, true)) {
            String outputReminder = messageId + ";" + event.getAuthor().getId() + ";" + splittedMessage[1] + ";" + splittedMessage[2] + "\n";
            output.append(outputReminder);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void removeFromFile(GuildMessageReceivedEvent event, String id) {
        String authorID = event.getAuthor().getId();

        try {
            List<String> fileLines = Files.readAllLines(container.toPath());
            String[] splittedMessage;
            StringBuffer buff = new StringBuffer();

            for(String el : fileLines) {
                splittedMessage = el.split(";");
                if (!splittedMessage[0].equals(id) || !splittedMessage[1].equals(authorID))
                    buff = buff.append(el + "\n");
            }

            BufferedWriter output = new BufferedWriter(new FileWriter(container));
            output.write(buff.toString());
            output.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void removeFromFile(String id) {
        try (BufferedReader input = new BufferedReader(new FileReader(container))){
            String actualLine;
            String[] splittedMessage;
            StringBuffer buff = new StringBuffer();

            while((actualLine = input.readLine()) != null) {
                splittedMessage = actualLine.split(";");
                if (!splittedMessage[0].equals(id))     //ID == id from command && message`s user id == user id
                    buff = buff.append(actualLine + "\n");
            }

            BufferedWriter output = new BufferedWriter(new FileWriter(container));
            output.write(buff.toString());
            output.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFromFile() {
        String actualLine;
        String[] splittedReminder;
        long actualId = 0;

        try (BufferedReader input = new BufferedReader(new FileReader(container))) {
            while ((actualLine = input.readLine()) != null) {
                ArrayList<String> reminder = new ArrayList<>();
                splittedReminder = actualLine.split(";");
                actualId = Long.parseLong(splittedReminder[0]) + 1;

                Collections.addAll(reminder, splittedReminder);
                reminders.add(reminder);
            }

        }
        catch(IOException ex) {
            ex.printStackTrace();
        }
        messageId = actualId;
    }
}