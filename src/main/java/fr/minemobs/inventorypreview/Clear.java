package fr.minemobs.inventorypreview;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Deprecated
/**
 * @deprecated I mark it as deprecated because I might need it in the future, but I don't think it's necessary.
 */
public class Clear {

    public static void main(String[] args) throws URISyntaxException, IOException {
        List<Path> renderedFiles = getPaths(Clear.class.getResource("/grab/rendered/").toURI());
        List<Path> itemFiles = getPaths(Clear.class.getResource("/grab/items/").toURI());
        List<Path> blockFiles = getPaths(Clear.class.getResource("/grab/blocks/").toURI());
        for (Path itemFile : itemFiles) {
            if(renderedFiles.stream().noneMatch(path -> path.getFileName().toString().equals(itemFile.getFileName().toString()))) {
                System.out.println("Creating " + itemFile.getFileName());
                Files.write(Path.of("assets/items/" + itemFile.getFileName()), Files.readAllBytes(itemFile), StandardOpenOption.CREATE);
            }
        }
        for (Path blockFile : blockFiles) {
            if(renderedFiles.stream().noneMatch(path -> path.getFileName().toString().equals(blockFile.getFileName().toString()))) {
                System.out.println("Creating " + blockFile.getFileName());
                Files.write(Path.of("assets/blocks/" + blockFile.getFileName()), Files.readAllBytes(blockFile), StandardOpenOption.CREATE);
            }
        }
        renderedFiles.forEach(path -> {
            System.out.println("Creating " + path.getFileName());
            try {
                Files.write(Path.of("assets/renders/" + path.getFileName()), Files.readAllBytes(path), StandardOpenOption.CREATE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static List<Path> getPaths(URI uri) {
        return Arrays.stream(Objects.requireNonNull(Paths.get(uri).toFile().listFiles((dir, name) -> name.endsWith(".png")))).map(File::toPath).toList();
    }

}
