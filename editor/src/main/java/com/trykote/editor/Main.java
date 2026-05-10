package com.trykote.editor;

import com.trykote.editor.analysis.ActionCatalogBuilder;
import com.trykote.editor.build.*;

import java.nio.file.Path;

/**
 * Unified entry point: GUI (default) or CLI commands.
 *
 * Usage:
 *   java -jar editor.jar                              → GUI
 *   java -jar editor.jar --gen-screens SRC_DIR OUT.java → generate ScreenDef.java
 *   java -jar editor.jar --gen-palette SRC_DIR OUT.java → generate PaletteKeys.java
 *   java -jar editor.jar --annotate SRC_DIR             → enrich config.json with _-annotations
 *   java -jar editor.jar --catalog SOURCES_DIR OUT.json → build action catalog
 */
public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length == 0 || !args[0].startsWith("--")) {
            // No CLI flags → launch GUI, pass args through
            EditorApp.main(args);
            return;
        }

        String command = args[0];
        switch (command) {
            case "--gen-screens" -> {
                requireArgs(args, 3, "--gen-screens <input-dir> <output.java>");
                ScreenDefGenerator.generate(Path.of(args[1]), Path.of(args[2]));
            }
            case "--gen-palette" -> {
                requireArgs(args, 3, "--gen-palette <input-dir> <output.java>");
                PaletteKeysGenerator.generate(Path.of(args[1]), Path.of(args[2]));
            }
            case "--gen-keys" -> {
                requireArgs(args, 3, "--gen-keys <input-dir> <output-dir>");
                KeysGenerator.generate(Path.of(args[1]), Path.of(args[2]));
            }
            case "--serialize" -> {
                requireArgs(args, 3, "--serialize <input-dir> <output.cfg>");
                CfgSerializer.serialize(Path.of(args[1]), Path.of(args[2]));
            }
            case "--deserialize" -> {
                requireArgs(args, 3, "--deserialize <input.cfg> <output-dir>");
                CfgSerializer.deserialize(Path.of(args[1]), Path.of(args[2]));
            }
            case "--annotate" -> {
                requireArgs(args, 2, "--annotate <input-dir>");
                ScreenAnnotator.annotate(Path.of(args[1]));
            }
            case "--catalog" -> {
                requireArgs(args, 3, "--catalog <sources-dir> <output.json>");
                ActionCatalogBuilder.main(new String[]{args[1], args[2]});
            }
            case "--patch-stringbuilder" -> {
                requireArgs(args, 2, "--patch-stringbuilder <classes-dir>");
                ClassPatcher.patchStringBuilder(Path.of(args[1]));
            }
            case "--patch-version" -> {
                requireArgs(args, 3, "--patch-version <classes-dir> <major.minor>");
                String[] v = args[2].split("\\.");
                int major = Integer.parseInt(v[0]);
                int minor = v.length > 1 ? Integer.parseInt(v[1]) : 0;
                ClassPatcher.patchClassVersion(Path.of(args[1]), major, minor);
            }
            case "--pack-resources" -> {
                requireArgs(args, 3, "--pack-resources <src-dir> <dst-dir>");
                ResourcePacker.pack(Path.of(args[1]), Path.of(args[2]));
            }
            case "--help" -> printHelp();
            default -> {
                System.err.println("Unknown command: " + command);
                printHelp();
                System.exit(1);
            }
        }
    }

    private static void requireArgs(String[] args, int count, String usage) {
        if (args.length < count) {
            System.err.println("Usage: " + usage);
            System.exit(1);
        }
    }

    private static void printHelp() {
        System.out.println("""
            MobileAgent Screen Editor

            GUI mode:
              java -jar editor.jar [resources-src-dir]

            CLI commands:
              --serialize <input-dir> <output.cfg>      Serialize config.json to binary cfg
              --deserialize <input.cfg> <output-dir>    Deserialize binary cfg to config.json
              --gen-screens <input-dir> <output.java>   Generate ScreenDef.java
              --gen-palette <input-dir> <output.java>   Generate PaletteKeys.java
              --gen-keys <input-dir> <output-dir>       Generate all *Keys.java
              --annotate <input-dir>                    Enrich config.json with annotations
              --catalog <sources-dir> <output.json>     Build action catalog
              --patch-stringbuilder <classes-dir>       StringBuilder→StringBuffer in .class
              --patch-version <classes-dir> <M.m>       Patch class version (e.g. 45.3)
              --pack-resources <src-dir> <dst-dir>      Copy images + binary resources
              --help                                    Show this help
            """);
    }
}
