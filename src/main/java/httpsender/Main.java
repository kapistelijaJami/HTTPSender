package httpsender;

public class Main {
	public static void main(String[] args) {
		HttpSender sender = new HttpSender();
		
		//sender.setUrl("https://api.pubg.com/shards/tournament/matches/5473beee-0ba8-4b1e-9974-92c166ca7610");
		//sender.addHeader("Accept", "application/vnd.api+json");
		
		sender.setUrl("http://localhost/serveri/pubgapi/heatmap/idt.txt");
		
		String body = sender.buildRequest().sendAndReceiveString().body();
		
		System.out.println(body);
	}
}
