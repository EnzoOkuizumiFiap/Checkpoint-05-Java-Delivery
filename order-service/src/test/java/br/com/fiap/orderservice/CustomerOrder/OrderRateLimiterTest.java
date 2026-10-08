package br.com.fiap.orderservice.CustomerOrder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderRateLimiterTest {

    @Test
    @DisplayName("Deve permitir exatamente 20 requisições e bloquear a 21ª no mesmo segundo")
    void shouldBlockAfter20Requests() {
        OrderRateLimiter limiter = new OrderRateLimiter();

        System.out.println("\n--- INICIANDO TESTE DE RATE LIMIT (BUCKET4J) ---");

        // As primeiras 20 requisições DEVEM ser aprovadas (consomem 20 tokens)
        for (int i = 1; i <= 20; i++) {
            boolean allowed = limiter.tryAcquire();
            System.out.println("Requisição " + i + "/20: ✅ PERMITIDA (Token consumido do balde)");
            assertTrue(allowed, "A requisição " + i + " deveria ter sido permitida");
        }

        // A 21ª requisição no mesmo segundo DEVE ser bloqueada (balde sem tokens)
        boolean blocked = !limiter.tryAcquire();
        System.out.println("Requisição 21: 🛑 BLOQUEADA! -> Retorna HTTP 429 Too Many Requests");
        System.out.println("--- FIM DO TESTE: RATE LIMIT 100% FUNCIONAL ---\n");

        assertTrue(blocked, "A requisição 21 deveria ter sido bloqueada com 429 Too Many Requests");
    }
}
