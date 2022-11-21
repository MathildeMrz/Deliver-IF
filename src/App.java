import java.awt.Label;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class App extends Application {

	public static void main(String[] args) {
		Application.launch(args);
		

	}

	@Override
	public void start(Stage arg0) throws Exception {
		Button button = new Button("Click Me");

		StackPane secondaryLayout = new StackPane();
		secondaryLayout.getChildren().add(button);

		Scene secondScene = new Scene(secondaryLayout, 230, 100);

		// New window (Stage)
		Stage newWindow = new Stage();
		newWindow.setTitle("Second Stage");
		newWindow.setScene(secondScene);
		
		newWindow.show();
		
	}
		

}
