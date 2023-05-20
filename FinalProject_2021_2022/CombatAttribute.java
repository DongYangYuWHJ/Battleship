package FinalProject_2021_2022;

public class CombatAttribute {

	
	//Attributes for combat
	private boolean atkMode = false;
	private String Difficulty;
	private String atkDirection = "N";
	private String tempAtkDir = "N";
	private String lightOrDarkSquare = "N";
	private int atkRandomizor = 5;
	private int toss = (int)(Math.random()*2); //0 = tail, 1 = head
	private String currentShipAtked = "N";
	private int[] hitsAndMisses = {0,0,0,0}; //playerHits, playerMisses, AIHits, AIMisses
	
	
	//Access
	public boolean atkMode() {
		return this.atkMode;
	}
	
	public String Difficulty() {
		return this.Difficulty;
	}
	
	public String atkDirection() {
		return this.atkDirection;
	}
	
	public String tempAtkDir() {
		return this.tempAtkDir;
	}
	
	public String lightOrDarkSquare() {
		return this.lightOrDarkSquare;
	}
	
	public int atkRandomizor() {
		return this.atkRandomizor;
	}
	
	public int hitsAndMisses(int i) {
		return this.hitsAndMisses[i-1];
	}
	
	public int toss() {
		return this.toss;
	}
	
	public String currentShip() {
		return this.currentShipAtked;
	}

	
	//Modifiers
	
	public void changeAtkMode(boolean atkMode) {
		this.atkMode = atkMode;
	}
	
	public void setDifficulty(String mode) {
		this.Difficulty = mode;
	}
	
	public void detectAtkDirection(String atkDirection) {
		this.atkDirection = atkDirection;
	}
	
	public void detectTempAtkDir(String dr) {
		this.tempAtkDir = dr;
	}
	
	public void setLightOrDarkSquare(String str) {
		this.lightOrDarkSquare = str;
	}
	
	public void setRandomizor() {
		this.atkRandomizor--;
	}
	
	public void setHitsAndMisses(int i) {
		this.hitsAndMisses[i-1]++;
	}
	
	public void setCurrentShip(String i) {
		this.currentShipAtked = i;
	}
	
}
