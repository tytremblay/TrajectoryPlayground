package com.team319.ui;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;

import javax.swing.SwingUtilities;

import com.team254.lib.trajectory.Path;

public class PathViewer {
	public static void view(final Path path){
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new JFXPanel(); // this will prepare JavaFX toolkit and
								// environment
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						Plotter pl = new Plotter();
						pl.plotChezyTrajectory(path);
					}
				});
			}
		});
	}
}
