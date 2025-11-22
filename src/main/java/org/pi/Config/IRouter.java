package org.pi.Config;

import io.javalin.Javalin;

@FunctionalInterface
public interface IRouter {
    void register(Javalin app);
}
