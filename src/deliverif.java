import controller.Controller;
import model.Plan;

public class deliverif {
	private static final int initialScale = 10;
	private static final int planHeight = 40;
	private static final int planWidth = 40;

	/**
	 * @param args the arguments 
	 */
	public static void main(String[] args) {
		Plan plan = new Plan();
		new Controller(plan);
	}
}
