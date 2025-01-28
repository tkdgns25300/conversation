package sanghun.project.conversation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sanghun.project.conversation.model.Message;
import sanghun.project.conversation.service.AIChatService;

import java.util.List;

@Controller
public class AIChatController {
    private final AIChatService aiChatService;

    @Autowired
    public AIChatController(AIChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    // chatting page rendering
    @GetMapping("/chat")
    public String chat (Model model) {
        model.addAttribute("messages", aiChatService.getChatHistory());
        return "chat";
    }

    // set subject and generate first message
    @PostMapping("/chat/start")
    @ResponseBody
    public String startChat(@RequestParam("topic") String topic) {
        aiChatService.resetChat();
        aiChatService.addUserMessage(topic);
        return "Chat started";
    }

    // get next ai's response
    @GetMapping("/chat/next")
    public ResponseEntity<List<Message>> getNextMessage() {
        aiChatService.generateNextMessageSync();
        List<Message> messages = aiChatService.getChatHistory();
        return ResponseEntity.ok(messages);
    }
}
