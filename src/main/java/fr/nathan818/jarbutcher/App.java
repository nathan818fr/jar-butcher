package fr.nathan818.jarbutcher;

import fr.nathan818.jarbutcher.util.JarUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.jar.JarOutputStream;
import java.util.regex.Pattern;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import picocli.CommandLine;

@CommandLine.Command(
    name = "jar-butcher",
    versionProvider = ImplementationVersionProvider.class,
    mixinStandardHelpOptions = true,
    description = "Remove all code and assets from a JAR file, leaving only class/method/field signatures."
)
public class App implements Callable<Integer> {

    private static final int ASM_VERSION = Opcodes.ASM9;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new App()).execute(args);
        System.exit(exitCode);
    }

    @CommandLine.Parameters(index = "0", paramLabel = "IN", description = "The input jar to butcher")
    private File inFile;

    @CommandLine.Parameters(index = "1", arity = "0..1", paramLabel = "OUT", description = "The output jar to create")
    private File outFile;

    @CommandLine.Option(names = { "-v", "--verbose" }, description = "Be more verbose")
    private boolean verbose;

    @CommandLine.Option(
        names = { "-f", "--filter" },
        arity = "0..*",
        paramLabel = "regex",
        description = "Filter entries names using regex"
    )
    private List<Pattern> filters;

    @Override
    public Integer call() throws Exception {
        if (outFile == null) {
            outFile = new File(inFile.getParentFile(), generateOutputName(inFile.getName()));
        }

        System.out.println("Input file: " + inFile);
        System.out.println("Output file: " + outFile);

        try (
            FileOutputStream outStream = new FileOutputStream(outFile);
            JarOutputStream outJar = new JarOutputStream(outStream)
        ) {
            JarUtil.forEachEntry(inFile, (entry, in) -> {
                if (!entry.getName().endsWith(".class") || !matchFilters(entry.getName())) {
                    // Skip non-class files
                    if (verbose) {
                        System.out.println("Skipping " + entry.getName());
                    }
                    return true;
                }

                // Butcher the class content
                if (verbose) {
                    System.out.println("Processing " + entry.getName());
                }

                byte[] classContent;
                try (InputStream inStream = in.get()) {
                    classContent = butcherClass(inStream);
                }

                // Write the butchered class to the output jar
                outJar.putNextEntry(entry);
                try {
                    outJar.write(classContent);
                } finally {
                    outJar.closeEntry();
                }
                return true;
            });

            if (verbose) {
                System.out.println("Flushing output");
            }
        }

        System.out.println("Done!");
        return 0;
    }

    private boolean matchFilters(String name) {
        if (filters == null || filters.isEmpty()) {
            return true;
        }
        for (Pattern filter : filters) {
            if (filter.matcher(name).find()) {
                return true;
            }
        }
        return false;
    }

    private byte[] butcherClass(InputStream in) throws IOException {
        ClassReader reader = new ClassReader(in);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        ClassVisitor cv = new EmptyBodyClassVisitor(ASM_VERSION, writer);
        reader.accept(cv, 0);
        return writer.toByteArray();
    }

    private static String generateOutputName(String name) {
        String baseName;
        String extension;
        int lastDotIndex = name.lastIndexOf('.');
        if (lastDotIndex > 0) {
            baseName = name.substring(0, lastDotIndex);
            extension = name.substring(lastDotIndex);
        } else {
            baseName = name;
            extension = ".jar";
        }
        return baseName + "-butchered" + extension;
    }
}
