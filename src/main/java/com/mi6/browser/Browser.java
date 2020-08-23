package com.mi6.browser;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import javafx.concurrent.Worker.State;

/**
 * A Simple Swing Component to Show Web Contents It use Java FX Webview Class to
 * show the content.
 * 
 * @author Mi6 
 *
 */
public class Browser extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private FXBrowser view;

	public Browser() {
		initCompoments();
		initLayout();
	}

	private void initCompoments() {
		view = new FXBrowser();
	}

	private void initLayout() {
		setLayout(new BorderLayout());
		add(view, BorderLayout.CENTER);
	}

	public void setUrl(String url) {
		view.setURL(url);
	}

	public String getHtml() {
		try {
			return view.getBrowserHTML();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public State getWorkerState() {
		return view.getWorkerState();
	}
	
	/**
	 * Access all the View Method from this Object, 
	 * e.g Current URL, Zoom in, Zoom out
	 * @return
	 */
	public FXBrowser getView() {
		return view;
	}
	
	/**
	 * Create new instance of the Browser set the URL. 
	 * Wait until the page loaded successfully, then get the HTML in String.
	 *  
	 * @param url of the Webpage. 
	 * @return String HTML of the Webpage. 
	 */
	public String get(String url) {
		setUrl(url);
		return getHtml();
	}

	public static void main(String[] args) {

		String url = "http://www.sia.ch/fr/affiliation/liste-des-membres/membres-etudiants/m/236019/";
		//String url = "http://stackoverflow.com";

		Browser comp = new Browser();
		/*
		 * JFrame frame = new JFrame(); frame.setTitle("A Simple Web Browser");
		 * frame.add(comp); frame.setSize(640, 480); frame.setLocationRelativeTo(null);
		 */

		comp.setUrl(url);
		//frame.setVisible(true);
		
		String html = comp.getHtml();
		
		System.err.println(html);
		
		System.err.println(comp.getView().getCurrentURL());

	}
}
