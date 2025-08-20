package com.saucedemo.config;

import org.aeonbits.owner.Config;

@Config.Sources({
    "classpath:config.properties",
    "system:properties"
})
public interface TestConfig extends Config {

    @Key("base.url")
    @DefaultValue("https://www.saucedemo.com")
    String baseUrl();

    @Key("browser")
    @DefaultValue("chromium")
    String browser();

    @Key("headless")
    @DefaultValue("true")
    boolean headless();

    @Key("slow.mo")
    @DefaultValue("0")
    int slowMo();

    @Key("timeout")
    @DefaultValue("30000")
    int timeout();

    @Key("video.mode")
    @DefaultValue("OFF")
    String videoMode();

    @Key("screenshot.mode")
    @DefaultValue("ON_FAILURE")
    String screenshotMode();

    @Key("trace.mode")
    @DefaultValue("ON_FAILURE")
    String traceMode();

    @Key("parallel.workers")
    @DefaultValue("1")
    int parallelWorkers();

    @Key("retry.count")
    @DefaultValue("1")
    int retryCount();
}
