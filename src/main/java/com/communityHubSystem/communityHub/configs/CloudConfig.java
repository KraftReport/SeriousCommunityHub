package com.communityHubSystem.communityHub.configs;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class CloudConfig {

    private final String CLOUD_NAME = "dws6icl91";
    private final String API_KEY = "861442768946264";
    private final String API_SECRET = "64TznD35NYGem1UsVLxz4GD5e88";

    @Bean
    public Cloudinary cloudinary(){
        var map = new HashMap<String ,String >();
        map.put("cloud_name",CLOUD_NAME);
        map.put("api_key",API_KEY);
        map.put("api_secret",API_SECRET);
        return new Cloudinary(map);
    }
}
