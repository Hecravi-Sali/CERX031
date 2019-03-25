package me.IAGO.Communication;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class WebsocketController {
	@RequestMapping(value = "/home")
	public String Home(HttpServletRequest req) {
		return "Main";
	}
}
