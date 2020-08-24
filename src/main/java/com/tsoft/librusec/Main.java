package com.tsoft.librusec;

import com.tsoft.librusec.service.config.Config;
import com.tsoft.librusec.service.library.LibraryReferenceGenerator;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length == 1 && args[0].equalsIgnoreCase("-help")) {
            System.out.println("Usage: java -jar librusec.jar <Path to the library's folder>");
            System.exit(0);
        }

        LibraryReferenceGenerator generator = new LibraryReferenceGenerator();
        Config config = generator.prepareConfig(args.length == 1 ? args[0] : null);
        if (config != null) {
            generator.generate(config);
        }
    }
}
