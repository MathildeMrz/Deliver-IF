package view;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import controller.Controller;
import javafx.application.Application;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import model.Courier;
import model.Plan;
import xml.XMLdeserializer;

public class Window  extends Application  {
	private Plan plan;
	private Controller controller;
	private int width;
	private int height;
	private ListView<Courier> couriers;
	private mapView mv;
	
	public static void main(String[] args) throws Exception {
		Application.launch(args);
		
	}
	

	@Override
	public void start(Stage arg0) throws Exception {
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		this.width = gd.getDisplayMode().getWidth();
		this.height = gd.getDisplayMode().getHeight();
		this.couriers = initCouriers();

		this.plan = new Plan();
		/*Deserialize XML file*/
		XMLdeserializer.load(this.plan);
		
		this.mv = new mapView();
		this.mv.setController(this.controller);
		this.mv.setCouriers(this.couriers);
		this.mv.setHeight(this.height);
		this.mv.setWidth(this.width);
		this.mv.setPlan(this.plan);
		this.mv.start(new Stage());
		
	}

	public ListView<Courier> initCouriers() {

		File file = new File("./saveCouriers.txt");
		this.couriers = new ListView<Courier>();

		if (file.exists()) {
			// Creating an object of BufferedReader class
			BufferedReader br;
			try {
				br = new BufferedReader(new FileReader(file));
				// Declaring a string variable
				String st;
				// Condition holds true till
				try {

					while ((st = br.readLine()) != null) {
						String[] arrSplit_2 = st.split(";");
						couriers.getItems().add(new Courier(arrSplit_2[0], Double.parseDouble(arrSplit_2[1])));
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
		} else {
			System.out.println(file.getPath() + " does not exist");
		}
		return couriers;
	}

	
}
