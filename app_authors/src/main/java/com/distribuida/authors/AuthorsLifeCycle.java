package com.distribuida.authors;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.vertx.ext.consul.CheckOptions;
import io.vertx.ext.consul.ConsulClientOptions;
import io.vertx.ext.consul.ServiceOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.consul.ConsulClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.UUID;
@ApplicationScoped
public class AuthorsLifeCycle {


    @ConfigProperty(name = "consul.host", defaultValue = "localhost")
    private String consultHost;

    //necesario para iniciar el port.
    @ConfigProperty(name = "consul.port", defaultValue = "8500")
    private int consultPort;

    @ConfigProperty(name = "quarkus.http.port")
    private int port;

    //id de la instancia y sea unica
    private String serviceId;

    //Observes llama el metodo
    public void init(@Observes StartupEvent evt, Vertx vertx) throws UnknownHostException {
        System.out.println("**AuthorsLifeCycle init**");

        ConsulClient client = ConsulClient.create(vertx,
                new ConsulClientOptions().setHost(consultHost).setPort(consultPort)
        );

        //genera un random extenso
        serviceId = UUID.randomUUID().toString();
        //tambien puedes poner otros atributos for example name addres
        var ipAddress = InetAddress.getLocalHost();
        String httpCheckUrl = String.format("http://%s:%d/authors", ipAddress.getHostAddress(), port);

        client.registerServiceAndAwait(
                new ServiceOptions()
                        .setName("app-authors")
                        .setId(serviceId)
                        .setAddress(ipAddress.getHostAddress())
                        .setPort(port)
                        .setTags(
                                List.of("traefik.enable=true",
                                        "traefik.http.routers.app-authors.rule=PathPrefix(`/app-authors`)",
                                        "traefik.http.routers.app-authors.middlewares=app-authors",
                                        "traefik.http.middlewares.app-authors.stripPrefix.prefixes=/app-authors"
                                )
                        )
                        .setCheckOptions(
                                new CheckOptions()
                                        .setHttp(httpCheckUrl)
                                        .setInterval("10s")
                                        .setDeregisterAfter("20s")
                        )
        );
    }
    //no es necesario porque el consul lo detiene
    public void stop(@Observes ShutdownEvent stevt, Vertx vertx) {
        System.out.println("**AuthorsLifeCycle stop**");

        ConsulClient client = ConsulClient.create(vertx,
                new ConsulClientOptions().setHost(consultHost).setPort(consultPort)
        );

        client.deregisterServiceAndAwait(serviceId);
    }
}