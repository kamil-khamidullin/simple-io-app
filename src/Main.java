import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.*;

public class Main {
    static OutputStreamProvider outputStreamProvider = new DefaultOutputStreamProvider();

	public static void main(String[] args) {
		IOApp app = new IOApp(
                inputStreamProviderFactory(args),
                outputStreamProvider
        );

        //run app
		app.process();
	}

	private static InputStreamProvider inputStreamProviderFactory(String[] args) {
		return new DefaultInputStreamProvider();
	}

    /**
     * simple logs processor class
     */
    public static class IOApp {
	    private InputStream inputStream;
	    private OutputStream outputStream;
        public final static String DELIMITER = " ";

	    public IOApp(InputStreamProvider inputStreamProvider, OutputStreamProvider outputStreamProvider) {
		   this.inputStream = inputStreamProvider.provide();
		   this.outputStream = outputStreamProvider.provide();
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
           String message = String.format("notice: %s", err);
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
                String capitalizeLogin = login.substring(0, 1).toUpperCase()
                        + login.substring(1);

                resultString.append(String.format("%s - %s\n", capitalizeLogin, entry.getValue()));
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

        public void process() {
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
                e.printStackTrace();
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
		InputStream provide();
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
}
