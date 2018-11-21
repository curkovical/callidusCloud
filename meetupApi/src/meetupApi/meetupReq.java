package meetupApi;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Scanner;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class meetupReq {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String uri = "https://api.meetup.com/2/cities?&sign=true&photo-host=public&country=rs&page=20";
		
		try {			
			HttpClient client = HttpClient.newBuilder().version(Version.HTTP_2).build();			
			HttpRequest request = HttpRequest.newBuilder()
						.uri(URI.create(uri))
						.GET()
						.build();
						
			HttpResponse<String> response = client.send(request, BodyHandlers.ofString());			
			JsonObject jsonObj = new JsonParser().parse(response.body()).getAsJsonObject();
			JsonArray arr = jsonObj.getAsJsonArray("results");				
			for(int i=0; i<arr.size(); i++) {
				System.out.printf("%d: %s\n", i, arr.get(i).getAsJsonObject().get("city"));					
			}

			System.out.println("Unesite broj zeljenog grada:");			
			Scanner sc = new Scanner(System.in);
			int chosenCity = sc.nextInt();
			
			uri = "https://api.meetup.com/2/open_events?and_text=False&country=rs&offset=0&format=json&limited_events=False&photo-host=public&page=20&radius=25.0&desc=False&status=upcoming";					
			StringBuffer sbUrl = new StringBuffer(uri);			
			sbUrl.append("&city=");
			
			String cityString = arr.get(chosenCity).getAsJsonObject().get("city").toString().toLowerCase();
			cityString = cityString.replace(' ', '+').replace('š', 's').replace('č', 'c').replace('ć', 'c'); // necessary for api to be working
			char[] city = cityString.toCharArray();			
			sbUrl.append(city, 1, city.length - 2); // this way quotes are deprecated
			sbUrl.append("&sing=true&key=16e4171132196822153775477a4960"); // request has to be key-signed  
			
			request = HttpRequest.newBuilder()
					.uri(URI.create(sbUrl.toString()))
					.GET()
					.build();
			
			response = client.send(request, BodyHandlers.ofString());
			jsonObj = new JsonParser().parse(response.body()).getAsJsonObject();
			arr = jsonObj.getAsJsonArray("results");
			if(arr.size() == 0) {
				System.out.println("Nema dešavanja u izabranom gradu.");
			}
			for(int i=0; i<arr.size(); i++) {					
				JsonObject result = arr.get(i).getAsJsonObject(); 
				String name = result.get("name").toString();
				String desc = result.get("description") != null ? result.get("description").toString() : "";
				System.out.printf("%d: %s\n %s\n", i, name, desc);					
			}
			
			sc.close();			
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

	}

}
