import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Main {
    static OutputStreamProvider outputStreamProvider = new DefaultOutputStreamProvider();

	public static void main(String[] args) {
		IOApp app = new IOApp(inputStreamProviderFactory(args), outputStreamProvider);
		app.process();
	}

	private static InputStreamProvider inputStreamProviderFactory(String[] args) {
		return new DefaultInputStreamProvider();
	}

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

        private void writeNotice(String err) throws IOException {
           String message = String.format("notice: %s", err);
           this.outputStream.write(message.getBytes());
        }

        private void writeResult(Map<String, Integer> result) throws IOException {
            StringBuilder resultString = new StringBuilder();
            resultString.append("-------- result ---------\n");
            for(Map.Entry<String, Integer> entry :  result.entrySet()) {
                resultString.append(String.format("%s - %s\n", entry.getKey(), entry.getValue()));
            }

            this.outputStream.write(resultString.toString().getBytes());
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
                       writeResult(result);
                   } else {
                       writeNotice("Data is empty!");
                   }
               }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
        *
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

	public static class FileInputStreamProvider implements InputStreamProvider {
		@Override
		public InputStream provide() {
			return System.in;
		}
	}
}
