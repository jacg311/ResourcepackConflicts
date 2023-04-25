package io.github.jacg311.resourcepackconflicts.client;

import com.github.miachm.sods.Range;
import com.github.miachm.sods.Sheet;
import com.github.miachm.sods.SpreadSheet;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourcepackConflictsClient implements ClientModInitializer {
    public static void logConflicts(ButtonWidget button) {

        try {
            List<String> packIgnoreList;

            Path path = FabricLoader.getInstance().getConfigDir().resolve("resourcepack-conflicts.config");
            if (Files.notExists(path)) {
                packIgnoreList = new ArrayList<>();
            }
            else {
                packIgnoreList = Files.readAllLines(path);
            }

            Map<Identifier, String> map = new HashMap<>();

            File file = FabricLoader.getInstance()
                    .getGameDir()
                    .resolve("resourcepack-conflicts.ods")
                    .toFile();
            SpreadSheet spreadSheet = new SpreadSheet();

            Sheet sheet = new Sheet("Texture Conflicts");
            sheet.appendColumns(2);
            Range range = sheet.getRange(0, 0, 1, 3);
            range.setValues("From Pack", "Overriding Pack", "Texture");
            range.setFontBold(true);

            MinecraftClient.getInstance()
                    .getResourcePackManager()
                    .getEnabledProfiles()
                    .forEach(resourcePackProfile -> {
                        try (ResourcePack pack = resourcePackProfile.createResourcePack()) {
                            pack.getNamespaces(ResourceType.CLIENT_RESOURCES)
                                    .forEach(namespace -> {
                                        String packname = pack.getName();

                                        if (packIgnoreList.contains(packname)) return;

                                        pack.findResources(ResourceType.CLIENT_RESOURCES, namespace, "textures", identifier -> true)
                                                .forEach(identifier -> {
                                                    if (map.get(identifier) != null) {
                                                        sheet.appendRow();
                                                        Range dataRange = sheet.getRange(sheet.getMaxRows() - 1, 0, 1, 3);
                                                        dataRange.setValues(map.get(identifier), packname, identifier);
                                                    }
                                                    else {
                                                        map.put(identifier, packname);
                                                    }
                                                });
                                    });
                        }
                    });

            spreadSheet.appendSheet(sheet);
            spreadSheet.save(file);

            MinecraftClient.getInstance().getToastManager().add(new SystemToast(SystemToast.Type.PERIODIC_NOTIFICATION, Text.of("Saved Texture Data to:"), Text.of(file.toString())));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onInitializeClient() {
    }
}
