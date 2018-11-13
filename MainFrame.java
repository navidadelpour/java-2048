import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.ChangedCharSetException;



import java.awt.event.*;
import java.awt.font.ShapeGraphicAttribute;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.awt.*;

public class MainFrame {

	public static Random r = new Random();
	public static int[][] map;
	public static ArrayList<Integer> highScores = new ArrayList<Integer>();
	public static int highScoresSize = 10;
	public static boolean[] block = new boolean[4];
	public static int[][] coverMap = new int[4][4];
	public static JLabel[][] label = new JLabel[4][4];
	public static ImageIcon[] icons = new ImageIcon[7];
	public static int height = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	public static Font font = new Font("Humanst521 BT", Font.PLAIN, height / 20);
	public static int time;
	public static int score;
	public static JLabel scoreLabel = new JLabel();
	private static Component frame;
	public static JPanel infoNumbers = new JPanel();
	public static JPanel infoBg = new JPanel();
	public static BufferedImage infoImg;
	
	public static void main(String[] args) {
		
		
		JPanel panel = new JPanel();
		JPanel info = new JPanel(){
			@Override
			public void paintComponent(Graphics g){
				super.paintComponent(g);
				try {
					infoImg = ImageIO.read(getClass().getResource("7.jpg"));
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				super.paintComponent(g);
				g.drawImage(infoImg.getScaledInstance(height / 2 + height / 380, height / 10 + height / 15, Image.SCALE_SMOOTH), 0, 0, null);
				
				repaint();
			}
		};
		
		
		info.setPreferredSize(new Dimension(height / 2 + height / 60, height / 10 + height / 15));

		JFrame frame = new JFrame("2048");
		
		for (int i = 0; i < icons.length; i++)
			icons[i] = new ImageIcon(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource(i + ".jpg"))
					.getScaledInstance(height / 8, height / 8, Image.SCALE_SMOOTH));
		
		
		scoreLabel.setFont(font);
		scoreLabel.setForeground(Color.black);
		scoreLabel.setText(score + "");
		scoreLabel.setVerticalAlignment(SwingConstants.CENTER);
		scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scoreLabel.setBackground(new Color(0, 0, 0, 0));
		
		JLabel timeLabel = new JLabel();
		timeLabel.setFont(font);
		timeLabel.setBackground(new Color(0, 0, 0, 0));
		timeLabel.setForeground(Color.GREEN);
		timeLabel.setVerticalAlignment(SwingConstants.CENTER);
		timeLabel.setHorizontalAlignment(SwingConstants.CENTER);

		infoNumbers.setLayout(new GridLayout(1, 3));
		infoNumbers.add(new JLabel());
		infoNumbers.add(timeLabel);
		infoNumbers.add(scoreLabel);
//		infoNumbers.setBounds(0, 0, infoImg.getIconWidth(), infoImg.getIconWidth());
		infoNumbers.setBackground(new Color(0, 0, 0, 0));
		

//		infoBg.add(new JLabel(infoImg));
		info.setLayout(new GridLayout(3, 1));
//		info.add(infoBg);
		info.add(new JLabel());
		info.add(infoNumbers);
		info.add(new JLabel());
		
		panel.setBackground(new Color(94, 49, 25));
		panel.setLayout(new GridLayout(4, 4));
		panel.setSize(height / 2 + height / 60, height / 2 + height / 52);

		frame.setLayout(new BorderLayout());
		frame.setIconImage(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("1.png")));
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(height / 2 + height / 60, (int) ((height / 2 + height / 22) * 1.3));
//		frame.setResizable(false);
		frame.add(info, BorderLayout.NORTH);
		frame.add(panel);

		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {
				label[i][j] = new JLabel();
				label[i][j].setFont(font);
				label[i][j].setHorizontalAlignment(SwingConstants.CENTER);
				label[i][j].setVerticalAlignment(SwingConstants.CENTER);
				label[i][j].setHorizontalTextPosition(SwingConstants.CENTER);
				label[i][j].setVerticalTextPosition(SwingConstants.CENTER);
				panel.add(label[i][j]);
			}
		// JLabel infoLabel = new JLabel();
		// infoLabel.setIcon(icons[7]);
		// info.add(infoLabel);
		startNewGame();
		try {
			int[] h;
			highScores = new ArrayList<Integer>();
			FileInputStream file = new FileInputStream("2048");
			ObjectInputStream reader = new ObjectInputStream(file);
			map = (int[][]) reader.readObject();
			score = (int) reader.readObject();
			highScores = (ArrayList<Integer>) reader.readObject();
			time = (int) reader.readObject();
			
//			for(int i = 0; i < h.length; i++){
//				for(int j = 0; j < highScores.size(); j++){
//					if(!highScores.get(j).equals(h[i]))
//						highScores.add(h[i]);
//				}
//				
//			}
//			java.util.Collections.sort(highScores);
//			while(highScores.size() > highScoresSize){
//				highScores.remove(highScores.size());
//			}
			file.close();
			reader.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(panel, "IOException");
		} catch (ClassNotFoundException e) {
			JOptionPane.showMessageDialog(panel, "class not found");
		}
		labelize(map);


		Thread thread = new Thread(new timerThread(timeLabel, frame));
		thread.start();

		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent event) {
				save();
				System.exit(0);
			}
		});
		
		new Thread(new Runnable(){
			@Override
			public void run(){
				while(true){
					infoBg.repaint();
					infoNumbers.repaint();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		frame.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent event) {
			}

			@Override
			public void keyTyped(KeyEvent event) {
			}

			@Override
			public void keyReleased(KeyEvent event) {
				boolean changed = false;
				switch (event.getKeyCode()) {
				case KeyEvent.VK_UP:
					for (int j = 0, i = 0; j < 4; j++) {
						while (i != 3) {
							if (map[i][j] == 0) {
								if (map[i + 1][j] == 0)
									i++;
								else {
									changed = true;
									break;
								}
							} else {
								if (map[i][j] != map[i + 1][j])
									i++;
								else {
									changed = true;
									break;
								}
							}
						}
						if (changed)
							break;
						block = new boolean[4];
						i = 0;
					}
					if (changed) {
						coverMap = copy(map);
						for (int j = 0, i = 0; j < 4; j++) {
							while (i != 3) {
								if (map[i][j] == 0) {
									if (map[i + 1][j] == 0)
										i++;
									else {
										changed = true;
										map[i][j] = map[i + 1][j];
										block[i] = block[i + 1];
										map[i + 1][j] = 0;
										block[i + 1] = false;
										i = 0;
									}
								} else {
									if (map[i][j] != map[i + 1][j] || block[i] || block[i + 1])
										i++;
									else if (!(block[i] && block[i + 1])) {
										changed = true;
										map[i][j] *= 2;
										time += Math.log10(map[i][j]) / Math.log10(2) / 2;
										score += map[i][j];
										block[i] = true;
										map[i + 1][j] = 0;
										i = 0;
									}
								}
							}
							block = new boolean[4];
							i = 0;
						}
						fill(map);
						break;
					} else
						break;

				case KeyEvent.VK_LEFT:
					for (int j = 0, i = 0; i < 4; i++) {
						while (j != 3) {
							if (map[i][j] == 0) {
								if (map[i][j + 1] == 0)
									j++;
								else {
									changed = true;
									break;
								}
							} else {
								if (map[i][j] != map[i][j + 1])
									j++;
								else {
									changed = true;
									break;
								}
							}
						}
						if (changed)
							break;
						block = new boolean[4];
						j = 0;
					}
					if (changed) {
						coverMap = copy(map);
						for (int j = 0, i = 0; i < 4; i++) {
							while (j != 3) {
								if (map[i][j] == 0) {
									if (map[i][j + 1] == 0)
										j++;
									else {
										changed = true;
										map[i][j] = map[i][j + 1];
										block[j] = block[j + 1];
										map[i][j + 1] = 0;
										block[j + 1] = false;
										j = 0;
									}
								} else {
									if (map[i][j] != map[i][j + 1] || block[j] || block[j + 1])
										j++;
									else if (!(block[j] && block[j + 1])) {
										changed = true;
										map[i][j] *= 2;
										time += Math.log10(map[i][j]) / Math.log10(2) / 2;
										score += map[i][j];
										block[j] = true;
										map[i][j + 1] = 0;
										j = 0;
									}
								}
							}
							block = new boolean[4];
							j = 0;
						}
						fill(map);
						break;
					} else
						break;
				case KeyEvent.VK_DOWN:
					for (int j = 0, i = 3; j < 4; j++) {
						while (i != 0) {
							if (map[i][j] == 0) {
								if (map[i - 1][j] == 0)
									i--;
								else {
									changed = true;
									break;
								}
							} else {
								if (map[i][j] != map[i - 1][j])
									i--;
								else {
									changed = true;
									break;
								}
							}
						}
						if (changed)
							break;
						block = new boolean[4];
						i = 3;
					}
					if (changed) {
						coverMap = copy(map);
						for (int j = 0, i = 3; j < 4; j++) {
							while (i != 0) {
								if (map[i][j] == 0) {
									if (map[i - 1][j] == 0)
										i--;
									else {
										changed = true;
										map[i][j] = map[i - 1][j];
										block[i] = block[i - 1];
										map[i - 1][j] = 0;
										block[i - 1] = false;
										i = 3;
									}
								} else {
									if (map[i][j] != map[i - 1][j] || block[i] || block[i - 1])
										i--;
									else if (!(block[i] && block[i - 1])) {
										changed = true;
										map[i][j] *= 2;
										time += Math.log10(map[i][j]) / Math.log10(2) / 2;
										score += map[i][j];
										block[i] = true;
										map[i - 1][j] = 0;
										i = 3;
									}
								}
							}
							block = new boolean[4];
							i = 3;
						}
						fill(map);
						break;
					} else
						break;
				case KeyEvent.VK_RIGHT:
					for (int j = 3, i = 0; i < 4; i++) {
						while (j != 0) {
							if (map[i][j] == 0) {
								if (map[i][j - 1] == 0)
									j--;
								else {
									changed = true;
									break;
								}
							} else {
								if (map[i][j] != map[i][j - 1])
									j--;
								else {
									changed = true;
									break;
								}
							}
						}
						if (changed)
							break;
						block = new boolean[4];
						j = 3;
					}
					if (changed) {
						coverMap = copy(map);
						for (int j = 3, i = 0; i < 4; i++) {
							while (j != 0) {
								if (map[i][j] == 0) {
									if (map[i][j - 1] == 0)
										j--;
									else {
										changed = true;
										map[i][j] = map[i][j - 1];
										block[j] = block[j - 1];
										map[i][j - 1] = 0;
										block[j - 1] = false;
										j = 3;
									}
								} else {
									if (map[i][j] != map[i][j - 1] || block[j] || block[j - 1])
										j--;
									else if (!(block[j] && block[j - 1])) {
										changed = true;
										map[i][j] *= 2;
										time += Math.log10(map[i][j]) / Math.log10(2) / 2;
										score += map[i][j];
										block[j] = true;
										map[i][j - 1] = 0;
										j = 3;
									}
								}
							}
							block = new boolean[4];
							j = 3;
						}
						fill(map);
						break;
					} else
						break;
				case KeyEvent.VK_Z:
					if(map != coverMap){
						if (score >= 100) {
							map = coverMap;
							score -= 100;
						}
						if (time > 10) {
							time /= 2;
						}
					}
					break;
				case KeyEvent.VK_R:
					startNewGame();
					timeLabel.setText(time + "");
					break;
				default:
					break;
				}
				labelize(map);
				if(checkLost()){
					addToList(score, highScores);
					int answer = JOptionPane.showConfirmDialog(null, "Game Over\nyourScore : " + MainFrame.score + "\n" + highScores.toString() + "\nwant to start again?", "Game Over", JOptionPane.YES_NO_OPTION);
					if(answer == JOptionPane.OK_OPTION){
						MainFrame.startNewGame();
						MainFrame.save();
						frame.dispose();
						MainFrame.main(null);
					}
					else{
						System.exit(0);
					}
				}
			}
			
		});
		
	}

	public static void labelize(int[][] map) {
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {
				// JOptionPane.showMessageDialog(null,i + " , " + j + " , " +
				// map[i][j]);
				if (map[i][j] == 0) {
					label[i][j].setText("");
					label[i][j].setIcon(icons[0]);
					label[i][j].setFont(new Font("Humanst521 BT", Font.PLAIN, height / 20));
				} else {
					label[i][j].setText(map[i][j] + "");
					switch (map[i][j]) {
					case 2:
						label[i][j].setIcon(icons[1]);
						label[i][j].setFont(new Font("Humanst521 BT", Font.PLAIN, height / 20));
						label[i][j].setForeground(Color.BLACK);
						break;
					case 4:
						label[i][j].setIcon(icons[2]);
						label[i][j].setForeground(Color.BLACK);
						label[i][j].setFont(new Font("Humanst521 BT", Font.PLAIN, height / 20));
						break;
					case 8:
						label[i][j].setIcon(icons[3]);
						label[i][j].setForeground(Color.BLACK);
						label[i][j].setFont(new Font("Humanst521 BT", Font.PLAIN, height / 20));
						break;
					case 16:
					case 32:
					case 64:
						label[i][j].setIcon(icons[4]);
						label[i][j].setForeground(Color.WHITE);
						label[i][j].setFont(new Font("Humanst521 BT", Font.PLAIN, height / 20));
						break;
					case 128:
					case 256:
					case 512:
						label[i][j].setIcon(icons[5]);
						label[i][j].setForeground(Color.darkGray);
						label[i][j].setFont(new Font("Humanst521 BT", Font.PLAIN, height / 20));
						break;
					case 1024:
						label[i][j].setIcon(icons[5]);
						label[i][j].setForeground(Color.darkGray);
						label[i][j].setFont(new Font("Humanst521 BT", Font.PLAIN, height / 25));
						break;
					default:
						label[i][j].setIcon(icons[6]);
						label[i][j].setForeground(Color.ORANGE);
						label[i][j].setFont(new Font("Humanst521 BT", Font.PLAIN, height / 25));
						break;
					}
				}
			}
		scoreLabel.setText(Integer.toString(score));
	}

	public static void fillNoChance(int[][] m) {
		while (true) {
			int[] x = { r.nextInt(4), r.nextInt(4) };
			if (m[x[0]][x[1]] == 0) {
				m[x[0]][x[1]] = 2;
				break;
			}
		}
	}

	private static void fill(int[][] m) {
		while (true) {
			int[] x = { r.nextInt(4), r.nextInt(4) };
			int y = r.nextInt(9) + 1;
			if (m[x[0]][x[1]] == 0) {
				if (y == 1)
					m[x[0]][x[1]] = 4;
				else
					m[x[0]][x[1]] = 2;
				break;
			}
		}
	}

	private static int[][] copy(int[][] m) {
		int[][] n = new int[4][4];
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				n[i][j] = m[i][j];
		return n;
	}

	public static void save() {
		try {
//			int[] h = new int[highScoresSize];
//			java.util.Collections.sort(highScores);
//			for(int i = 0; i < highScoresSize && i < highScoresSize; i++){
//				JOptionPane.showMessageDialog(null, h.length + ", " + highScores.size());
//				h[i] = highScores.get(i);
//			}
			FileOutputStream file = new FileOutputStream("2048");
			ObjectOutputStream writer = new ObjectOutputStream(file);
			writer.writeObject(map);
			writer.writeObject(score);
			writer.writeObject(highScores);
			writer.writeObject(time);

			file.close();
			writer.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(MainFrame.frame, "IOException");
			System.exit(0);
		}
	}

	public static void startNewGame() {
		time = 30;
		score = 0;
		map = new int[4][4];
		fillNoChance(map);
		fillNoChance(map);
		coverMap = copy(map);
	}
	
	public static void addToList(int x, ArrayList<Integer> al){
		if(!al.contains(x)){
			al.add(x);
			java.util.Collections.sort(al);
			if(al.size() > 10){
				al.remove(al.size() - 1);
			}
		}
	}
	
	public static boolean checkLost() {
		boolean full = true;
		boolean rowSame = false;
		boolean columnSame = false;
		for(int i = 0; i < 4; i++){
			for(int j = 0; j < 4; j++) {
				if(map[i][j] == 0)
					full = false;
			}
		}
		if(full){
//			JOptionPane.showMessageDialog(null, "full");
			for(int i = 0; i < 4; i++) {
				for (int j = 0; j < 3; j++){
					if(map[i][j] == map[i][j + 1]){
						rowSame = true;
						break;
					}
				}
				if(rowSame){
//						JOptionPane.showMessageDialog(null, "objects are the same in a row");
					break;
				}
			}
			for(int j = 0; j < 4; j++){
				for (int i = 0; i < 3; i++){
					if(map[i][j] == map[i + 1][j]){
						columnSame = true;
						break;
					}
				}
				if(columnSame){
//						JOptionPane.showMessageDialog(null, "objects are the same in a column");
					break;
				}
			}
			if(rowSame || columnSame)
				return false;
			else {
				return true;
			}
		}
		return false;
	}
}