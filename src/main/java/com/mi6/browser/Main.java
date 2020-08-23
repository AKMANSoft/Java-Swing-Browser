package com.mi6.browser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
	public static void main(String[] args) {

		try{

			ExecutorService executor = Executors.newCachedThreadPool();
			Runnable r3 = new Runnable() {
				public void run() {
					try{

						Browser b = new Browser();
						while(1==1){
							Thread.sleep(500);
							System.out.println(b.get("https://www.free.fr"));
						}


					}catch(Throwable e){
						e.printStackTrace();
					}

				}
			};
			executor.submit(r3);


		}catch(Throwable e){
			e.printStackTrace();
			System.out.println(e.toString());
		}

	}
}
