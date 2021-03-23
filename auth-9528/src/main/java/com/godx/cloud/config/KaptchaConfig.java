package com.godx.cloud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Properties;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

@Configuration
public class KaptchaConfig {

    @Bean
    public Producer kaptcharProducer(){

        Properties pro=new Properties();
        pro.setProperty("kaptcha.image.width","120");
        pro.setProperty("kaptcha.image.height","40");
        pro.setProperty("kaptcha.textproducer.font.size","32");
        pro.setProperty("kaptcha.textproducer.font.color","black");
        pro.setProperty("kaptcha.textproducer.font.string","0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        pro.setProperty("kaptcha.textproducer.font.length","3");
        pro.setProperty("kaptcha.textproducer.char.length","4");
        pro.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");

        DefaultKaptcha kaptcha=new DefaultKaptcha();
        Config config=new Config(pro);
        kaptcha.setConfig(config);
        return kaptcha;
    }

}

