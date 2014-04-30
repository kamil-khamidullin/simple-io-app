import java.io.*;
import java.util.*;

public class Main {
    final static String KEY_FILE = "-f";

	public static void main(String[] args) {
        try {
            //create app
            IOApp app = new IOApp(
                    inputStreamFactory(args),
                    new DefaultOutputStreamProvider().provide()
            );
            //run app
            app.process();
        } catch (IOException ex) {
            System.out.println(String.format("Error: %s", ex.getMessage()));
        }
	}

    /**
     * Create input stream by args
     * @param args
     * @return
     * @throws IOException
     */
	private static InputStream inputStreamFactory(String[] args) throws IOException {
        if(args.length == 0) {
            return new DefaultInputStreamProvider().provide();
        } else if(args.length == 2) {

            final String key = args[0];

            //todo create other input streams
            if(key.equals(KEY_FILE)) {
                return new FileInputStreamProvider(new File(args[1])).provide();
            } else {
                throw new IOException(String.format("Wrong arg key '%s'", key));
            }

        } else {
            throw new IOException("Wrong arg key(s)");
        }
	}

    /**
     * simple logs processor class
     */
    public static class IOApp {
	    private InputStream inputStream;
	    private OutputStream outputStream;
        public final static String DELIMITER = " ";

	    public IOApp(InputStream inputStream, OutputStream outputStream) throws IOException {
		   this.inputStream = inputStream;
		   this.outputStream = outputStream;
	    }

        public InputStream getInputStream() {
           return inputStream;
        }

        public OutputStream getOutputStream() {
           return outputStream;
        }

        /**
         * write notice message
         * @param err
         * @throws IOException
         */
        private void writeNotice(String err) throws IOException {
           String message = String.format("Notice: %s", err);
            getOutputStream().write(message.getBytes());
        }

        /**
         * write result message
         * @param result
         * @throws IOException
         */
        private void writeResult(Map<String, Integer> result) throws IOException {
            StringBuilder resultString = new StringBuilder();
            resultString.append("-------- result ---------\n");
            for(Map.Entry<String, Integer> entry :  result.entrySet()) {
                String login = entry.getKey();

                /*//capitalize login
                login = login.substring(0, 1).toUpperCase()
                        + login.substring(1);*/

                resultString.append(String.format("%s %s\n", login, entry.getValue()));
            }

            getOutputStream().write(resultString.toString().getBytes());
        }

        /**
         * prepare raw data for calculate
         * @param rawData
         * @return
         */
        private String[] prepareData(String rawData) {
            rawData = rawData.replace("\n", "");
            String[] logins =  rawData.split(DELIMITER);

            for(int j = 0; j < logins.length; j++) {
                logins[j] = logins[j].toLowerCase();
            }

            return logins;
        }

        public void process() throws IOException {
            InputStreamReader inputStreamReader = new InputStreamReader(getInputStream());
            StringBuilder sb = new StringBuilder();
            try {
               do {
                   sb.append((char)inputStreamReader.read());
               } while (inputStreamReader.ready());

               String inputString =  sb.toString();
               if(inputString.isEmpty()) {
                   writeNotice("Data is empty!");
               } else {
                   HashMap<String, Integer> result = calculate(prepareData(inputString));
                   if(!result.isEmpty()) {

                       //sort result
                       TreeMap<String,Integer> sortedResult
                               = new TreeMap<String,Integer>(new ResultComparator(result));
                       sortedResult.putAll(result);

                       writeResult(sortedResult);
                   } else {
                       writeNotice("Data is empty!");
                   }
               }
            } catch (IOException e) {
                throw e;
            } finally {
                //close streams
                getInputStream().close();
                getOutputStream().close();
            }
        }

        /**
        * calculate data count
        * @param logins
        * @return
        */
        private HashMap<String, Integer> calculate(String[] logins) {
            HashMap<String, Integer> result = new HashMap<String, Integer>();

            for(String login: logins) {
                int count = 0;
                if(result.containsKey(login)) {
                    count += result.get(login);
                }

                count++;

                result.put(login, count);
            }

            return result;
        }
    }

    /**
     * comparator for calculated result
     */
    public static class ResultComparator implements Comparator<String> {
        private Map<String, Integer> result;

        public ResultComparator(Map<String, Integer> result) {
            this.result = result;
        }

        public int compare(String a, String b) {
            int cntA = result.get(a);
            int cntB = result.get(b);

            if(cntA == cntB) {
                return a.compareTo(b);
            } else if (cntA > cntB) {
                return -1;
            } else {
                return 1;
            }
        }
    }

	public static interface InputStreamProvider {
		InputStream provide() throws IOException;
	}

	public static interface OutputStreamProvider {
		public OutputStream provide();
	}

	public static class DefaultInputStreamProvider implements InputStreamProvider {
		@Override
		public InputStream provide() {
			return System.in;
		}
	}

	public static class DefaultOutputStreamProvider implements OutputStreamProvider {
		@Override
		public OutputStream provide() {
			return System.out;
		}
	}

    public static class FileInputStreamProvider implements InputStreamProvider {
        private File file;

        public FileInputStreamProvider(File file) throws FileNotFoundException {
            this.file = file;
        }

        @Override
        public InputStream provide() throws IOException {
            return new FileInputStream(file);
        }
    }
}
