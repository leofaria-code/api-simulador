package br.com.leo.apisimulador.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
public class HomeController {

    @GetMapping("/")
    public RedirectView home() {
        return new RedirectView("/swagger-ui.html");
    }

    @GetMapping("/api")
    public RedirectView api() {
        return new RedirectView("/swagger-ui.html");
    }
}