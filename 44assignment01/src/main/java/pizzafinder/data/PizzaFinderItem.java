package pizzafinder.data;

import gridgames.data.item.Item;

public enum PizzaFinderItem implements Item {
    WALL,
    PIZZA;
	
	@Override
	public boolean isHiddenItem() {
		return true;
	}

    @Override
    public String toString() {
        if(this.equals(WALL)) {
            return "W";
        } else if(this.equals(PizzaFinderItem.PIZZA)) {
        	return "Z";
        } else {
            return "";
        }
    }
}
