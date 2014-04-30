import junit.framework.TestCase;

import java.io.*;

/**
 * Author: Khamidullin Kamil
 * Date: 30.04.14
 * Time: 16:14
 */
public class MainTest extends TestCase {
    private final static String outputValidResult =
            "-------- result ---------\n" +
            "sergey_martynov 3\n" +
            "batman 2\n" +
            "rustem_bedretdinov 2\n" +
            "guest123 1\n";


    public void testFileInputOutput() throws Exception {
        OutputStream outputStream = new ByteArrayOutputStream();

        //create app
        Main.IOApp app = new Main.IOApp(
                new FileInputStream(new File("test_raw_data")),
                outputStream
        );
        //run app
        app.process();

        String output = outputStream.toString();


        assertTrue(output.equals(outputValidResult));
    }


    public void testDefaultInputOutput() throws Exception {
        final String rawString = "sergey_martynov rustem_bedretdinov sergey_martynov guest123 sergey_martynov rustem_bedretdinov BATMAN batman";

        OutputStream outputStream = new ByteArrayOutputStream();
        //create app
        Main.IOApp app = new Main.IOApp(
                new ByteArrayInputStream(rawString.getBytes()),
                outputStream
        );
        //run app
        app.process();

        String output = outputStream.toString();

        assertTrue(output.equals(outputValidResult));
    }
}
