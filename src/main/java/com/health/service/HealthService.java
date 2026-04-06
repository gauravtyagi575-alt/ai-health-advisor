package com.health.service;

import org.springframework.stereotype.Service;

@Service
public class HealthService {

	public double calculateBMI(double height, double weight) {
		if(height <= 0 || weight <= 0) {
			return 0;
		}
		double h = height / 100.0;
		return weight / (h * h);
	}
	
	public String getBMICategory(double bmi) {
		if(bmi < 18.5)return "UnderWeight";
		else if(bmi < 24.4)return "Normal";
		else if(bmi < 29.9)return "OverWeight";
		else return "Obese";
	}
}
