package sanghun.project.conversation;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SpringBootApplication
public class ConversationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConversationApplication.class, args);
	}

	@PostConstruct
	public void init() {
		File file = new File("chat_history.txt");
		if (file.exists()) {
			try (FileWriter writer = new FileWriter(file)) {
				writer.write("");
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			file.delete();
		}
	}
}
