package runners;

import readers.SharedFileReader;
import writers.SharedFileWriter;

public class CobolApplicationRunner {

    private static final String DOCKER_RUN = "docker run --name testpipe --ipc=\"host\" -v /home/uliana/Documents/lgi/docker:/home/uliana/Documents/lgi/docker outintest";
    private static final String DOCKER_REMOVE = "docker rm -f testpipe";

    private static final String INPUT_PATH = "/home/egor/COBOL_WORKS/test_fifo/TEMP/FILE.in";
    private static final String OUTPUT_PATH = "/home/egor/COBOL_WORKS/test_fifo/TEMP/FILE.out";

    private static final String TEXT = "xnzxmzxvfzxhoqesnkczhwwxkhnkcglogfrmbzrvdpgfetphvbmyztuarxkkzgxmnkfrzdsioecwnipdzorkgvvkjiqxhmpxpahukutrsovlbmtbausqxfgusfzdhcdvudnadvkgpwtdwnpqqzylmetrvnhdknfuawzlyupivdaqnnbmwmpmwhkpoqpbymjwnbtptlcrnvhsijvgdzgsazskgdcfgpmuzcmtruyxxxxrwweulfjkqzbaydvrcchsyrotevjqydwdftcjjxcfczzvbedzpbyikeffqmheqrssjjneirfwvtchxgbeldzggnwxujdfxgtqkmhmfczgnbhcqgiksupmokxkrpsxhfbsupznwhzufavtjmiyaudllksnodlgqljpjdeassqdtskalawlnwfacrfvkmrmzhrvkbvmswtcchsxdsdmebzhisoqtajzaqxjnjpeevursgwqusiiljzjiarnsbndunprrmoylaobytlncnzjtfjknqmqkgqqvsretqidepykvvyqplshvakxzgsvaldrqjjhiwscpuupohbqhqmqkvwsmwndnvfxcczubcgpbdpzsnxglitfyqfmscdmongfclqhfcwfmluwekfaqejafruzzycfabrqdglbdxrtrdbqnvmxbjtslvmvmjlfsxzvxbzrwbefzzpujzrnmsoklifktdusibdktoetavhxszjxuzzsxjkugaduuzpsigpkaiccocbimwahfqwcqfjxktdvkxgaaolxjkbuhguuvgfiwvobpclherdmzqqdpbikjxeohwssidrhdbyidgnbdxsjrijtdspfpdhbeifowlpksmoxewxvppefutlqvrxgbabsxiipstieeuycgpddbqivrnzdvwjohkvhofvpitehhuqsxscptsttnizeprbjwfrzismtrrbjisfeoynekjsshiawmegfakrmpslvfqbmxkpxhlmvlwcdvvxgysle\n";

    public static void runCobolApp() {

        System.out.println("Application listener is running");
        long start = System.nanoTime();
        executeApp();
        long finish = System.nanoTime();
        System.out.println("Application listener stops");
        long traceTime = finish - start;
        System.out.println("Executing time is " + traceTime / 1e06 + " ms");
    }

    private static void executeApp() {

        try {
            System.out.println("Java launching the cobol application...");

            Runtime.getRuntime().exec(DOCKER_RUN);
            SharedFileWriter sharedFileWriter = new SharedFileWriter();
            for (int i = 0; i < 64; i++) {
                sharedFileWriter.writeToFile(INPUT_PATH, TEXT);
            }

            SharedFileReader sharedFileReader = new SharedFileReader();
            System.out.println(sharedFileReader.readFile(OUTPUT_PATH));

            Runtime.getRuntime().exec(DOCKER_REMOVE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
