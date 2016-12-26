import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.Gson;
/**
 * @author qolsys
 *
 */
public class MyHttpClient {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 *//*
	public static void main(String[] args) throws IOException, InterruptedException {

		String deviceId = args[0];
		String command = args[1];
		postCommandsToDevice("{ \"client_ep\" : \""+deviceId+"\", \"command\" : \""+command+"\"}");
	}*/

	public static Device postCommandsToDevice(String jsonString, String leshanIp) throws ClientProtocolException, IOException{
		Device device = null;
		try{
		device = new Gson().fromJson(jsonString, Device.class);
		//System.out.println(device);
		postArmCommand(device.getClient_ep(), device.getCommand(), leshanIp);
		}catch(Exception e){
			System.out.println(e.getMessage());
		}
		return device;
	}

	public static void postArmCommand(String deviceId, String command, String leshanIp) throws ClientProtocolException, IOException{
		if((deviceId != null || command != null) && command.length()<33){
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost postRequest = new HttpPost(
					"http://"+leshanIp+":8080/api/clients/"+deviceId+"/2014/0/0");

			StringEntity input = new StringEntity(command);
			input.setContentType("application/json");
			
			postRequest.setEntity(input);

			HttpResponse response = httpClient.execute(postRequest);

			BufferedReader br = new BufferedReader(
					new InputStreamReader((response.getEntity().getContent())));

			String output;
			while ((output = br.readLine()) != null) {
				System.out.println(output);
			}

			httpClient.getConnectionManager().shutdown();
		}
	}

}


