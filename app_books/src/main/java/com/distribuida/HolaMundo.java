package com.distribuida;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDateTime;

@Path("/hola")
public class HolaMundo {

    @Inject
    @ConfigProperty(name = "app.books.msg", defaultValue = "xx")
    private String message;

    @GET
    public String hola() {
        // API
        Config config = ConfigProvider.getConfig(); // Objeto tipo config, Escanea las fuentes
        config.getConfigSources() // Fuentes disponibles en la aplicacion
                .forEach(t -> {
                    System.out.printf("%d: %s\n", t.getOrdinal(), t.getName());
                });

        var msg = config.getValue("app.books.msg", String.class);
        System.out.println(msg);
        return msg + LocalDateTime.now();

        //       System.out.println(message);
        //       return message + LocalDateTime.now();
    }
}
