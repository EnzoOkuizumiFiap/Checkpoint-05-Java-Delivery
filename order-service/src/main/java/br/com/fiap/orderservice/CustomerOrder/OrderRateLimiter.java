package br.com.fiap.orderservice.CustomerOrder;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
    O Bucket4j implementa o algoritmo Token Bucket (Balde de Fichas):

    1. O Balde (Capacity = 20): Imagine um balde na porta da sua cozinha que comporta no máximo 20 fichas.
    2. A Torneira (Refill = 20 por segundo): A cada 1 segundo, uma torneira abastece o balde com 20 novas fichas (refillGreedy).
    3. O Cliente (POST /orders): Cada vez que alguém tenta criar um pedido, ele precisa gastar 1 ficha (tryConsume(1)):
    • Se ainda houver ficha no balde: ele consome a ficha e o pedido é criado normalmente.
    • Se chegarem mais de 20 requisições dentro do mesmo segundo: o balde esvazia! As requisições excedentes não conseguem ficha e tomam
    HTTP 429 Too Many Requests na hora, sem nem tocar no banco de dados.

 */
@Component
public class OrderRateLimiter {

    // O "balde" thread-safe que gerencia as fichas/tokens disponíveis
    private final Bucket bucket;

    public OrderRateLimiter() {
        // 1. Configura a largura de banda (banda de consumo):
        //  - capacity(20): capacidade máxima do balde (até 20 tokens acumulados)
        //  - refillGreedy(20, Duration.ofSeconds(1)): a cada 1 segundo, 20 novos tokens são adicionados
        Bandwidth limit = Bandwidth.builder()
                .capacity(20)
                .refillGreedy(20, Duration.ofSeconds(1))
                .build();

        // 2. Constrói a instância do balde com o limite configurado
        this.bucket = Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Tenta consumir 1 ficha (token) do balde.
     * @return true se havia token disponível e foi consumido com sucesso;
     *         false se o balde estava vazio (atingiu mais de 20 req/s no segundo atual).
     */
    public boolean tryAcquire() {
        return bucket.tryConsume(1);
    }
}
