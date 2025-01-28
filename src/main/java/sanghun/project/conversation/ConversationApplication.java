package sanghun.project.conversation;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class ConversationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConversationApplication.class, args);
	}

	@PostConstruct
	public void init() {
		File file = new File("chat_history.txt");
		if (!file.exists()) {
			file.delete();
		}
	}
}
