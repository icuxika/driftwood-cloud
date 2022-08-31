package com.icuxika.gateway.config;

import org.springframework.cloud.gateway.route.RouteDefinition;

import java.util.List;

public class RouteDefinitionList {

    private List<RouteDefinition> routes;

    public List<RouteDefinition> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteDefinition> routes) {
        this.routes = routes;
    }
}
