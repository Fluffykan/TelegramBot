package tutorial;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class Bot extends TelegramLongPollingBot {
    private boolean isScreaming = false;

    private InlineKeyboardMarkup keyboardM1;
    private InlineKeyboardMarkup keyboardM2;

    // Buttons
     private InlineKeyboardButton next = InlineKeyboardButton.builder()
            .text("Next") // what the user sees
            .callbackData("next") // what is sent back as a new Update
            .build();
    private InlineKeyboardButton back = InlineKeyboardButton.builder()
            .text("Back").callbackData("back")
            .build();
    private InlineKeyboardButton url = InlineKeyboardButton.builder()
            .text("Tutorial")
            .url("https://core.telegram.org/bots/api") // opens the link when clicked
            .build();

    // Building Keyboards
    {
        keyboardM1 = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(next)).build();
        keyboardM2 = InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(back, url)).build();
    }

    @Override
    public String getBotUsername() {
        return "Notey";
    }

    @Override
    public String getBotToken() {
        return "7588013279:AAFzCSKD1Q-L5A_ov8y7JcO2zGBm_MtP1Hw";
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message msg = update.getMessage();
        User user = msg.getFrom();
        Long id = user.getId();

        if (msg.isCommand()) {
            switch (msg.getText()) {
            case "/scream":
                isScreaming = true;
                break;
            case "/whisper":
                isScreaming = false;
                break;
            case "/menu":
                sendMenu(id, "<b> Menu 1</b>", keyboardM2);
            default:
                // do nothing
            }

            return; // do not echo commands
        }


        if (isScreaming) {
            scream(id, msg);
        } else {
            copyMessage(id, msg.getMessageId());
        }

    }

    private void handleButtonTap(Long id, String queryId, String data, int msgId) {
        EditMessageText newText = EditMessageText.builder()
                .chatId(id.toString())
                .messageId(msgId).text("").build();

        EditMessageReplyMarkup newKb = EditMessageReplyMarkup.builder()
                .chatId(id.toString()).build();
    }

    public void sendMenu(Long who, String text, InlineKeyboardMarkup kb) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString())
                .parseMode("HTML").text(text)
                .replyMarkup(kb).build();

        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void scream(Long id, Message msg) {
        if (msg.hasText()) {
            sendText(id, msg.getText().toUpperCase());
        } else {
            copyMessage(id, msg.getMessageId());
        }
    }

    public void echo(Update update) {
        Message msg = update.getMessage();
        User user = msg.getFrom();

        sendText(user.getId(), "echo: " + msg.getText());
    }

    public void copyMessage(Long who, Integer msgId) {
        CopyMessage cm = CopyMessage.builder()
                .fromChatId(who.toString())
                .chatId(who.toString())
                .messageId(msgId)
                .build();
        try {
            execute(cm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) // Who we are sending the msg to
                .text(what).build(); // Msg content

        try {
            execute(sm); // Send the msg
        } catch (TelegramApiException e) {
            throw new RuntimeException(e); // Any errors
        }
    }
}
