package com.jobs.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@Import({ JobRouterConfig.class })
public class RootRouteConfig {
	
	@Bean
	public RouterFunction<ServerResponse> routerFunction() {
		
		return RouterFunctions
				.route(RequestPredicates.GET("/"), 
						request -> ServerResponse.ok().build());

	}

}
