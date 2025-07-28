package com.bonfire;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.apache.ignite.Ignite;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AppLifecycleBean {

    private static final Logger log = Logger.getLogger(AppLifecycleBean.class);

    @Inject
    Ignite ignite;

    public void onStart(@Observes StartupEvent ev) {
        log.info("The application is starting...");
        log.info(STR."Ignite : \{ignite.configuration().toString()}");
    }

    public void onStop(@Observes ShutdownEvent ev) {
        log.info("The application is stopping...");
    }
}
