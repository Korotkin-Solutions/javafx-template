/**

The MIT License (MIT) | Copyright (c) 2015 Korotkin - yehuda@korotkin.co.il

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

 */
package com.korotkin.JavaFxStart.screen.framework;

import java.net.URL;
import java.util.HashMap;
import java.util.Stack;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class ScreensController extends StackPane {

	private HashMap<String, Node> screens = new HashMap<>();
	private static final String FXML_FOLDER = "/fxml";
	private Stack<String> breadcrumbs = new Stack<String>();
	/**
	 * Resource getter
	 * @param screen
	 * @return
	 */
	private URL getResource(Class<? extends ControlledScreen> cls)
	{
		return getClass().getResource(FXML_FOLDER + "/"+cls.getSimpleName()+".fxml");
	}
	
	/**
	 * Load extends of ControlledScreen
	 * @param screenClass
	 * @throws Exception
	 */
	private void loadScreen(Class<? extends ControlledScreen> screenClass) throws Exception {

		// If screen already loaded ignore loading
		if(screens.containsKey(screenClass.getCanonicalName()))
			return;
		
		// Get FXML resource
		FXMLLoader loader = new FXMLLoader( getResource(screenClass));
		
		// Load resources
		Parent loadScreen = (Parent) loader.load();
		
		// Get screen controller
		IControlledScreen screenController = ((IControlledScreen) loader.getController());
		
		// Set parent screen
		screenController.setScreenParent(this);
		
		// save screen list
		screens.put(screenClass.getCanonicalName(), loadScreen);
	}


	/**
	 * Set current screen - make transactions
	 * @param screen
	 * @throws Exception
	 */
	private void setScreen(Class<? extends ControlledScreen>  screen) throws Exception {
		if (!screens.containsKey(screen.getCanonicalName()))
			throw new Exception("Screen " + screen.getCanonicalName() + " Not exists!");

		setScreen(screen.getCanonicalName());
	}
	/**
	 * Set screen by class canonicalname
	 * @param canonicalName
	 */
	private void setScreen(String canonicalName){
		final DoubleProperty opacity = opacityProperty();
		
		if (!getChildren().isEmpty()) {
			Timeline fade = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 1.0)), new KeyFrame(new Duration(100.0), new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent t) {
					getChildren().remove(0);
					getChildren().add(0, screens.get(canonicalName));
					Timeline fadeIn = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)), new KeyFrame(new Duration(300.0), new KeyValue(opacity, 1.0)));
					fadeIn.play();
				}
			}, new KeyValue(opacity, 0.0)));
			
			fade.play();
			
		} else {
			setOpacity(0.0);
			getChildren().add(screens.get(canonicalName));
			Timeline fadeIn = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(opacity, 0.0)), new KeyFrame(new Duration(250), new KeyValue(opacity, 1.0)));
			fadeIn.play();
		}

		if(breadcrumbs.empty() || breadcrumbs.firstElement() != canonicalName)
			breadcrumbs.push( canonicalName );
	}
	/**
	 * Go back screen
	 */
	public void back(){
		// Current pop
		breadcrumbs.pop();
		
		// Show previews screen
		setScreen(breadcrumbs.pop());
	}
	/**
	 * Show screen
	 * @param screen
	 */
	public void show(Class<? extends ControlledScreen> screen){
		try {
			loadScreen(screen);
			setScreen(screen);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
