package de.timderspieler.deluxeshop.external;

public class Currency {
	
	private String name;
	private String singular;
	private String plural;
	
	public Currency(String name, String singular, String plural) {
		this.name = name;
		this.singular = singular;
		this.plural = plural;
	}
	
	public String getName() {
		return name;
	}
	
	public String getSingular() {
		return singular;
	}
	
	public String getPlural() {
		return plural;
	}

}
