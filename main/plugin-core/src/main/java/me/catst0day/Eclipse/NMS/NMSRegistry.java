package me.catst0day.Eclipse.NMS;

import me.catst0day.Eclipse.Utils.Util;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;


public final class NMSRegistry {

    private static NMSHandler handler;
    private static final Map<String, Supplier<NMSHandler>> handlerFactories = new HashMap<>();

    static {
        registerHandler("v1_21_R1", NMS_v1_21_R1::new);
        registerHandler("v1_20_R4", NMS_v1_20_R4::new);
        registerHandler("v1_20_R3", NMS_v1_20_R3::new);
        registerHandler("v1_20_R2", NMS_v1_20_R2::new);
        registerHandler("v1_20_R1", NMS_v1_20_R1::new);
        registerHandler("v1_19_R4", NMS_v1_19_R4::new);
        registerHandler("v1_19_R3", NMS_v1_19_R3::new);
        registerHandler("v1_19_R2", NMS_v1_19_R2::new);
        registerHandler("v1_19_R1", NMS_v1_19_R1::new);
        registerHandler("v1_18_R2", NMS_v1_18_R2::new);
        registerHandler("v1_18_R1", NMS_v1_18_R1::new);
        registerHandler("v1_17_R1", NMS_v1_17_R1::new);
    }

    private NMSRegistry() {}

    
    public static void registerHandler(String version, Supplier<NMSHandler> factory) {
        handlerFactories.put(version, factory);
    }

    
    public static NMSHandler getHandler() {
        if (handler != null) {
            return handler;
        }

        String nmsVersion = NMSVersion.getNmsVersion();
        Supplier<NMSHandler> factory = handlerFactories.get(nmsVersion);

        if (factory != null) {
            handler = factory.get();
            Util.log("Loaded NMS handler for version: " + nmsVersion);
            return handler;
        }

        
        Util.log("No specific NMS handler found for " + nmsVersion + ", using fallback");
        handler = new NMSFallback();
        return handler;
    }

    
    @Nullable
    public static NMSHandler getHandlerOrNull() {
        return handler;
    }

    
    public static void setHandler(NMSHandler handler) {
        NMSRegistry.handler = handler;
    }

    
    public static void reset() {
        handler = null;
    }
}
