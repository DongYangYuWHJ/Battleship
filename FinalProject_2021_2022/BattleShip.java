package FinalProject_2021_2022;

public class BattleShip {
	//begin with upper
	
	//attribute
	private String battleshipName;
	private int battleshipIndex;
	private int battleshipHealth;
	private String battleshipDirection;
	
	
	
	//constructor
	public BattleShip(String battleshipName, int battleshipHealth) {
		this.battleshipName = battleshipName;
		this.battleshipIndex = -1;//changed later
		this.battleshipHealth = battleshipHealth;//health is hole at the original point
		this.battleshipDirection = "";//changed later
	}
	
	
	//action
	
	
	//Accessor
	public String getBattleshipName() {return battleshipName;}
	public int getBattleshipHealth() {return battleshipHealth;}
	public int getBattleshipIndex() {return battleshipIndex;}
	public String getBattleshipDirection() {return battleshipDirection;}
	
	//modifier
	public void setBattleshipIndex(int index_2D) {this.battleshipIndex = index_2D;}
	public void setBattleshipHealth (int health) {this.battleshipHealth = health;}
	public void setBattleshipName  (String name) {this.battleshipName = name;}
	public void setBattleshipDirection(String dir) {this.battleshipDirection = dir;}
	
}