package reservation;

public class Request {

	public int terminalId;
	public String function;
	public Object args;
	
	public Request(int terminalId, String function, Object args) {
		this.terminalId = terminalId;
		this.function = function;
		this.args = args;
	}
	
}
