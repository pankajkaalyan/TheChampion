package com.springboot.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Participant {

	private long participantId;
	private String participantName;
	private String participantCountry;
	private int participantAge;
	private String email;
	
	public Participant(String participantName, String participantCountry, int participantAge, String email) {
		super();
		this.participantName = participantName;
		this.participantCountry = participantCountry;
		this.participantAge = participantAge;
		this.email = email;
	}
	
	
}
