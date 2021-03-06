/*
 * Copyright (c) 2011-2017, Peter Abeles. All Rights Reserved.
 *
 * This file is part of BoofCV (http://boofcv.org).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package boofcv.demonstrations.calibration;

import boofcv.demonstrations.shapes.ThresholdControlPanel;
import boofcv.factory.filter.binary.ThresholdType;
import boofcv.gui.BoofSwingUtil;
import boofcv.gui.StandardAlgConfigPanel;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

/**
 * Shows calibration grid detector status, configure display, and adjust parameters.
 *
 * @author Peter Abeles
 */
public class DetectCalibrationPanel extends StandardAlgConfigPanel
		implements ChangeListener, ItemListener, MouseWheelListener
{
	// zoom values
	private static double ZOOM_MAX = 20;
	private static double ZOOM_MIN = 0.1;
	private static double ZOOM_INC = 0.1;

	// cursor location
	JTextField textCursorX = new JTextField(10);
	JTextField textCursorY = new JTextField(10);

	// indicates if a calibration grid was found or not
	JLabel successIndicator;

	// select the calibration grid's dimensions
	JSpinner selectRows;
	JSpinner selectColumns;

	// selects which image to view
	JComboBox viewSelector;

	// toggle what is visible or not
	JCheckBox showPoints;
	JCheckBox showNumbers;

	JCheckBox showGraph;
	JCheckBox showGrids;
	JCheckBox showShapes;
	JCheckBox showOrder;
	JCheckBox showContour;

	// allows the user to change the image zoom
	JSpinner selectZoom;

	// selects threshold to create binary image from
	ThresholdControlPanel threshold;

	boolean doShowPoints = true;
	boolean doShowNumbers = true;
	boolean doShowClusters = false;
	boolean doShowGraph = false;
	boolean doShowOrder = true;
	boolean doShowGrids = false;
	boolean doShowShapes = false;
	boolean doShowContour = false;

	double scale = 1;
	int gridRows;
	int gridColumns;

	Listener listener;
	
	int selectedView = 0;

	public DetectCalibrationPanel(int gridRows, int gridColumns) {
		this(gridRows,gridColumns,true);
	}

	public DetectCalibrationPanel(int gridRows, int gridColumns, boolean addComponents ) {
		this.gridRows = gridRows;
		this.gridColumns = gridColumns;

		textCursorX.setEditable(false);
		textCursorX.setMaximumSize(textCursorX.getPreferredSize());
		textCursorY.setEditable(false);
		textCursorY.setMaximumSize(textCursorY.getPreferredSize());

		viewSelector = new JComboBox();
		viewSelector.addItem("Original");
		viewSelector.addItem("Threshold");
		viewSelector.addItemListener(this);
		viewSelector.setMaximumSize(viewSelector.getPreferredSize());

		selectZoom = new JSpinner(new SpinnerNumberModel(1, ZOOM_MIN, ZOOM_MAX, ZOOM_INC));
		selectZoom.addChangeListener(this);
		selectZoom.setMaximumSize(selectZoom.getPreferredSize());

		successIndicator = new JLabel();

		showPoints = new JCheckBox("Show Points");
		showPoints.setSelected(doShowPoints);
		showPoints.addItemListener(this);
		showPoints.setMaximumSize(showPoints.getPreferredSize());

		showNumbers = new JCheckBox("Show Numbers");
		showNumbers.setSelected(doShowNumbers);
		showNumbers.addItemListener(this);
		showNumbers.setMaximumSize(showNumbers.getPreferredSize());

		showGraph = new JCheckBox("Show Graphs");
		showGraph.setSelected(doShowGraph);
		showGraph.addItemListener(this);
		showGraph.setMaximumSize(showGraph.getPreferredSize());

		showGrids = new JCheckBox("Show Grids");
		showGrids.setSelected(doShowGrids);
		showGrids.addItemListener(this);
		showGrids.setMaximumSize(showGrids.getPreferredSize());

		showOrder = new JCheckBox("Show Order");
		showOrder.setSelected(doShowOrder);
		showOrder.addItemListener(this);
		showOrder.setMaximumSize(showOrder.getPreferredSize());

		showShapes = new JCheckBox("Show Shapes");
		showShapes.setSelected(doShowShapes);
		showShapes.addItemListener(this);
		showShapes.setMaximumSize(showShapes.getPreferredSize());

		showContour = new JCheckBox("Show Contour");
		showContour.setSelected(doShowContour);
		showContour.addItemListener(this);
		showContour.setMaximumSize(showContour.getPreferredSize());

		selectRows = new JSpinner(new SpinnerNumberModel(gridRows,2, 100, 1));
		selectRows.addChangeListener(this);
		selectRows.setMaximumSize(selectRows.getPreferredSize());

		selectColumns = new JSpinner(new SpinnerNumberModel(gridColumns,2, 100, 1));
		selectColumns.addChangeListener(this);
		selectColumns.setMaximumSize(selectColumns.getPreferredSize());

		threshold = new ThresholdControlPanel(new ThresholdControlPanel.Listener() {
			@Override
			public void imageThresholdUpdated() {
				listener.calibEventDetectorModified();
			}
		}, ThresholdType.BLOCK_OTSU);
		threshold.setRadius(40);

		if( addComponents )
			addComponents();
	}

	protected void addComponents() {
		addLabeled(successIndicator, "Found:", this);
		addLabeled(textCursorX,"X",this);
		addLabeled(textCursorY,"Y",this);
		addSeparator(100);
		addLabeled(viewSelector, "View ", this);
		addSeparator(100);
		addLabeled(selectRows, "Rows", this);
		addLabeled(selectColumns, "Cols", this);
		addAlignLeft(threshold,this);
		addLabeled(selectZoom, "Zoom ", this);
		addAlignLeft(showPoints, this);
		addAlignLeft(showNumbers,this);
		addAlignLeft(showGraph,this);
		addAlignLeft(showGrids,this);
		addAlignLeft(showOrder,this);
		addAlignLeft(showShapes, this);
		addAlignLeft(showContour, this);
	}

	public void addView( String name ) {
		viewSelector.addItem(name);
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		if( listener == null )
			return;

		 if( e.getSource() == selectRows) {
			gridRows = ((Number) selectRows.getValue()).intValue();
			listener.calibEventDetectorModified();
		}  else if( e.getSource() == selectColumns) {
			gridColumns = ((Number) selectColumns.getValue()).intValue();
			listener.calibEventDetectorModified();
		} else if( e.getSource() == selectZoom ) {
			scale = ((Number)selectZoom.getValue()).doubleValue();
			listener.calibEventGUI();
		}
	}

	public void setSuccessMessage( String message , boolean worked )
	{
		successIndicator.setText(message);
		if( worked )
			successIndicator.setForeground(Color.BLACK);
		else
			successIndicator.setForeground(Color.RED);
	}

	public int getSelectedView() {
		return selectedView;
	}

	public boolean isShowShapes() {
		return doShowShapes;
	}

	public boolean isShowPoints() {
		return doShowPoints;
	}

	public boolean isShowNumbers() {
		return doShowNumbers;
	}

	public boolean isShowGraph() {
		return doShowGraph;
	}

	public boolean isShowGrids() {
		return doShowGrids;
	}

	public boolean isShowOrder() {
		return doShowOrder;
	}


	public void setListener( Listener listener) {
		this.listener = listener;
	}

	public double getScale() {
		return scale;
	}

	public void setCursor( final double x , final double y ) {
		BoofSwingUtil.invokeNowOrLater(
				new Runnable() {
					@Override
					public void run() {
						textCursorX.setText(String.format("%5.3f", x));
						textCursorY.setText(String.format("%5.3f", y));
					}
				});
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if( listener == null )
			return;

		if( e.getSource() == viewSelector ) {
			selectedView = viewSelector.getSelectedIndex();
			listener.calibEventGUI();
		} else if( e.getSource() == showShapes) {
			doShowShapes = showShapes.isSelected();
			listener.calibEventGUI();
		} else if( e.getSource() == showNumbers ) {
			doShowNumbers = showNumbers.isSelected();
			listener.calibEventGUI();
		} else if( e.getSource() == showPoints ) {
			doShowPoints = showPoints.isSelected();
			listener.calibEventGUI();
		} else if( e.getSource() == showGraph ) {
			doShowGraph = showGraph.isSelected();
			listener.calibEventGUI();
		} else if( e.getSource() == showGrids ) {
			doShowGrids = showGrids.isSelected();
			listener.calibEventGUI();
		} else if( e.getSource() == showOrder ) {
			doShowOrder = showOrder.isSelected();
			listener.calibEventGUI();
		} else if( e.getSource() == showContour ) {
			doShowContour = showContour.isSelected();
			listener.calibEventGUI();
		} else if( e.getSource() == selectZoom ) {
			scale = ((Number)selectZoom.getValue()).doubleValue();
			listener.calibEventGUI();
		}
	}

	public int getGridRows() {
		return gridRows;
	}

	public int getGridColumns() {
		return gridColumns;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {

		double curr = ((Number)selectZoom.getValue()).doubleValue();

		if( e.getWheelRotation() > 0 )
			curr *= 1.1;
		else
			curr /= 1.1;

		setScale(curr);
	}
	public void setScale(double scale) {
		if( ((Number)selectZoom.getValue()).doubleValue() == scale )
			return;

		double curr;

		if( scale >= 1 ) {
			curr = ZOOM_INC * ((int) (scale / ZOOM_INC + 0.5));
		} else {
			curr = scale;
		}
		if( curr < ZOOM_MIN ) curr = ZOOM_MIN;
		if( curr > ZOOM_MAX) curr = ZOOM_MAX;

		selectZoom.setValue(curr);
	}
	
	public interface Listener
	{
		void calibEventGUI();

		void calibEventDetectorModified();
	}
}
