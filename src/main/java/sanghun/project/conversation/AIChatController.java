package sanghun.project.conversation;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AIChatController {
    private final OpenAiChatModel openAiChatModel;
    private final VertexAiGeminiChatModel vertexAiGeminiChatModel;

    @Autowired
    public AIChatController(OpenAiChatModel openAiChatModel, VertexAiGeminiChatModel vertexAiGeminiChatModel) {
        this.openAiChatModel = openAiChatModel;
        this.vertexAiGeminiChatModel = vertexAiGeminiChatModel;
    }

    @GetMapping("/ai/generate/openai")
    public Map<String,String> generateOpen(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", this.openAiChatModel.call(message));
    }

    @GetMapping("/ai/generate/geminiai")
    public Map generateGemini(@RequestParam(value = "message", defaultValue = "Tell me a joke") String message) {
        return Map.of("generation", this.vertexAiGeminiChatModel.call(message));
    }
}
