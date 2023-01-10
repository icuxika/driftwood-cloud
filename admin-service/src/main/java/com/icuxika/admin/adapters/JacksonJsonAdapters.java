package com.icuxika.admin.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.mizosoft.methanol.adapter.ForwardingDecoder;
import com.github.mizosoft.methanol.adapter.ForwardingEncoder;
import com.github.mizosoft.methanol.adapter.jackson.JacksonAdapterFactory;

public class JacksonJsonAdapters {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static class Encoder extends ForwardingEncoder {
        public Encoder() {
            super(JacksonAdapterFactory.createJsonEncoder(mapper));
        }
    }

    public static class Decoder extends ForwardingDecoder {
        public Decoder() {
            super(JacksonAdapterFactory.createJsonDecoder(mapper));
        }
    }
}
