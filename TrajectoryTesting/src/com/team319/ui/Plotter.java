package com.team319.ui;

import com.team254.lib.trajectory.Path;

import javafx.scene.*;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.stage.*;

public class Plotter {

	public Plotter(){}

	@SuppressWarnings("rawtypes")
	public void plotChezyTrajectory(Path path) {
		Stage stage = new Stage();
		stage.setTitle("Scatter Chart Sample");
		final NumberAxis xAxis = new NumberAxis(-10, 20, .5);
		final NumberAxis yAxis = new NumberAxis(-10, 10, .5);
		final ScatterChart<Number, Number> sc = new ScatterChart<Number, Number>(xAxis, yAxis);
		xAxis.setLabel("x");
		yAxis.setLabel("y");
		sc.setTitle(path.getName());

		XYChart.Series series1 = new XYChart.Series();
		series1.setName("Left");
		for (int i = 0; i < path.getPair().left.getNumSegments(); i++) {
			series1.getData().add(new XYChart.Data(path.getPair().left.getSegment(i).x, path.getPair().left.getSegment(i).y));
		}

		XYChart.Series series2 = new XYChart.Series();
		series2.setName("Right");
		for (int i = 0; i < path.getPair().left.getNumSegments(); i++) {
			series1.getData().add(new XYChart.Data(path.getPair().right.getSegment(i).x, path.getPair().right.getSegment(i).y));
		}

		sc.getData().addAll(series1, series2);
		Scene scene = new Scene(sc, 1000, 800);
		scene.getStylesheets().clear();
		scene.getStylesheets().add(getClass().getResource("Plotter.css").toExternalForm());
		stage.setScene(scene);
		stage.show();
	}

}
