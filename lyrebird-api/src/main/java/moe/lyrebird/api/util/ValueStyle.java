package moe.lyrebird.api.util;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
@JsonDeserialize
@Value.Style(
        get = {"is*", "get*"},
        optionalAcceptNullable = true,
        privateNoargConstructor = true,
        passAnnotations = {JsonTypeName.class, JsonPropertyOrder.class, JsonProperty.class},
        strictBuilder = true,
        forceJacksonPropertyNames = false
)
public @interface ValueStyle {

}
