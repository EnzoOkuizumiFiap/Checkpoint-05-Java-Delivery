package br.com.fiap.orderservice.Assistant;

import br.com.fiap.orderservice.Assistant.dto.AssistantRequest;
import br.com.fiap.orderservice.Assistant.dto.AssistantResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AssistantController {

    private final AssistantService service;

    @PostMapping("/assistant")
    public AssistantResponse ask(@Valid @RequestBody AssistantRequest request) {
        return new AssistantResponse(service.answer(request));
    }
}
