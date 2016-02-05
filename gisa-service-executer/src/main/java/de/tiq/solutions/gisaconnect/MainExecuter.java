package de.tiq.solutions.gisaconnect;

import java.util.Scanner;

import de.tiq.solutions.gisaconnect.basics.ServiceExecuter;

public class MainExecuter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Welcome to the Executer type Archive or LM to select subjected service");
		System.out.println("Service Name ?: ");
		Scanner scanner = new Scanner(System.in);
		String service = scanner.nextLine();
		scanner.close();
		ServiceExecuter impl;
		System.out.printf("You supplied Service is %s ", service);
		if (service.equalsIgnoreCase("Archive")) {

			impl = new de.tiq.solutions.archive.Main();

		} else {

			impl = new de.tiq.solutions.livemonitoring.Main();
		}

		impl.main(args);

	}

}
