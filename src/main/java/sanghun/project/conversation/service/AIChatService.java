package sanghun.project.conversation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sanghun.project.conversation.model.Message;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class AIChatService {
    private static final Logger logger = LoggerFactory.getLogger(AIChatService.class);

    private final OpenAiChatModel openAIChatModel;
    private final VertexAiGeminiChatModel vertexAiGeminiChatModel;

    private static final String CHAT_FILE = "chat_history.txt"; // save converstaion's content
    private int currentAiIndex = 0; // current AI index
    private String[] models ={"OpenAI", "GeminiAI"};

    @Autowired
    public AIChatService(OpenAiChatModel openAiChatModel, VertexAiGeminiChatModel vertexAiGeminiChatModel) {
        this.openAIChatModel = openAiChatModel;
        this.vertexAiGeminiChatModel = vertexAiGeminiChatModel;
    }

    // reset conversation content
    public void resetChat() {
        File file = new File(CHAT_FILE);
        if (file.exists()) {
            file.delete();
        }
        currentAiIndex = 0;
    }

    // inject subject
    public void addUserMessage(String topic) {
        Message topicMessage = new Message("User", topic);
        appendMessageToFile(topicMessage);
    }

    // save conversation content
    private void appendMessageToFile(Message message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CHAT_FILE, true ))) {
            writer.write((message.getSender() + ": " + message.getContent()));
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // generate next AI's response
    public void generateNextMessageSync() {
        generateNextMessage();
    }

    public void generateNextMessage() {
        logger.info("generateNextMessage() called");
        logger.info("Current AI index: {}", currentAiIndex);

        List<Message> messages = getChatHistory();
        if (messages.isEmpty()) {
            logger.warn("No messages in chat history. Existing generateNextMessage().");
            return;
        }

        // read content until now
        StringBuilder conversationBuilder = new StringBuilder();
        for (Message msg : messages) {
            conversationBuilder.append(msg.getSender()).append(": ").append(msg.getContent()).append("\n");
        }
        String conversation = conversationBuilder.toString();

        String currentModel = models[currentAiIndex % models.length];
        logger.info("Current AI model : {} (index: {})", currentModel, currentAiIndex);

        String response = "";

        try {
            String prompt = "tell me a joke";
            switch (currentModel) {
                case "OpenAI":
                    response = openAIChatModel.call(prompt);
                    break;
                case "GeminiAI":
                    response = vertexAiGeminiChatModel.call(prompt);
                    break;
                default:
                    logger.warn("Unknown model: {}", currentModel);
                    break;
            }

            if (response != null && !response.isEmpty()) {
                Message aiMessage = new Message(currentModel, response);
                appendMessageToFile(aiMessage);
                logger.info("AI Response from {}: {}", currentModel, response);
            } else {
                logger.warn("No response from {}.", currentModel);
            }

            currentAiIndex++;
            if (currentAiIndex >= models.length) {
                currentAiIndex = 0;
            }
        } catch (Exception e) {
            logger.error("Error generating response from {}: {}", currentModel, e.getMessage(), e);
            e.printStackTrace();
            // add error message into conversation content
            Message errorMessage = new Message(currentModel, "Couldn't generate response because of Error");
            appendMessageToFile(errorMessage);
            currentAiIndex++;
        }
    }

    // read conversation content from file
    public List<Message> getChatHistory() {
        List<Message> messages = new ArrayList<>();
        File file = new File(CHAT_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(CHAT_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    int colonIndex = line.indexOf(':');
                    if (colonIndex > 0) {
                        String sender = line.substring(0, colonIndex);
                        String content = line.substring(colonIndex + 1).trim();
                        messages.add(new Message(sender, content));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return messages;
    }

}
