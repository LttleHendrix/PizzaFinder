package pizzafinder.data;

public enum Percept {
    BUMP,
    SMELL;
	
	public String getMessage() {
		if(this.equals(BUMP)) {
			return "You feel a wall in front of you."; 
		} else if(this.equals(SMELL)) {
			return "You smell pizza!";
		} else {
			return null;
		}
	}
}
