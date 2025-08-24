package br.com.leo.apisimulador.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Serializador específico para valores monetários que força sempre 2 casas
 * decimais.
 */
public class MoneySerializer extends JsonSerializer<BigDecimal> {

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        // Força sempre 2 casas decimais usando setScale
        BigDecimal scaledValue = value.setScale(2, java.math.RoundingMode.HALF_UP);
        gen.writeNumber(scaledValue);
    }
}
