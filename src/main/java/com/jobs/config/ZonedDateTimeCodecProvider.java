package com.jobs.config;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ZonedDateTimeCodecProvider implements CodecProvider {

    @Override
    public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
        if (clazz == ZonedDateTime.class) {
            return (Codec<T>) new ZonedDateTimeCodec();
        }
        return null;
    }

    private static class ZonedDateTimeCodec implements Codec<ZonedDateTime> {

        @Override
        public ZonedDateTime decode(org.bson.BsonReader reader, org.bson.codecs.DecoderContext decoderContext) {
            return ZonedDateTime.parse(reader.readString());
        }

        @Override
        public void encode(org.bson.BsonWriter writer, ZonedDateTime value, org.bson.codecs.EncoderContext encoderContext) {
            writer.writeString(value.toString());
        }

        @Override
        public Class<ZonedDateTime> getEncoderClass() {
            return ZonedDateTime.class;
        }
    }
}

