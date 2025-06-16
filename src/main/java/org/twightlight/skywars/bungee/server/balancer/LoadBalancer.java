package org.twightlight.skywars.bungee.server.balancer;

import org.twightlight.skywars.bungee.server.balancer.elements.LoadBalancerObject;

public interface LoadBalancer<T extends LoadBalancerObject> {

    public T next();
}
