import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Based on the user's specified destinations, this program 
 * determines the shortest tour to visit each destination exactly once 
 * before returning to the starting destination.
 * 
 * @author Phoebe Chua 
 */
public class WinterBeater2 extends JFrame {
	private static final long serialVersionUID = 1L;

	/** Buttons for ShortestTourCalculatorGUI's GUI. */
	private static JButton[][] buttons; 

	/** Label for the panel that displays information. */ 
	private static JLabel statsLabel; 
	
	/** Locations (destinations) entered by the user. */
	private static String locationsString = null; 
	
	/** An array of the locations entered by the user. */ 
	private static String[] locationsSplitted;
	
	/** Images to represent the buildings */ 
	private ImageIcon gymIcon = new ImageIcon("gymImage.jpg");
	private ImageIcon becksIcon = new ImageIcon("becksImage.jpg");
	private ImageIcon HCIcon = new ImageIcon("HCImage.jpg");
	private ImageIcon freyIcon = new ImageIcon("freyImage.jpg");
	private ImageIcon libraryIcon = new ImageIcon("libraryImage.jpg");
	private ImageIcon doneIcon = new ImageIcon("doneImage.jpg");

	/**
	 * Creates an object that allows the user 
	 * to specify the destinations. The program then determines the shortest 
	 * tour.
	 */
	public WinterBeater2() {
		super("Winter Beater");

		setDefaultCloseOperation(EXIT_ON_CLOSE);	
		add(makeMainPanel());
		pack();
		setVisible(true);
	}
	
	/**
	 * Returns the locations specified by the user.
	 * 
	 * @return	Locations specified by the user.
	 */
	public static String getLocationsString() {
		new WinterBeater2(); 

		return locationsString; 
	}

	/**
	 * Creates a GUI layout (just one panel consisting of buttons 
	 * representing the campus buildings and a panel displaying 
	 * information to the user.)
	 * 
	 * @return 	A panel consisting of buttons representing 
	 * 			the campus buildings and a panel displaying
	 * 			information to the user.
	 */
	private JPanel makeMainPanel() {
		buttons = new JButton[2][3];
		JPanel buttonPanel = new JPanel(); 
		buttonPanel.setLayout(new GridLayout(2,3));
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		ActionListener listener = new Listener(); 

		for (int i = 0; i < buttons.length; i++) {
			for (int j = 0; j < buttons[i].length; j++) {

				buttons[i][j] = new JButton(" ");
				buttonPanel.add(buttons[i][j]);
				buttons[i][j].setPreferredSize(new Dimension(200, 200));
				buttons[i][j].addActionListener(listener);
			}
		}

		buttons[0][0].setIcon(gymIcon);
		buttons[0][0].setText("Gym");

		buttons[0][1].setIcon(becksIcon);
		buttons[0][1].setText("Becks");

		buttons[0][2].setIcon(freyIcon);
		buttons[0][2].setText("Frey");

		buttons[1][0].setIcon(HCIcon);
		buttons[1][0].setText("HC");

		buttons[1][1].setIcon(libraryIcon);
		buttons[1][1].setText("Library");

		buttons[1][2].setIcon(doneIcon);
		buttons[1][2].setText("Done");

		statsLabel = new JLabel(" ");
		JPanel statsPanel = new JPanel(); 
		statsPanel.add(statsLabel);
		statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		statsLabel.setText(
				"<html>This program determines the shortest route " +
						"that visits each destination once <br>" +
						"and returns to the origin destination. " +
						"<br> <br>" +
						"Select at least three destinations. <br> " +
						"From top left: Gym, Becks, Frey, High Center, Library. <br><br> " +						
				"When you are done, click on the orange check mark.<html>");

		statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

		JPanel mainPanel = new JPanel(); 
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(buttonPanel, BorderLayout.CENTER);
		mainPanel.add(statsPanel, BorderLayout.SOUTH);

		return mainPanel; 
	}
	
	/** Calculates the shortest tour based on user input. */
	private static void calculateShortestTour() {
		StringBuilder shortestTour = new StringBuilder(); 
		locationsSplitted = locationsString.split(" "); 

		int n = locationsSplitted.length; 

		double dist[][] = 
				new double[locationsSplitted.length + 1]
						[locationsSplitted.length + 1];

		for (int i = 0; i < locationsSplitted.length + 1; i++) {
			for (int j = 0; j < locationsSplitted.length + 1; j++) {

				if (i == 0 || j == 0) {
					dist[i][j] = 0;

				} else {
					String filename = 
							locationsSplitted[i - 1] + "_" + 
							locationsSplitted[j -1] +
							".txt";

					Scanner scanner = null;

					try {
						scanner = new Scanner(new File(filename));
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					dist[i][j] = scanner.nextDouble(); 
					scanner.close(); 
				}
			}
		}

		int sol[] = new int[n + 1];
		ShortestTourCalculator.travelingSalesmanProblem(n, dist, sol);

		for (int k = 1; k <= n + 1; k++) {
			if (k == n + 1) {
				shortestTour.append(locationsSplitted[0]);

			} else { 
				shortestTour.append(locationsSplitted[sol[k] - 1] + " -> "); 
			}
		}

		String shortestTourString = shortestTour.toString(); 

		// walkingSpeed is based on time taken to walk from 
		// Frey to Library (approx. 180s to walk a distance of 277 units.) 
		double walkingSpeed = 0.6498;

		double estimatedTimeInSeconds = dist[0][0] * walkingSpeed;
		int estimatedTimeMin = (int) estimatedTimeInSeconds / 60; 
		int estimatedTimeSeconds = (int) estimatedTimeInSeconds % 60;

		statsLabel.setText("<html>Shortest Tour: " + 
				shortestTourString + "<html> <br> <br>" +
				"Estimated Travel Time: " + estimatedTimeMin + " min " 
				+ estimatedTimeSeconds + " secs <br> <br>");

		// In case I want to print out the total distance: 
		// System.out.println("\n\nTotal distance = " + dist[0][0]);	
	}

	StringBuilder locations = new StringBuilder();
	
	/**
	 * Appends the destination's name to a string whenever the user 
	 * clicks on the corresponding image of the destination. 
	 */
	private class Listener implements ActionListener {

		
		public void actionPerformed(ActionEvent event) {
			Object source = event.getSource(); 

			if (source instanceof JButton) {

				// if it is just an object source, cannot use JButton 
				// methods. But now that we've checked that the source is 
				// a JButton, we can cast it to a JButton. 

				JButton button = (JButton) source;

				if (button.getText().equals("Gym")) {
					buttons[0][0].setEnabled(false);
					locations.append("Gym ");
				}

				if (button.getText().equals("Becks")) {
					buttons[0][1].setEnabled(false);
					locations.append("Becks ");
				}

				if (button.getText().equals("Frey")) {
					buttons[0][2].setEnabled(false);
					locations.append("Frey ");
				}

				if (button.getText().equals("HC")) {
					buttons[1][0].setEnabled(false);
					locations.append("HC ");
				}

				if (button.getText().equals("Library")) {
					buttons[1][1].setEnabled(false);
					locations.append("Library ");
				}

				if (button.getText().equals("Done")) {
					buttons[1][2].setEnabled(false);
					locationsString = locations.toString(); 
					calculateShortestTour();
				}
			}
		}
	}
	
	/**
	 * @param args command-line arguments (ignored) 
	 */
	public static void main(String args[]) throws FileNotFoundException {
		new WinterBeater2(); 
	}
}
