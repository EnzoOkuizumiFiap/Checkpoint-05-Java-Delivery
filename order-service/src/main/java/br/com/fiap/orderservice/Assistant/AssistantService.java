package br.com.fiap.orderservice.Assistant;

import br.com.fiap.orderservice.Assistant.dto.AssistantRequest;
import br.com.fiap.orderservice.Dish.Dish;
import br.com.fiap.orderservice.Dish.DishRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AssistantService {

    private final ChatClient chatClient;
    private final DishRepository dishRepository;

    public String answer(AssistantRequest request) {
        String menu = buildMenu(dishRepository.findAll());

        String prompt = """
                Current restaurant menu:
                %s

                Customer question:
                %s
                """.formatted(menu, request.question());

        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();
    }

    private String buildMenu(List<Dish> dishes) {
        if (dishes.isEmpty()) {
            return "No dishes are currently available.";
        }

        return dishes.stream()
                .map(dish -> "- %s | %s | price: R$ %.2f | stock: %d"
                        .formatted(
                                dish.getName(),
                                dish.getDescription(),
                                dish.getPrice(),
                                dish.getStock()))
                .collect(Collectors.joining("\n"));
    }
}
