package com.trykote.editor.build;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.*;
import java.util.Iterator;
import java.util.Map;

/**
 * Copies PNG images and binary resources from resources-src/ to output directory.
 * Replaces: tools/pack_resources.sh
 */
public class ResourcePacker {

    public static void pack(Path srcDir, Path dstDir) throws IOException {
        Files.createDirectories(dstDir);
        packImages(srcDir, dstDir);
        packBinaryResources(srcDir, dstDir);
    }

    private static void packImages(Path srcDir, Path dstDir) throws IOException {
        Path mappingFile = srcDir.resolve("images/mapping.json");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode mapping = mapper.readTree(mappingFile.toFile());

        int count = 0;
        Iterator<Map.Entry<String, JsonNode>> fields = mapping.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String obfuscatedName = entry.getKey();
            String readableName = entry.getValue().asText();

            Path src = srcDir.resolve("images/" + readableName);
            Path dst = dstDir.resolve(obfuscatedName);
            Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
            count++;
        }
        System.out.println("Copied " + count + " images");
    }

    private static void packBinaryResources(Path srcDir, Path dstDir) throws IOException {
        Path blowfish = srcDir.resolve("blowfish_constants.bin");
        if (Files.exists(blowfish)) {
            Files.copy(blowfish, dstDir.resolve("a"), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Copied blowfish_constants.bin -> a");
        }
    }
}
