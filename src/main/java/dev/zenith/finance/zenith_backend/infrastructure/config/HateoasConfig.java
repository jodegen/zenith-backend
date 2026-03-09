package dev.zenith.finance.zenith_backend.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.mediatype.hal.HalConfiguration;

@Configuration
public class HateoasConfig {

    /**
     * Forces single-item links to be rendered as objects (not arrays),
     * keeping HAL responses clean and consistent.
     */
    @Bean
    public HalConfiguration halConfiguration() {
        return new HalConfiguration()
                .withRenderSingleLinks(HalConfiguration.RenderSingleLinks.AS_SINGLE);
    }
}
