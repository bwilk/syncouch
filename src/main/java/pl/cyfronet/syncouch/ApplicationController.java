package pl.cyfronet.syncouch;

import javax.servlet.http.HttpSession;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.cyfronet.syncouch.changes.ChangesProcessing;

@RestController
@EnableAutoConfiguration
public class ApplicationController {

	// TODO eventually changes processing management service schould be used
	// here but for now following design will suffice
	ChangesProcessing changesProcessing = new ChangesProcessing();

	@RequestMapping("/")
	String home(HttpSession httpSession) {
		return "Hello! I am SynCouch!";
	}

	@RequestMapping("/start")
	String start(HttpSession httpSession) {
		changesProcessing.start();
		return "Synchronization started";
	}

	@RequestMapping("/stop")
	String stop(HttpSession httpSession) {
		try {
			changesProcessing.stop();
		} catch (InterruptedException e) {
			return "Synchronization finished with error";
		}
		return "Synchronization finished";
	}

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ApplicationController.class, args);
	}

}