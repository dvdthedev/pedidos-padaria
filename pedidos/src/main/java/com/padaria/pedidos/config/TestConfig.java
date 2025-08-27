package com.padaria.pedidos.config;
import com.padaria.pedidos.model.Pedido;
import com.padaria.pedidos.repositories.PedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {

    @Autowired
    private PedidoRepository pedidoRepository;


    @Override
    public void run(String... args) throws Exception {
        Pedido p1 = new Pedido(null, "Cachorro Quente", 50.0, 500.0,
                LocalDateTime.of(2025, Month.AUGUST, 28, 17, 30),
                "Deivid Rocha", "31997459829", 40.0);
        Pedido p2 = new Pedido(null, "Cachorro Quente", 150.0, 300.0,
                LocalDateTime.of(2025, Month.AUGUST, 29, 11, 30), "Renato Rocha", "31997459829", 40.0);

        //pedidoRepository.saveAll(Arrays.asList(p1, p2));
    }
}
