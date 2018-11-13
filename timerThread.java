import java.awt.Color;
import java.awt.Frame;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class timerThread implements Runnable {
	private JLabel label;
	private JFrame frame;

	public timerThread(JLabel label, JFrame frame) {
		this.label = label;
		this.frame = frame;
	}
	
	@Override
	public void run() {
		while (true) {
			if(MainFrame.time < 00) {
				MainFrame.addToList(MainFrame.score, MainFrame.highScores);
				int answer = JOptionPane.showConfirmDialog(null, "Game Over\nyourScore : " + MainFrame.score + "\n" + MainFrame.highScores.toString() + "\nwant to start again?", "Game Over", JOptionPane.YES_NO_OPTION);
				if(answer == JOptionPane.OK_OPTION){
					MainFrame.startNewGame();
					MainFrame.save();
					frame.dispose();
					MainFrame.main(null);
				} else {
					System.exit(0);
				}
				break;
			} else {
				label.setText(MainFrame.time-- + "");
				if (MainFrame.time >= 30) {
					label.setForeground(Color.green);
				}
				if (MainFrame.time < 20 && MainFrame.time >= 10) {
					label.setForeground(Color.orange);
				}
				if (MainFrame.time < 10) {
					label.setForeground(Color.red);
				}
			}
//			MainFrame.infoBg.repaint();
//			MainFrame.infoNumbers.repaint();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
