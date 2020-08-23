package com.mi6.browser;

import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;

/**
 * A Simple JavaFX Webview Showing on JFXPanel. 
 * 
 * @author Mi6
 *
 */
public class FXBrowser extends JFXPanel implements ChangeListener<State> {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Check Page Worker State after every 1000 ms. 
	 */
	public static final int PAGE_DELAY = 1000;

	/**
	 * Stop Page status checker After 15 Iteration. 
	 */
	public static final int STOP_CHECK = 15;

	WebView webView;
	WebEngine webEngine;
	WebHistory history;
	Document document;
	State state;
	String html;
	
	/**
	 * Wait to get the html string, see {@code getStringFromDocument()} method.
	 * accessing this method in swing environment was crashing if some exception happened.
	 * Move this method to {@code Platform.runLater } and wait to get the HTML string. 
	 * 
	 */
	boolean loaded;

	public FXBrowser() {
		
		state = State.SCHEDULED;
		
		Platform.runLater(() -> {
			webView = new WebView();
			webEngine = webView.getEngine();
			history = webEngine.getHistory();
			setScene(new Scene(webView));
			webEngine.getLoadWorker().stateProperty().addListener(FXBrowser.this);
		});

	}

	/**
	 * Set a URL On the Web Engine to load it. 
	 * 
	 * @param url of a Website. 
	 */
	public void setURL(final String url) {
		Platform.runLater(() -> {
			webEngine.load(url);
		});
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Set the Zoom Level of the Page. Default is 1, need to test it.  
	 * 
	 * @param zoom Level, Integer value 
	 */
	public void setZoom(int zoom) {
			webView.setZoom(1.25);
	}

	/**
	 * Change Font size of Web Page, Default is 1, need to test it.  
	 * 
	 * @param font Size Integer value. 
	 */
	public void fontScale(int font) {
			webView.setFontScale(1.25);
	}

	/**
	 * Go to Previous visited Page, need to test it, need to test it.  
	 */
	public void prePage() {
			history.go(-1);
	}

	/**
	 * Go to Next Page, need to test it., need to test it.  
	 */
	public void nextPage() {
			history.go(1);
	}

	/**
	 * Get Document Object Model from the Web Engine. 
	 * 
	 * @return Document. 
	 */
	private Document getDocument() {
		return webEngine.getDocument();
	}

	/**
	 * Get the HTML of the WebPage. 
	 * 
	 * @return String which contains HTML of WebPage. 
	 * @throws Exception 
	 */
	public String getBrowserHTML() throws Exception {
		
		String result = null;
		
		waitfor();

		try {
			document = getDocument();
			result = getStringFromDocument(document);
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return result == null ? "" : result;
	}
	
	/**
	 * Wait for get the HTML
	 * TODO complete method description. 
	 * 
	 */
	private void waitfor() {
		
		int count = 0;
		//cannot get the html until the html is loaded successfully, if failed or cancel we will not get complete html. 
		while (getWorkerState() != State.SUCCEEDED) {
			
			//wait 1 sec
			try {
				Thread.sleep(PAGE_DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//After 15sec break the Loop
			if(++count > STOP_CHECK)
				break;
			
		}
	}
	
	/**
	 * Get the Current URL of the Page. 
	 * 
	 * @return String which will be current URL of the Page. 
	 */
	public String getCurrentURL() {
		return webEngine.getLocation();
	}
	
	/**
	 * Execute the JavaScript on the Page. 
	 * 
	 * e.g
	 * 
	 * function updateHello(user){
  	 * document.getElementById("helloprompt").innerHTML="Hello: " + user;
	 * }
	 * 
	 * executeJavascript( " updateHello(Robert) " );
	 * 
	 * @param function Name of the JavaScript Function. 
	 * @return String 
	 * @throws Exception Cannot Run the Script if the Page is not loaded fully. 
	 */
	public String executeJavascript(String function) throws Exception {
		
		if (getWorkerState() != State.SUCCEEDED 
				&& getWorkerState() != State.FAILED
				&& getWorkerState() != State.CANCELLED) {
			throw new Exception("Web Page is not Loaded fully. ");
		}
		
		return (String) webEngine.executeScript(function);
	}

	/**
	 * Convert Web Document to String, need to flush the writer to release the memory. 
	 * 
	 * @param doc Web Document. 
	 * @return String HTML. 
	 */
	private String getStringFromDocument(Document doc) {
		
		Platform.runLater(() -> {
			try {
				html = null;
				loaded = false;
				DOMSource domSource = new DOMSource(doc);
				StringWriter writer = new StringWriter();
				StreamResult result = new StreamResult(writer);
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				transformer.transform(domSource, result);
				html = writer.toString();
				writer.flush();
			} catch (TransformerException ex) {
				ex.printStackTrace();
			}finally {
				loaded = true;
			}
		});
		
		while(!loaded) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		return html;
	}
	
	public State getWorkerState() {
		return state;
	}

	/**
	 * Change Listener, Listen to Engine Worker State, e.g Running, Complete ...
	 */
	@Override
	public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
		System.out.println("oldValue: " + oldValue);
		System.out.println("newValue: " + newValue);

		state = newValue;

		if (newValue == Worker.State.SUCCEEDED) {

		}

		//TODO can implement progress bar or wait cursor. 
	}

}
